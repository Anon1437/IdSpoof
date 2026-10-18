package com.example.idspoof;

import android.content.ContentResolver;

import java.security.SecureRandom;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    private static final String SELF = "com.example.idspoof";
    private static final String HEX = "0123456789abcdef";

    private static String randomId() {
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append(HEX.charAt(r.nextInt(HEX.length())));
        }
        return sb.toString();
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (SELF.equals(lpparam.packageName)) return;

        // One ID per app process so the app sees a stable value while running
        final String fakeId = randomId();
        final String pkg = lpparam.packageName;

        XC_MethodHook hook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                if ("android_id".equals(param.args[1])) {
                    XposedBridge.log("[IdSpoof] " + pkg + " -> " + fakeId);
                    param.setResult(fakeId);
                }
                // any other setting falls through to the original method
            }
        };

        try {
            XposedHelpers.findAndHookMethod(
                    "android.provider.Settings$Secure", lpparam.classLoader,
                    "getString", ContentResolver.class, String.class, hook);
        } catch (Throwable t) {
            XposedBridge.log("[IdSpoof] getString hook failed: " + t);
        }

        try {
            XposedHelpers.findAndHookMethod(
                    "android.provider.Settings$Secure", lpparam.classLoader,
                    "getStringForUser", ContentResolver.class, String.class, int.class, hook);
        } catch (Throwable t) {
            XposedBridge.log("[IdSpoof] getStringForUser hook failed: " + t);
        }
    }
}
