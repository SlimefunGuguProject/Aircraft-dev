package org.metamechanists.aircraft.vehicles.controls;

import org.bukkit.entity.Pig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;
import org.metamechanists.aircraft.vehicles.Storage;
import org.metamechanists.aircraft.vehicles.Vehicle;

import java.util.UUID;


public class MountHandler implements Listener {
    @EventHandler
    public static void onMount(@NotNull PlayerInteractEntityEvent e) {
        UUID uuid = new PersistentDataTraverser(e.getRightClicked().getUniqueId()).getUuid("pigId");
        if (uuid == null) {
            return;
        }

        Pig pig = Storage.get(uuid);
        if (pig == null) {
            return;
        }

        Vehicle.mount(pig, e.getPlayer());
    }

    @EventHandler
    public static void onUnmount(@NotNull PlayerToggleSneakEvent e) {
        Pig pig = Storage.getPig(e.getPlayer());
        if (pig == null) {
            return;
        }

        Vehicle.unMount(pig, e.getPlayer());
    }
}
