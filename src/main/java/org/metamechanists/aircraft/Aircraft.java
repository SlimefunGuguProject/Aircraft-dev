package org.metamechanists.aircraft;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.metamechanists.aircraft.items.Groups;
import org.metamechanists.aircraft.vehicles.VehicleTicker;


public final class Aircraft extends JavaPlugin implements SlimefunAddon {
    @Getter
    private static Aircraft instance;

    private void initializeRunnables() {
        new VehicleTicker().runTaskTimer(this, 0, VehicleTicker.INTERVAL_TICKS);
    }

    @Override
    public void onEnable() {
        instance = this;
        Groups.initialize();
        initializeRunnables();
    }
    @Override
    public void onDisable() {

    }

    @Override
    public @NotNull JavaPlugin getJavaPlugin() {
        return this;
    }
    @Override
    public @Nullable String getBugTrackerURL() {
        return null;
    }
}
