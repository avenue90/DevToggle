package com.ketan.devtoggle;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/** Launcher icon: one tap toggles silently (the widget light and tile show the state). */
public final class ToggleActivity extends Activity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!DebugSettings.permitted(this)) Toast.makeText(this, DebugSettings.GRANT, Toast.LENGTH_LONG).show();
        else try { DebugSettings.toggle(this); }
        catch (SecurityException e) { Toast.makeText(this, "Blocked by the system: " + e.getMessage(), Toast.LENGTH_LONG).show(); }
        finish();
    }
}
