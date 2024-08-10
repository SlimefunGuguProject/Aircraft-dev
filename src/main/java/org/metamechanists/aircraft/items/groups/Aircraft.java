package org.metamechanists.aircraft.items.groups;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.metamechanists.aircraft.items.Groups;
import org.metamechanists.aircraft.vehicles.Vehicle;
import org.metamechanists.aircraft.vehicles.VehicleDescription;

import java.util.HashMap;
import java.util.Map;

import static org.metamechanists.aircraft.vehicles.Vehicle.LIGHT_AIRCRAFT;


@UtilityClass
public class Aircraft {
    private final Map<String, Vehicle> vehicles = new HashMap<>();

    public void initialize() {
        JavaPlugin plugin = org.metamechanists.aircraft.Aircraft.getInstance();
        SlimefunAddon addon = (SlimefunAddon) plugin;

        plugin.saveResource("vehicles/test_aircraft.yml", false);

        vehicles.put("test_aircraft", new Vehicle(Groups.AIRCRAFT, LIGHT_AIRCRAFT, RecipeType.NULL, new ItemStack[]{}, "test_aircraft", new VehicleDescription("vehicles/test_aircraft.yml")));

        vehicles.values().forEach(vehicle -> vehicle.register(addon));
    }

    public void reload() {
        vehicles.values().forEach(Vehicle::reload);
    }

    public Vehicle getVehicle(String name) {
        return vehicles.get(name);
    }
}
