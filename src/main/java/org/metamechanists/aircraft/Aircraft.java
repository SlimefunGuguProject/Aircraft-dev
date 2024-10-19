package org.metamechanists.aircraft;

import co.aikar.commands.PaperCommandManager;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.metamechanists.aircraft.commands.AircraftCommand;
import org.metamechanists.aircraft.slimefun.Items;
import org.metamechanists.aircraft.vehicle.component.Hider;
import org.metamechanists.aircraft.vehicle.handler.MountHandler;
import org.metamechanists.aircraft.vehicle.handler.KeyboardHandler;
import org.metamechanists.kinematiccore.api.addon.KinematicAddon;


public final class Aircraft extends JavaPlugin implements SlimefunAddon, KinematicAddon {
    @Getter
    private static Aircraft instance;

    private void initializeCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new AircraftCommand());
        commandManager.enableUnstableAPI("help");
    }

    @Override
    public void onEnable() {
        instance = this;
        Items.init();
        Hider.init();
        KeyboardHandler.init();
        MountHandler.init();
        initializeCommands();
    }

    @Override
    public void onDisable() {
        cleanup();
    }

    @Override
    public @NotNull JavaPlugin getJavaPlugin() {
        return this;
    }
    @Override
    public @Nullable String getBugTrackerURL() {
        return null;
    }

    @Override
    public @NotNull String name() {
        return "aircraft";
    }
}
