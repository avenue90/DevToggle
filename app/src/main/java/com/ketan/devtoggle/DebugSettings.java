package com.ketan.devtoggle;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

/** Developer options + USB debugging (+ wireless debugging in Wireless mode), switched together.
 * Needs WRITE_SECURE_SETTINGS (granted once over adb; survives reboots and app updates). */
final class DebugSettings {
    private static final String DEV = Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, ADB = Settings.Global.ADB_ENABLED, WIFI = "adb_wifi_enabled";
    static final String MODE_WIRELESS = "wireless", MODE_USB = "usb";

    static SharedPreferences prefs(Context c) { return c.getSharedPreferences("devtoggle", Context.MODE_PRIVATE); }
    static String mode(Context c) { return prefs(c).getString("mode", MODE_WIRELESS); }
    static boolean wireless(Context c) { return MODE_WIRELESS.equals(mode(c)); }

    static boolean permitted(Context c) {
        return c.checkSelfPermission(android.Manifest.permission.WRITE_SECURE_SETTINGS) == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }

    /** "On" means what the chosen mode turns on: USB debugging, plus wireless debugging in Wireless mode. */
    static boolean enabled(Context c) {
        ContentResolver r = c.getContentResolver();
        boolean usb = Settings.Global.getInt(r, DEV, 0) == 1 && Settings.Global.getInt(r, ADB, 0) == 1;
        return usb && (!wireless(c) || Settings.Global.getInt(r, WIFI, 0) == 1);
    }

    static void set(Context c, boolean on) {
        ContentResolver r = c.getContentResolver();
        if (on) { // Developer options must be on before debugging can be.
            Settings.Global.putInt(r, DEV, 1); Settings.Global.putInt(r, ADB, 1); Settings.Global.putInt(r, WIFI, wireless(c) ? 1 : 0);
        } else {  // Reverse order: what banking apps check is developer options and USB debugging.
            Settings.Global.putInt(r, WIFI, 0); Settings.Global.putInt(r, ADB, 0); Settings.Global.putInt(r, DEV, 0);
        }
        StatusWidget.updateAll(c);
    }

    /** Applies a new mode immediately when debugging is already on. */
    static void setMode(Context c, String mode) {
        boolean wasOn = enabled(c);
        prefs(c).edit().putString("mode", mode).apply();
        if (wasOn && permitted(c)) Settings.Global.putInt(c.getContentResolver(), WIFI, MODE_WIRELESS.equals(mode) ? 1 : 0);
        StatusWidget.updateAll(c);
    }

    static boolean toggle(Context c) { boolean on = !enabled(c); set(c, on); return on; }

    static final String GRANT = "Grant once from the Mac:\nadb shell pm grant com.ketan.devtoggle android.permission.WRITE_SECURE_SETTINGS";
}
