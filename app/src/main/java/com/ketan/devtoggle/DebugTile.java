package com.ketan.devtoggle;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

public final class DebugTile extends TileService {
    @Override public void onStartListening() { refresh(); }

    @Override public void onClick() {
        if (!DebugSettings.permitted(this)) { Toast.makeText(this, DebugSettings.GRANT, Toast.LENGTH_LONG).show(); return; }
        try { DebugSettings.set(this, !DebugSettings.enabled(this)); }
        catch (SecurityException e) { Toast.makeText(this, "Blocked by the system: " + e.getMessage(), Toast.LENGTH_LONG).show(); }
        refresh();
    }

    private void refresh() {
        Tile tile = getQsTile(); if (tile == null) return;
        boolean on = DebugSettings.enabled(this);
        tile.setState(on ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.setSubtitle(on ? "On" : "Off");
        tile.updateTile();
    }
}
