package com.reactnativenavigation.viewcontrollers.stack;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.parse.TopBarOptions;
import com.reactnativenavigation.presentation.ComponentPresenter;
import com.reactnativenavigation.presentation.Presenter;
import com.reactnativenavigation.presentation.StackPresenter;
import com.reactnativenavigation.react.Constants;
import com.reactnativenavigation.react.ReactView;
import com.reactnativenavigation.utils.CommandListenerAdapter;
import com.reactnativenavigation.viewcontrollers.ChildController;
import com.reactnativenavigation.viewcontrollers.ChildControllersRegistry;
import com.reactnativenavigation.viewcontrollers.IReactView;
import com.reactnativenavigation.viewcontrollers.ReactViewCreator;
import com.reactnativenavigation.viewcontrollers.topbar.TopBarController;
import com.reactnativenavigation.views.Component;
import com.reactnativenavigation.views.ComponentLayout;
import com.reactnativenavigation.views.StackLayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class StackComponentController extends ChildController<StackLayout> {
    private TopBarController topBarController;
    private final String componentName;
    private final ReactViewCreator viewCreator;
    private ComponentLayout child;
    private ComponentPresenter presenter;

    public StackComponentController(
            final Activity activity,
            final ChildControllersRegistry childRegistry,
            final String id,
            final String componentName,
            final ReactViewCreator viewCreator,
            final Options initialOptions,
            final Presenter presenter,
            final ComponentPresenter componentPresenter,
            final TopBarController topBarController) {
        super(activity, childRegistry, id, presenter, initialOptions);
        this.topBarController = topBarController;
        this.componentName = componentName;
        this.viewCreator = viewCreator;
        this.presenter = componentPresenter;
    }

    public TopBarController getTopBarController() {
        return topBarController;
    }

    @Override
    public void setDefaultOptions(Options defaultOptions) {
        super.setDefaultOptions(defaultOptions);
        presenter.setDefaultOptions(defaultOptions);
    }

    @Override
    public void onViewAppeared() {
        updatePresenter();
        super.onViewAppeared();
        if (child != null) {
            child.sendComponentStart();
        }
    }

    @Override
    public void onViewDisappear() {
        if (child != null) {
            child.sendComponentStop();
        }
        super.onViewDisappear();
    }

    @Override
    public void destroy() {
        if (child != null) {
            child.destroy();
        }
        super.destroy();
    }

    private void updatePresenter() {
        if (getParentController() instanceof StackController) {
            StackController stackController = (StackController) getParentController();
            StackPresenter presenter = stackController.presenter;
            presenter.bindView(topBarController.getView());
        }
    }

    @Override
    public void applyOptions(Options options) {
        super.applyOptions(options);
        if (child != null) {
            child.applyOptions(options);
        }
        presenter.applyOptions(child, resolveCurrentOptions(presenter.defaultOptions));
    }

    @Override
    public boolean isViewShown() {
        return super.isViewShown() && child != null && child.isReady();
    }

    @Override
    public void mergeOptions(Options options) {
        if (options == Options.EMPTY) return;
        super.mergeOptions(options);
        if (child != null) {
            presenter.mergeOptions(child, options);
        }
        performOnParentController(parentController -> parentController.mergeChildOptions(options, this, getView()));
    }

    @NonNull
    @Override
    protected StackLayout createView() {
        StackLayout stackLayout = new StackLayout(getActivity(), topBarController, getId());
        updatePresenter();

        ComponentLayout child = (ComponentLayout) viewCreator.create(getActivity(), getId(), componentName);
        stackLayout.setChild(child);
        child.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        Options options = resolveCurrentOptions(presenter.defaultOptions);
        child.applyOptions(options);
        setInitialTopBarVisibility(options.topBar);
        stackLayout.addView(child, 0);
        this.child = child;
        return stackLayout;
    }

    @Override
    public void sendOnNavigationButtonPressed(String buttonId) {
        if (child != null) {
            child.sendOnNavigationButtonPressed(buttonId);
        }
    }

    private void setInitialTopBarVisibility(TopBarOptions options) {
        if (options.visible.isFalse()) {
            topBarController.getView().hide();
        }
        if (options.visible.isTrueOrUndefined()) {
            topBarController.getView().show();
        }
    }
}
