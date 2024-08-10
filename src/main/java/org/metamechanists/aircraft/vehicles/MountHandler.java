package org.metamechanists.aircraft.vehicles;

import org.bukkit.entity.Pig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;

import java.util.UUID;


public class MountHandler implements Listener {
    @EventHandler
    public static void onMount(@NotNull PlayerInteractEntityEvent e) {
        UUID uuid = new PersistentDataTraverser(e.getRightClicked().getUniqueId()).getUuid("seatId");
        if (uuid == null) {
            return;
        }

        Pig seat = Storage.get(uuid);
        if (seat == null) {
            return;
        }

        Vehicle.mount(seat, e.getPlayer());
    }

    @EventHandler
    public static void onUnmount(@NotNull PlayerToggleSneakEvent e) {
        Pig seat = Storage.getSeat(e.getPlayer());
        if (seat == null) {
            return;
        }

        Vehicle.unMount(seat, e.getPlayer());
    }
}
