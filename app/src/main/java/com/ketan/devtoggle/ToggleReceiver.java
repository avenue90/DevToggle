package com.ketan.devtoggle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/** Private (not exported): only this app's widget PendingIntent can reach it, so other apps can't toggle debugging. */
public final class ToggleReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (!DebugSettings.permitted(context)) { Toast.makeText(context, DebugSettings.GRANT, Toast.LENGTH_LONG).show(); return; }
        try { DebugSettings.toggle(context); }
        catch (SecurityException e) { Toast.makeText(context, "Blocked by the system: " + e.getMessage(), Toast.LENGTH_LONG).show(); }
    }
}
