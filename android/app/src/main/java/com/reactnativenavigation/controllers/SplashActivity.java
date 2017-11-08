package com.reactnativenavigation.controllers;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.reactnativenavigation.NavigationApplication;
import com.reactnativenavigation.react.ReactDevPermission;

public abstract class SplashActivity extends AppCompatActivity {
    private static final int MSG_INIT_REACT_CONTEXT = 2100;
    public static boolean isResumed = false;
    private Handler mHandler;
    private boolean mDelayTimeOut = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSplashLayout();
        IntentDataHandler.saveIntentData(getIntent());

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(isFinishing()) return;
                if(msg.what == MSG_INIT_REACT_CONTEXT) {
                    mDelayTimeOut = true;
                    if(!hasInitReady()){
                        return;
                    }
                    initReactContext();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        if (getDelayFinishTime() > 0) {
            mDelayTimeOut = false;
            mHandler.removeMessages(MSG_INIT_REACT_CONTEXT);
            mHandler.sendEmptyMessageDelayed(MSG_INIT_REACT_CONTEXT, getDelayFinishTime());
        } else if(hasInitReady()){
            initReactContext();
        }
    }

    private void initReactContext() {
        if (NavigationApplication.instance.getReactGateway().hasStartedCreatingContext()) {
            NavigationApplication.instance.getEventEmitter().sendAppLaunchedEvent();
            if (NavigationApplication.instance.clearHostOnActivityDestroy()) {
                overridePendingTransition(0, 0);
                finish();
            }
            return;
        }

        if (ReactDevPermission.shouldAskPermission()) {
            ReactDevPermission.askPermission(this);
            return;
        }

        if (NavigationApplication.instance.isReactContextInitialized()) {
            NavigationApplication.instance.getEventEmitter().sendAppLaunchedEvent();
            return;
        }

        // TODO I'm starting to think this entire flow is incorrect and should be done in Application
        NavigationApplication.instance.startReactContextOnceInBackgroundAndExecuteJS();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
    }

    private void setSplashLayout() {
        final int splashLayout = getSplashLayout();
        if (splashLayout > 0) {
            setContentView(splashLayout);
        } else {
            setContentView(createSplashLayout());
        }
    }

    /**
     * @return xml layout res id
     */
    @LayoutRes
    public int getSplashLayout() {
        return 0;
    }

    /**
     * @return the layout you would like to show while react's js context loads
     */
    public View createSplashLayout() {
        View view = new View(this);
        view.setBackgroundColor(Color.WHITE);
        return view;
    }

    protected int getDelayFinishTime() {
        return 0;
    }

    protected boolean hasInitReady(){
        return true;
    }

    protected void onInitReady(){
        if(!mDelayTimeOut) return;
        initReactContext();
    }
}
