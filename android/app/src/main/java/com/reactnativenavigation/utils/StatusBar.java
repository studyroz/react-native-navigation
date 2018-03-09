package com.reactnativenavigation.utils;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.reactnativenavigation.params.StatusBarTextColorScheme;
import com.reactnativenavigation.params.StyleParams;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StatusBar {

    public static void setHidden(Window window, boolean statusBarHidden) {
        if (statusBarHidden) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setColor(Window window, StyleParams.Color statusBarColor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;
        if (statusBarColor.hasColor()) {
            window.setStatusBarColor(statusBarColor.getColor());
        } else {
            window.setStatusBarColor(Color.BLACK);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void displayOverScreen(View view, boolean shouldDisplay) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;

        if(shouldDisplay) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            view.setSystemUiVisibility(flags);
        } else {
            int flags = view.getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            flags &= ~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            view.setSystemUiVisibility(flags);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void setTextColorScheme(Window window, StatusBarTextColorScheme textColorScheme) {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;
//        if (StatusBarTextColorScheme.Dark.equals(textColorScheme)) {
//            int flags = view.getSystemUiVisibility();
//            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
//            view.setSystemUiVisibility(flags);
//        } else {
//            clearLightStatusBar(view);
//        }
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            return;
//        }

        final View view = window.getDecorView();
        final boolean isDark = StatusBarTextColorScheme.Dark.equals(textColorScheme);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isDark){
                int flags = view.getSystemUiVisibility();
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                view.setSystemUiVisibility(flags);
            } else {
                clearLightStatusBar(view);
            }
        }

        // 处理小米和魅族的情况，其机型在4.4之前有定制API接口修改状态栏字体颜色
        MIUISetStatusBarLightMode(window, isDark);
        FlymeSetStatusBarLightMode(window, isDark);
    }

    private static void clearLightStatusBar(View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;
        int flags = view.getSystemUiVisibility();
        flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        view.setSystemUiVisibility(flags);
    }


    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }
}
