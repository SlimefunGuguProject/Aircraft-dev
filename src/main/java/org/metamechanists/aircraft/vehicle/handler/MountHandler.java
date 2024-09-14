package org.metamechanists.aircraft.vehicle.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.aircraft.vehicle.VehicleInteractor;
import org.metamechanists.kinematiccore.api.entity.KinematicEntity;
import org.metamechanists.kinematiccore.api.storage.EntityStorage;
import org.spigotmc.event.entity.EntityDismountEvent;


public class MountHandler implements Listener {
    @EventHandler
    public static void onMount(@NotNull PlayerInteractEntityEvent e) {
        Bukkit.getLogger().severe("oh my god");
        if (EntityStorage.kinematicEntity(e.getRightClicked().getUniqueId()) != null) {
            Bukkit.getLogger().severe(EntityStorage.kinematicEntity(e.getRightClicked().getUniqueId()).schema().getId());
            return;
        }
        if (!(EntityStorage.kinematicEntity(e.getRightClicked().getUniqueId()) instanceof VehicleInteractor interactor)) {
            return;
        }

    }

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
