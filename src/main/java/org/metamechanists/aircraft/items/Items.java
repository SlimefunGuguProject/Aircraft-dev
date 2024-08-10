package org.metamechanists.aircraft.items;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.metamechanists.aircraft.utils.Keys;
import org.metamechanists.aircraft.vehicles.Vehicle;
import org.metamechanists.aircraft.vehicles.VehicleDescription;
import org.metamechanists.aircraft.vehicles.controls.ThrottleControl;

import java.util.HashMap;
import java.util.Map;

import static org.metamechanists.aircraft.vehicles.Vehicle.LIGHT_AIRCRAFT;


@UtilityClass
public class Items {
    private final Map<String, Vehicle> vehicles = new HashMap<>();

    private final ItemGroup AIRCRAFT_GROUP = new ItemGroup(Keys.AIRCRAFT,
            new CustomItemStack(Material.COMPASS, "&aAircraft"));

    public void initialize() {
        JavaPlugin plugin = org.metamechanists.aircraft.Aircraft.getInstance();
        SlimefunAddon addon = (SlimefunAddon) plugin;

        plugin.saveResource("vehicles/test_aircraft.yml", false);

        vehicles.put("test_aircraft", new Vehicle(
                AIRCRAFT_GROUP,
                LIGHT_AIRCRAFT,
                RecipeType.NULL,
                new ItemStack[]{},
                "test_aircraft",
                new VehicleDescription("vehicles/test_aircraft.yml")));

        vehicles.values().forEach(vehicle -> vehicle.register(addon));

        new ThrottleControl(
                AIRCRAFT_GROUP,
                new SlimefunItemStack(
                        "THROTTLE_UP",
                        new CustomItemStack(Material.GREEN_DYE, "Throttle up")
                ),
                RecipeType.NULL,
                new ItemStack[]{},
                2
        ).register(addon);

        new ThrottleControl(
                AIRCRAFT_GROUP,
                new SlimefunItemStack(
                        "THROTTLE_DOWN",
                        new CustomItemStack(Material.GREEN_DYE, "Throttle down")
                ),
                RecipeType.NULL,
                new ItemStack[]{},
                -2
        ).register(addon);
    }

    public void reload() {
        vehicles.values().forEach(Vehicle::reload);
    }

    public Vehicle getVehicle(String name) {
        return vehicles.get(name);
    }
}
