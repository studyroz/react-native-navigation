package com.reactnativenavigation.controllers;

import android.content.*;
import android.os.*;

import com.facebook.react.bridge.*;
import com.reactnativenavigation.*;
import com.reactnativenavigation.params.*;
import com.reactnativenavigation.params.parsers.*;
import com.reactnativenavigation.react.*;
import com.reactnativenavigation.utils.*;
import com.reactnativenavigation.views.SideMenu.*;

import java.util.*;

public class NavigationCommandsHandler {

    private static final String ACTIVITY_PARAMS_BUNDLE = "ACTIVITY_PARAMS_BUNDLE";

    static ActivityParams parseActivityParams(Intent intent) {
        return ActivityParamsParser.parse(intent.getBundleExtra(NavigationCommandsHandler.ACTIVITY_PARAMS_BUNDLE));
    }

    public static void startApp(Bundle params, Promise promise) {
        Intent intent = new Intent(NavigationApplication.instance, NavigationActivity.class);
        IntentDataHandler.onStartApp(intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ACTIVITY_PARAMS_BUNDLE, params);
        intent.putExtra("animationType", params.getString("animationType"));
        NavigationActivity.setStartAppPromise(promise);
        NavigationApplication.instance.startActivity(intent);
    }

    public static void push(final Bundle screenParams, final Promise onPushComplete) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }
        final boolean useNewAct = screenParams.getBoolean("startNewActivity",false);
        final ScreenParams params = ScreenParamsParser.parse(screenParams);
        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if(!useNewAct) {
                    currentActivity.push(params, onPushComplete);
                }else {
                    String animationType = screenParams.getString("animationType");
                    Intent intent = new Intent(NavigationApplication.instance, NavigationActivity.class);
                    IntentDataHandler.onStartApp(intent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();

                    Bundle innerBundle = new Bundle();
                    // 传入AppStyle中的方向参数， StyleParamsParser.java getDefaultOrientation方法
                    innerBundle.putCharSequence("orientation", AppStyle.appStyle == null ? null : AppStyle.appStyle.orientation.name);
                    bundle.putBundle("appStyle", innerBundle);
                    
                    bundle.putBundle("screen", screenParams);
                    intent.putExtra(ACTIVITY_PARAMS_BUNDLE, bundle);
                    intent.putExtra("animationType", screenParams.getString("animationType"));
                    NavigationApplication.instance.startActivity(intent);
                    if(animationType != null && "slide-horizontal".equals(animationType)){
                        currentActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.fade_out);
                    }

                }
            }
        });
    }

    public static void pop(Bundle screenParams) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        final ScreenParams params = ScreenParamsParser.parse(screenParams);
        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.pop(params);
            }
        });
    }

    public static void popToRoot(Bundle screenParams) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        final ScreenParams params = ScreenParamsParser.parse(screenParams);
        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.popToRoot(params);
            }
        });
    }

    public static void newStack(Bundle screenParams) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        final ScreenParams params = ScreenParamsParser.parse(screenParams);
        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.newStack(params);
            }
        });
    }

    public static void setTopBarVisible(final String screenInstanceID, final boolean hidden, final boolean animated) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setTopBarVisible(screenInstanceID, hidden, animated);
            }
        });
    }

    public static void setBottomTabsVisible(final boolean hidden, final boolean animated) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setBottomTabsVisible(hidden, animated);
            }
        });
    }

    public static void setScreenTitleBarTitle(final String screenInstanceId, final String title) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setTitleBarTitle(screenInstanceId, title);
            }
        });
    }

    public static void setScreenTitleBarSubtitle(final String screenInstanceId, final String subtitle) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setTitleBarSubtitle(screenInstanceId, subtitle);
            }
        });
    }

    public static void showModal(final Bundle params) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.showModal(ScreenParamsParser.parse(params));
            }
        });
    }

    public static void showLightBox(final LightBoxParams params) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.showLightBox(params);
            }
        });
    }

    public static void dismissLightBox() {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.dismissLightBox();
            }
        });
    }

    public static void setScreenTitleBarRightButtons(final String screenInstanceId,
                                                     final String navigatorEventId,
                                                     final List<TitleBarButtonParams> titleBarButtons) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setTitleBarButtons(screenInstanceId, navigatorEventId, titleBarButtons);
            }
        });
    }

    public static void setScreenTitleBarLeftButtons(final String screenInstanceId,
                                                    final String navigatorEventId,
                                                    final TitleBarLeftButtonParams titleBarButtons) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setTitleBarLeftButton(screenInstanceId, navigatorEventId, titleBarButtons);
            }
        });
    }

    public static void setScreenFab(final String screenInstanceId, final String navigatorEventId, final FabParams fab) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }
        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setScreenFab(screenInstanceId, navigatorEventId, fab);
            }
        });
    }

    public static void setScreenStyle(final String screenInstanceId, final Bundle styleParams) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }
        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setScreenStyle(screenInstanceId, styleParams);
            }
        });
    }

    public static void dismissTopModal(final ScreenParams params, final Promise promise) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.dismissTopModal(params);
                promise.resolve("true");
            }
        });
    }

    public static void dismissAllModals(final Promise promise) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.dismissAllModals();
                promise.resolve("true");
            }
        });
    }

    public static void toggleSideMenuVisible(final boolean animated, final Side side) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.toggleSideMenuVisible(animated, side);
            }
        });
    }

    public static void setSideMenuVisible(final boolean animated, final boolean visible, final Side side) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setSideMenuVisible(animated, visible, side);
            }
        });
    }

    public static void setSideMenuEnabled(final boolean enabled, final Side side) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setSideMenuEnabled(enabled, side);
            }
        });
    }

    public static void selectTopTabByTabIndex(final String screenInstanceId, final int index) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.selectTopTabByTabIndex(screenInstanceId, index);
            }
        });
    }

    public static void selectTopTabByScreen(final String screenInstanceId) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }
        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.selectTopTabByScreen(screenInstanceId);
            }
        });
    }

    public static void selectBottomTabByTabIndex(final Integer index) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.selectBottomTabByTabIndex(index);
            }
        });
    }

    public static void selectBottomTabByNavigatorId(final String navigatorId) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.selectBottomTabByNavigatorId(navigatorId);
            }
        });
    }

    public static void setBottomTabBadgeByIndex(final Integer index, final String badge) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setBottomTabBadgeByIndex(index, badge);
            }
        });
    }

    public static void setBottomTabBadgeByNavigatorId(final String navigatorId, final String badge) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setBottomTabBadgeByNavigatorId(navigatorId, badge);
            }
        });
    }

    public static void setBottomTabButtonByIndex(final Integer index, final Bundle screenParams) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        final ScreenParams params = ScreenParamsParser.parse(screenParams);
        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setBottomTabButtonByIndex(index, params);
            }
        });
    }

    public static void setBottomTabButtonByNavigatorId(final String navigatorId, final Bundle screenParams) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        final ScreenParams params = ScreenParamsParser.parse(screenParams);
        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setBottomTabButtonByNavigatorId(navigatorId, params);
            }
        });
    }

    public static void showSlidingOverlay(final SlidingOverlayParams params) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.showSlidingOverlay(params);
            }
        });
    }

    public static void hideSlidingOverlay() {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.hideSlidingOverlay();
            }
        });
    }

    public static void showSnackbar(final SnackbarParams params) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.showSnackbar(params);
            }
        });
    }

    public static void showContextualMenu(final String screenInstanceId, final ContextualMenuParams params, final Callback onButtonClicked) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.showContextualMenu(screenInstanceId, params, onButtonClicked);
            }
        });
    }

    public static void dismissContextualMenu(final String screenInstanceId) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.dismissContextualMenu(screenInstanceId);
            }
        });
    }

    public static void dismissSnackbar() {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.dismissSnackbar();
            }
        });
    }

    public static void getOrientation(Promise promise) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }
        promise.resolve(OrientationHelper.getOrientation(currentActivity));
    }

    public static void isAppLaunched(Promise promise) {
        final boolean isAppLaunched = SplashActivity.isResumed || NavigationActivity.currentActivity != null;
        promise.resolve(isAppLaunched);
    }

    public static void isRootLaunched(Promise promise) {
        promise.resolve(NavigationActivity.currentActivity != null);
    }

    public static void getCurrentlyVisibleScreenId(final Promise promise) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            promise.resolve("");
            return;
        }
        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                WritableMap map = Arguments.createMap();
                map.putString("screenId", currentActivity.getCurrentlyVisibleScreenId());
                promise.resolve(map);
            }
        });
    }

    public static void getLaunchArgs(Promise promise) {
        Bundle bundle = LaunchArgs.instance.get();
        promise.resolve(Arguments.fromBundle(bundle));
    }
}
