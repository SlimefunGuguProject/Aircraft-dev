package org.metamechanists.aircraft.vehicles;

import org.bukkit.entity.Pig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;


public class UnmountHandler implements Listener {
    @EventHandler
    public static void onUnmount(@NotNull PlayerToggleSneakEvent e) {
        Pig seat = Storage.getSeat(e.getPlayer());
        if (seat == null) {
            return;
        }

        e.getPlayer().setInvisible(false);
    }
}
