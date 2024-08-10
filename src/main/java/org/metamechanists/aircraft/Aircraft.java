package org.metamechanists.aircraft;

import co.aikar.commands.PaperCommandManager;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.metamechanists.aircraft.commands.AircraftCommand;
import org.metamechanists.aircraft.items.Items;
import org.metamechanists.aircraft.vehicles.VehicleTicker;
import org.metamechanists.aircraft.vehicles.KeyboardHandler;


public final class Aircraft extends JavaPlugin implements SlimefunAddon {
    @Getter
    private static Aircraft instance;

    private static void initializeListeners() {
        KeyboardHandler.addProtocolListener();
    }

    private void initializeRunnables() {
        new VehicleTicker().runTaskTimer(this, 0, VehicleTicker.INTERVAL_TICKS);
    }

    private void initializeCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new AircraftCommand());
        commandManager.enableUnstableAPI("help");
    }

    @Override
    public void onEnable() {
        instance = this;
        Items.initialize();
        initializeListeners();
        initializeRunnables();
        initializeCommands();
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
