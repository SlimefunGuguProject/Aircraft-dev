package org.metamechanists.aircraft.vehicles;

import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;


public class CollisionListener implements Listener {
    @EventHandler
    public static void onVehicleBlockCollide(final VehicleBlockCollisionEvent event) {
        if (event.getVehicle() instanceof final Interaction seatInteraction) {

        }
    }
}
