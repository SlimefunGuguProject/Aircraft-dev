package org.metamechanists.aircraft.items.groups;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.metamechanists.aircraft.items.Groups;
import org.metamechanists.aircraft.vehicles.Vehicle;
import org.metamechanists.aircraft.vehicles.VehicleDescription;
import org.metamechanists.metalib.yaml.YamlTraverser;

import static org.metamechanists.aircraft.vehicles.Vehicle.GLIDER;


@UtilityClass
public class Aircraft {
    public void initialize() {
        final JavaPlugin plugin = org.metamechanists.aircraft.Aircraft.getInstance();
        final SlimefunAddon addon = (SlimefunAddon) plugin;

        new Vehicle(Groups.AIRCRAFT, GLIDER, RecipeType.NULL, new ItemStack[]{}, new VehicleDescription(new YamlTraverser(plugin, "vehicles/test_aircraft.yml"))).register(addon);
    }
}
