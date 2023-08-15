package org.metamechanists.aircraft.vehicles;

import org.bukkit.scheduler.BukkitRunnable;


public class VehicleTicker extends BukkitRunnable {
    public static final int INTERVAL_TICKS = 2;

    @Override
    public void run() {
        VehicleStorage.tick();
    }
}