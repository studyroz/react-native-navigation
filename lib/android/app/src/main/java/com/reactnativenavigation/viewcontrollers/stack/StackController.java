package com.reactnativenavigation.viewcontrollers.stack;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.reactnativenavigation.anim.NavigationAnimator;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.presentation.Presenter;
import com.reactnativenavigation.presentation.StackPresenter;
import com.reactnativenavigation.react.Constants;
import com.reactnativenavigation.utils.CommandListener;
import com.reactnativenavigation.utils.CommandListenerAdapter;
import com.reactnativenavigation.viewcontrollers.ChildControllersRegistry;
import com.reactnativenavigation.viewcontrollers.IdStack;
import com.reactnativenavigation.viewcontrollers.ParentController;
import com.reactnativenavigation.viewcontrollers.ViewController;
import com.reactnativenavigation.views.Component;
import com.reactnativenavigation.views.ReactComponent;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.reactnativenavigation.utils.CollectionUtils.*;

public class StackController extends ParentController<RelativeLayout> {

    private IdStack<ViewController> stack = new IdStack<>();
    private final NavigationAnimator animator;
    private BackButtonHelper backButtonHelper;
    public final StackPresenter presenter;
    private boolean didPush = true;

    public StackController(Activity activity, List<ViewController> children, ChildControllersRegistry childRegistry, NavigationAnimator animator, String id, Options initialOptions, BackButtonHelper backButtonHelper, StackPresenter stackPresenter, Presenter presenter) {
        super(activity, childRegistry, id, presenter, initialOptions);
        this.animator = animator;
        this.backButtonHelper = backButtonHelper;
        this.presenter = stackPresenter;
        stackPresenter.setButtonOnClickListener(this::onNavigationButtonPressed);
        for (ViewController child : children) {
            stack.push(child.getId(), child);
            child.setParentController(this);
            if (size() > 1) backButtonHelper.addToPushedChild(child);
        }
    }

    @Override
    public boolean isRendered() {
        if (isEmpty()) return false;
        if (getCurrentChild().isDestroyed()) return false;
        ViewGroup currentChild = getCurrentChild().getView();
        if (currentChild instanceof Component) {
            return super.isRendered() && presenter.isRendered((Component) currentChild);
        }
        return super.isRendered();
    }

    @Override
    public void setDefaultOptions(Options defaultOptions) {
        super.setDefaultOptions(defaultOptions);
        presenter.setDefaultOptions(defaultOptions);
    }

    @Override
    protected ViewController getCurrentChild() {
        return stack.peek();
    }

    @Override
    public void onAttachToParent() {
        if (!isEmpty() && !getCurrentChild().isDestroyed() && !isViewShown()) {
            presenter.applyChildOptions(resolveCurrentOptions(), (Component) getCurrentChild().getView());
        }
    }

    @Override
    public void mergeOptions(Options options) {
        presenter.mergeOptions(options, (Component) getCurrentChild().getView());
        super.mergeOptions(options);
    }

    @Override
    public void applyChildOptions(Options options, Component child) {
        super.applyChildOptions(options, child);
        presenter.applyChildOptions(resolveCurrentOptions(), child);
        if (child instanceof ReactComponent) {
            fabOptionsPresenter.applyOptions(this.options.fabOptions, (ReactComponent) child, getView());
        }
        performOnParentController(parentController ->
                ((ParentController) parentController).applyChildOptions(
                        this.options.copy()
                                .clearTopBarOptions()
                                .clearAnimationOptions()
                                .clearFabOptions()
                                .clearTopTabOptions()
                                .clearTopTabsOptions(),
                        child
                )
        );

    }

    @Override
    public void mergeChildOptions(Options options, ViewController childController, Component child) {
        super.mergeChildOptions(options, childController, child);
        if (childController.isViewShown() && peek() == childController) {
            presenter.mergeChildOptions(options, resolveCurrentOptions(), child);
            if (options.fabOptions.hasValue() && child instanceof ReactComponent) {
                fabOptionsPresenter.mergeOptions(options.fabOptions, (ReactComponent) child, getView());
            }
        }
        performOnParentController(parentController ->
                ((ParentController) parentController).mergeChildOptions(
                        options.copy()
                                .clearTopBarOptions()
                                .clearAnimationOptions()
                                .clearFabOptions()
                                .clearTopTabOptions()
                                .clearTopTabsOptions(),
                        childController,
                        child
                )
        );
    }

    @Override
    public void onChildDestroyed(Component child) {
        super.onChildDestroyed(child);
        presenter.onChildDestroyed(child);
    }

    private void updatePresenter(ViewController child) {
        if (child instanceof StackComponentController) {
            StackComponentController componentController = (StackComponentController) child;
            presenter.bindView(componentController.getTopBarController().getView());
        }
    }

    public void push(ViewController child, CommandListener listener) {
        updatePresenter(child);
        child.setParentController(this);

        didPush = false;
        final ViewController toRemove = stack.peek();
        if (size() > 0) backButtonHelper.addToPushedChild(child);
        stack.push(child.getId(), child);
        Options resolvedOptions = resolveCurrentOptions(presenter.getDefaultOptions());
        addChildToStack(child, child.getView(), resolvedOptions);

        if (toRemove != null) {
            if (resolvedOptions.animations.push.enabled.isTrueOrUndefined()) {
                if (resolvedOptions.animations.push.waitForRender.isTrue()) {
                    child.getView().setAlpha(0);
                    child.addOnAppearedListener(() -> animator.push(child.getView(), resolvedOptions.animations.push, resolvedOptions.transitions, toRemove.getElements(), child.getElements(), () -> {
                        getView().removeView(toRemove.getView());
                        listener.onSuccess(child.getId());
                        didPush = true;
                    }));
                } else {
                    animator.push(child.getView(), resolvedOptions.animations.push, () -> {
                        if (!toRemove.equals(peek())) {
                            getView().removeView(toRemove.getView());
                        }
                        listener.onSuccess(child.getId());
                        didPush = true;
                    });
                }
            } else {
                getView().removeView(toRemove.getView());
                listener.onSuccess(child.getId());
                didPush = true;
            }
        } else {
            listener.onSuccess(child.getId());
            didPush = true;
        }
    }

    private void addChildToStack(ViewController child, View view, Options resolvedOptions) {
        view.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        child.setWaitForRender(resolvedOptions.animations.push.waitForRender);
        presenter.applyLayoutParamsOptions(resolvedOptions, view);
        if (size() == 1) presenter.applyInitialChildLayoutOptions(resolvedOptions);
        getView().addView(view);
    }

    public void setRoot(List<ViewController> children, CommandListener listener) {
        animator.cancelPushAnimations();
        IdStack stackToDestroy = stack;
        stack = new IdStack<>();
        if (children.size() == 1) {
            backButtonHelper.clear(last(children));
            push(last(children), new CommandListenerAdapter() {
                @Override
                public void onSuccess(String childId) {
                    destroyStack(stackToDestroy);
                    listener.onSuccess(childId);
                }
            });
        } else {
            push(last(children), new CommandListenerAdapter() {
                @Override
                public void onSuccess(String childId) {
                    destroyStack(stackToDestroy);
                    for (int i = 0; i < children.size() - 1; i++) {
                        stack.set(children.get(i).getId(), children.get(i), i);
                        children.get(i).setParentController(StackController.this);
                        if (i == 0) {
                            backButtonHelper.clear(children.get(i));
                        } else {
                            backButtonHelper.addToPushedChild(children.get(i));
                        }
                    }
                    listener.onSuccess(childId);
                }
            });
        }
    }

    private void destroyStack(IdStack stack) {
        for (String s : (Iterable<String>) stack) {
            ((ViewController) stack.get(s)).destroy();
        }
    }

    public void pop(Options mergeOptions, CommandListener listener) {
        if (!canPop()) {
            listener.onError("Nothing to pop");
            return;
        }

        peek().mergeOptions(mergeOptions);
        Options disappearingOptions = resolveCurrentOptions(presenter.getDefaultOptions());

        final ViewController disappearing = stack.pop();
        final ViewController appearing = stack.peek();
        updatePresenter(appearing);

        disappearing.onViewWillDisappear();
        appearing.onViewWillAppear();

        Options resolvedOptions = resolveCurrentOptions();
        ViewGroup appearingView = appearing.getView();
        if (appearingView.getLayoutParams() == null) {
            appearingView.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            presenter.applyLayoutParamsOptions(resolvedOptions, appearingView);
        }
        if (appearingView.getParent() == null) {
            getView().addView(appearingView, 0);
        }
        presenter.onChildWillAppear(appearing.options, disappearing.options);
        if (disappearingOptions.animations.pop.enabled.isTrueOrUndefined()) {
            animator.pop(disappearing.getView(), disappearingOptions.animations.pop, () -> finishPopping(disappearing, listener));
        } else {
            finishPopping(disappearing, listener);
        }
    }

    private void finishPopping(ViewController disappearing, CommandListener listener) {
        disappearing.destroy();
        listener.onSuccess(disappearing.getId());
    }

    public void popTo(ViewController viewController, Options mergeOptions, CommandListener listener) {
        if (!stack.containsId(viewController.getId()) || peek().equals(viewController)) {
            listener.onError("Nothing to pop");
            return;
        }

        animator.cancelPushAnimations();
        String currentControlId;
        for (int i = stack.size() - 2; i >= 0; i--) {
            currentControlId = stack.get(i).getId();
            if (currentControlId.equals(viewController.getId())) {
                break;
            }

            ViewController controller = stack.get(currentControlId);
            stack.remove(controller.getId());
            controller.destroy();
        }

        pop(mergeOptions, listener);
    }

    public void popToRoot(Options mergeOptions, CommandListener listener) {
        if (!canPop()) {
            listener.onError("Nothing to pop");
            return;
        }

        animator.cancelPushAnimations();
        Iterator<String> iterator = stack.iterator();
        iterator.next();
        while (stack.size() > 2) {
            ViewController controller = stack.get(iterator.next());
            if (!stack.isTop(controller.getId())) {
                stack.remove(iterator, controller.getId());
                controller.destroy();
            }
        }

        pop(mergeOptions, listener);
    }

    ViewController peek() {
        return stack.peek();
    }

    public int size() {
        return stack.size();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public boolean handleBack(CommandListener listener) {
        if (canPop()) {
            pop(Options.EMPTY, listener);
            return true;
        }
        return false;
    }

    @VisibleForTesting()
    boolean canPop() {
        return stack.size() > 1;
    }

    @NonNull
    @Override
    protected RelativeLayout createView() {
        RelativeLayout stackLayout = new RelativeLayout(getActivity());
        addInitialChild(stackLayout);
        return stackLayout;
    }

    private void addInitialChild(RelativeLayout stackLayout) {
        if (isEmpty()) return;
        ViewGroup child = peek().getView();
        child.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        Options options = resolveCurrentOptions();
        presenter.applyLayoutParamsOptions(options, child);
        if (peek() instanceof StackComponentController) {
            StackComponentController controller = (StackComponentController) peek();
            presenter.bindView(controller.getTopBarController().getView());
            presenter.applyInitialChildLayoutOptions(options);
        }
        stackLayout.addView(child, 0);
    }

    private void onNavigationButtonPressed(String buttonId) {
        if (!didPush) {
            return;
        }
        if (Constants.BACK_BUTTON_ID.equals(buttonId)) {
            pop(Options.EMPTY, new CommandListenerAdapter());
        } else {
            sendOnNavigationButtonPressed(buttonId);
        }
    }

    @Override
    public void sendOnNavigationButtonPressed(String buttonId) {
        peek().sendOnNavigationButtonPressed(buttonId);
    }

    @NonNull
    @Override
    public Collection<ViewController> getChildControllers() {
        return stack.values();
    }

    @Override
    public void setupTopTabsWithViewPager(ViewPager viewPager) {
//        topBarController.initTopTabs(viewPager);
    }

    @Override
    public void clearTopTabs() {
//        topBarController.clearTopTabs();
    }

//    @RestrictTo(RestrictTo.Scope.TESTS)
//    public TopBar getTopBar() {
//        return topBarController.getView();
//    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    public RelativeLayout getStackLayout() {
        return getView();
    }
}
