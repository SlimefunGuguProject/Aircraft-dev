package org.metamechanists.aircraft.vehicles.controls;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;
import org.metamechanists.aircraft.vehicles.Storage;
import org.metamechanists.aircraft.vehicles.Vehicle;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.spigotmc.event.entity.EntityDismountEvent;

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
    public static void onUnmount(@NotNull EntityDismountEvent e) {
        Entity exited = e.getEntity();
        if (!(exited instanceof Player player)) {
            return;
        }

        Pig pig = Storage.getPig(player);
        if (pig == null) {
            return;
        }

        Vehicle.unMount(pig, player);

        VehicleState state = VehicleState.fromPig(pig);
        if (state == null) {
            return;
        }

        state.throttle = 0;
        state.write(pig);
    }
}
