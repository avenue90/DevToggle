package com.ketan.devtoggle;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

/** Developer options + USB debugging + wireless debugging, switched together. Needs
 * WRITE_SECURE_SETTINGS (granted once over adb; survives reboots and app updates). */
final class DebugSettings {
    private static final String DEV = Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, ADB = Settings.Global.ADB_ENABLED, WIFI = "adb_wifi_enabled";

    static boolean permitted(Context c) {
        return c.checkSelfPermission(android.Manifest.permission.WRITE_SECURE_SETTINGS) == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }

    static boolean enabled(Context c) {
        ContentResolver r = c.getContentResolver();
        return Settings.Global.getInt(r, DEV, 0) == 1 && Settings.Global.getInt(r, WIFI, 0) == 1;
    }

    static void set(Context c, boolean on) {
        ContentResolver r = c.getContentResolver();
        if (on) { // Developer options must be on before debugging can be.
            Settings.Global.putInt(r, DEV, 1); Settings.Global.putInt(r, ADB, 1); Settings.Global.putInt(r, WIFI, 1);
        } else {  // Reverse order: what banking apps check is developer options and USB debugging.
            Settings.Global.putInt(r, WIFI, 0); Settings.Global.putInt(r, ADB, 0); Settings.Global.putInt(r, DEV, 0);
        }
    }

    static final String GRANT = "Grant once from the Mac:\nadb shell pm grant com.ketan.devtoggle android.permission.WRITE_SECURE_SETTINGS";
}
