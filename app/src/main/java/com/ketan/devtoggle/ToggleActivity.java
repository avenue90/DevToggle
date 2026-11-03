package com.ketan.devtoggle;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public final class ToggleActivity extends Activity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        String message;
        if (!DebugSettings.permitted(this)) message = DebugSettings.GRANT;
        else {
            boolean on = !DebugSettings.enabled(this);
            try { DebugSettings.set(this, on); message = on ? "Wireless debugging ON" : "Developer options OFF (banking apps OK)"; }
            catch (SecurityException e) { message = "Blocked by the system: " + e.getMessage(); }
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }
}
