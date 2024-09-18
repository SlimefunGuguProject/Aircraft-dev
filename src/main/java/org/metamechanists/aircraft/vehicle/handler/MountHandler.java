package org.metamechanists.aircraft.vehicle.handler;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.kinematiccore.api.storage.EntityStorage;


public class MountHandler implements Listener {
    @EventHandler
    public static void onUnmount(@NotNull EntityDismountEvent e) {
        Entity maybePlayer = e.getEntity();
        if (!(maybePlayer instanceof Player player)) {
            return;
        }

        if (!(EntityStorage.kinematicEntity(e.getDismounted().getUniqueId()) instanceof VehicleEntity vehicleEntity)) {
            return;
        }

        vehicleEntity.unmount(player);
    }
}
