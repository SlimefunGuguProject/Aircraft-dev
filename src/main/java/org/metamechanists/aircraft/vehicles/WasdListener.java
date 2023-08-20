package org.metamechanists.aircraft.vehicles;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.metamechanists.aircraft.Aircraft;


public class WasdListener implements Listener {
    @EventHandler
    public static void onPlayerMove(final PlayerMoveEvent event) {
        Aircraft.getInstance().getLogger().info("oh no");
    }
}
