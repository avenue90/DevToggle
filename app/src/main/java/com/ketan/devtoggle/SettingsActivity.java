package com.ketan.devtoggle;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

/** Settings: current state with an on/off button, and which debugging to turn on. */
public final class SettingsActivity extends Activity {
    private int bg, card, ink, muted, accent;
    private ImageView light; private TextView status; private Button toggle; private RadioButton wirelessChoice, usbChoice;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        boolean dark = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        bg = dark ? 0xFF121212 : 0xFFFFFFFF; card = dark ? 0xFF1F1F1F : 0xFFF2F2F2; ink = dark ? 0xFFFFFFFF : 0xFF0F0F0F;
        muted = dark ? 0xFFAAAAAA : 0xFF606060; accent = dark ? 0xFF3EA6FF : 0xFF065FD4;
        getWindow().setStatusBarColor(bg); getWindow().setNavigationBarColor(bg);
        if (!dark) getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        ScrollView scroll = new ScrollView(this); scroll.setBackgroundColor(bg); scroll.setFitsSystemWindows(true);
        LinearLayout page = column(); page.setPadding(dp(20), dp(16), dp(20), dp(24)); scroll.addView(page);
        page.addView(text("Debug toggle", 26, true, ink));

        LinearLayout statusCard = card(page); statusCard.setGravity(Gravity.CENTER_HORIZONTAL);
        light = new ImageView(this); statusCard.addView(light, new LinearLayout.LayoutParams(dp(72), dp(72)));
        status = text("", 20, true, ink); status.setGravity(Gravity.CENTER); status.setPadding(0, dp(10), 0, dp(14)); statusCard.addView(status);
        toggle = new Button(this); toggle.setAllCaps(false); toggle.setTextSize(16); toggle.setTypeface(Typeface.DEFAULT_BOLD);
        toggle.setOnClickListener(v -> {
            if (!DebugSettings.permitted(this)) { Toast.makeText(this, DebugSettings.GRANT, Toast.LENGTH_LONG).show(); return; }
            try { DebugSettings.toggle(this); } catch (SecurityException e) { Toast.makeText(this, "Blocked by the system: " + e.getMessage(), Toast.LENGTH_LONG).show(); }
            refresh();
        });
        statusCard.addView(toggle, new LinearLayout.LayoutParams(-1, dp(52)));

        page.addView(heading("When turned on"));
        LinearLayout modes = card(page);
        RadioGroup group = new RadioGroup(this);
        wirelessChoice = radio("Wireless debugging", "Developer options, USB debugging and wireless debugging");
        usbChoice = radio("USB debugging only", "Developer options and USB debugging; wireless stays off");
        group.addView(wirelessChoice); group.addView(usbChoice); modes.addView(group);
        wirelessChoice.setOnClickListener(v -> { DebugSettings.setMode(this, DebugSettings.MODE_WIRELESS); refresh(); });
        usbChoice.setOnClickListener(v -> { DebugSettings.setMode(this, DebugSettings.MODE_USB); refresh(); });
        TextView note = text("Turning off always switches off developer options, USB debugging and wireless debugging, so banking apps work.", 14, false, muted);
        note.setPadding(dp(4), dp(8), 0, 0); modes.addView(note);

        page.addView(heading("Shortcuts"));
        LinearLayout tips = card(page);
        tips.addView(text("• Tap the Debug toggle icon to switch instantly.\n• Add the Debug light widget to your home screen: it glows green while debugging is on.\n• Add the Quick Settings tile; long-press it to open this page.\n• Wireless debugging works only while the phone is on Wi-Fi.", 15, false, ink));

        if (!DebugSettings.permitted(this)) {
            page.addView(heading("Setup needed"));
            card(page).addView(text(DebugSettings.GRANT, 14, false, ink));
        }
        setContentView(scroll);
    }

    @Override protected void onResume() { super.onResume(); refresh(); }

    private void refresh() {
        boolean on = DebugSettings.enabled(this), wireless = DebugSettings.wireless(this);
        light.setImageResource(on ? R.drawable.light_on : R.drawable.light_off);
        status.setText(on ? (wireless ? "Wireless debugging is on" : "USB debugging is on") : "Debugging is off");
        toggle.setText(on ? "Turn off" : "Turn on");
        toggle.setTextColor(on ? ink : (accent == 0xFF3EA6FF ? 0xFF0F0F0F : Color.WHITE));
        toggle.setBackground(round(on ? card : accent, 26));
        if (on) toggle.setBackground(stroke(card, 26));
        wirelessChoice.setChecked(wireless); usbChoice.setChecked(!wireless);
        light.setContentDescription(on ? "Debugging on" : "Debugging off");
    }

    private RadioButton radio(String title, String detail) {
        RadioButton button = new RadioButton(this); button.setText(title + "\n" + detail); button.setTextColor(ink); button.setTextSize(15);
        button.setButtonTintList(android.content.res.ColorStateList.valueOf(accent)); button.setPadding(dp(8), dp(10), 0, dp(10)); return button;
    }
    private LinearLayout column() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l; }
    private LinearLayout card(LinearLayout parent) {
        LinearLayout c = column(); c.setPadding(dp(16), dp(16), dp(16), dp(16)); c.setBackground(round(card, 14));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2); p.topMargin = dp(10); parent.addView(c, p); return c;
    }
    private TextView heading(String value) { TextView t = text(value, 16, true, ink); t.setPadding(dp(4), dp(20), 0, 0); return t; }
    private TextView text(String value, int size, boolean bold, int color) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color); t.setLineSpacing(dp(2), 1); if (bold) t.setTypeface(Typeface.DEFAULT_BOLD); return t;
    }
    private GradientDrawable round(int color, int radius) { GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d; }
    private GradientDrawable stroke(int color, int radius) { GradientDrawable d = round(color, radius); d.setStroke(dp(2), muted); return d; }
    private int dp(int v) { return Math.round(v * getResources().getDisplayMetrics().density); }
}
