package com.reactnativenavigation.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.reactnativenavigation.utils.UiUtils;
import com.reactnativenavigation.viewcontrollers.topbar.TopBarController;
import com.reactnativenavigation.views.topbar.TopBar;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

@SuppressLint("ViewConstructor")
public class StackLayout extends RelativeLayout implements Component {
    private String stackId;
    private Component child;

    public StackLayout(Context context, TopBarController topBarController, String stackId, Component child) {
        super(context);
        this.child = child;
        this.stackId = stackId;
        createLayout(topBarController);
        setContentDescription("StackLayout");
    }

    private void createLayout(TopBarController topBarController) {
        addView(topBarController.createView(getContext(), this),
                MATCH_PARENT,
                UiUtils.getTopBarHeight(getContext())
        );
    }

    public String getStackId() {
        return stackId;
    }

    @Override
    public void drawBehindTopBar() {
        child.drawBehindTopBar();
    }

    @Override
    public void drawBelowTopBar(TopBar topBar) {
        child.drawBelowTopBar(topBar);
    }

    @Override
    public boolean isRendered() {
        return getChildCount() >= 2 &&
                getChildAt(1) instanceof Component &&
                ((Component) getChildAt(1)).isRendered();
    }
}
