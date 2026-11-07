package com.ketan.devtoggle;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/** Home-screen light: green when debugging is on, grey when off. Tap to toggle. */
public final class StatusWidget extends AppWidgetProvider {
    @Override public void onUpdate(Context context, AppWidgetManager manager, int[] ids) { render(context, manager, ids); }

    @Override public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
            updateAll(context);
        }
    }

    static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        render(context, manager, manager.getAppWidgetIds(new ComponentName(context, StatusWidget.class)));
    }

    private static void render(Context context, AppWidgetManager manager, int[] ids) {
        if (ids == null || ids.length == 0) return;
        boolean on = DebugSettings.enabled(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_status);
        views.setImageViewResource(R.id.light, on ? R.drawable.light_on : R.drawable.light_off);
        views.setTextViewText(R.id.label, on ? "Debug on" : "Debug off");
        views.setTextViewText(R.id.mode, DebugSettings.wireless(context) ? "Wireless" : "USB only");
        views.setContentDescription(R.id.root, (on ? "Debugging is on. " : "Debugging is off. ") + "Tap to turn " + (on ? "off." : "on."));
        Intent toggle = new Intent(context, ToggleReceiver.class); // Private receiver: other apps cannot trigger it.
        views.setOnClickPendingIntent(R.id.root, PendingIntent.getBroadcast(context, 0, toggle, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));
        manager.updateAppWidget(ids, views);
    }
}
