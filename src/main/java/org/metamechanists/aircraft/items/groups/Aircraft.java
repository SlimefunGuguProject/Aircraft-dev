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

import java.util.HashMap;
import java.util.Map;

import static org.metamechanists.aircraft.vehicles.Vehicle.GLIDER;


@UtilityClass
public class Aircraft {
    private final Map<String, Vehicle> vehicles = new HashMap<>();

    public void initialize() {
        final JavaPlugin plugin = org.metamechanists.aircraft.Aircraft.getInstance();
        final SlimefunAddon addon = (SlimefunAddon) plugin;

        plugin.saveResource("vehicles/test_aircraft.yml", true);

        vehicles.put("test_aircraft", new Vehicle(Groups.AIRCRAFT, GLIDER, RecipeType.NULL, new ItemStack[]{},
                "test_aircraft", new VehicleDescription(new YamlTraverser(plugin, "vehicles/test_aircraft.yml"))));

        vehicles.values().forEach(vehicle -> vehicle.register(addon));
    }

    public Vehicle getVehicle(final String name) {
        return vehicles.get(name);
    }
}
