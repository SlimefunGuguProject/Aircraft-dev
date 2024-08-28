package org.metamechanists.aircraft.vehicles;

import org.bukkit.scheduler.BukkitRunnable;


public class VehicleTicker extends BukkitRunnable {
    @Override
    public void run() {
        Storage.tick();
    }
}