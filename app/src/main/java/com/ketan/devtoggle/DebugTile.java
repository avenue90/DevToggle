package com.ketan.devtoggle;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

/** Quick Settings tile; long-press opens Settings (QS_TILE_PREFERENCES). */
public final class DebugTile extends TileService {
    @Override public void onStartListening() { refresh(); StatusWidget.updateAll(this); }

    @Override public void onClick() {
        if (!DebugSettings.permitted(this)) { Toast.makeText(this, DebugSettings.GRANT, Toast.LENGTH_LONG).show(); return; }
        try { DebugSettings.toggle(this); }
        catch (SecurityException e) { Toast.makeText(this, "Blocked by the system: " + e.getMessage(), Toast.LENGTH_LONG).show(); }
        refresh();
    }

    private void refresh() {
        Tile tile = getQsTile(); if (tile == null) return;
        boolean on = DebugSettings.enabled(this);
        tile.setLabel(DebugSettings.wireless(this) ? "Wireless debug" : "USB debug");
        tile.setState(on ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.setSubtitle(on ? "On" : "Off");
        tile.updateTile();
    }
}
