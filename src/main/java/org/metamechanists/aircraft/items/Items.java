package org.metamechanists.aircraft.items;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.utils.Keys;
import org.metamechanists.aircraft.vehicles.Vehicle;
import org.metamechanists.aircraft.vehicles.VehicleDescription;
import org.metamechanists.aircraft.vehicles.controls.ThrottleControl;
import org.metamechanists.metalib.yaml.YamlTraverser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.metamechanists.aircraft.vehicles.Vehicle.LIGHT_AIRCRAFT;


@UtilityClass
public class Items {
    private final Map<String, Vehicle> vehicles = new HashMap<>();

    private final ItemGroup AIRCRAFT_GROUP = new ItemGroup(Keys.AIRCRAFT,
            new CustomItemStack(Material.COMPASS, "&aAircraft"));

    private void readVehicles() {
        File[] vehicleFiles = new File(Aircraft.getInstance().getDataFolder(), "vehicles").listFiles();
        if (vehicleFiles == null) {
            return;
        }

        for (File file : vehicleFiles) {
            try {
                String rawName = new YamlTraverser(Aircraft.getInstance(), file).get("name");
                String name = ChatColor.translateAlternateColorCodes('&', rawName);
                String id = rawName.toLowerCase()
                        .replace(' ', '_')
                        .replaceAll("&.", "");
                SlimefunItemStack itemStack = new SlimefunItemStack(id, Material.FEATHER, name);
                vehicles.put(id, new Vehicle(
                        AIRCRAFT_GROUP,
                        itemStack,
                        RecipeType.NULL,
                        new ItemStack[]{},
                        id,
                        new VehicleDescription("vehicles/" + file.getName())));
            } catch (RuntimeException e) {
                Aircraft.getInstance().getLogger().severe("Failed to load aircraft " + file.getName() + ": " + e);
            }
        }
    }

    public void initialize() {
        JavaPlugin plugin = Aircraft.getInstance();
        SlimefunAddon addon = (SlimefunAddon) plugin;

        plugin.saveResource("vehicles/test_aircraft.yml", false);
        readVehicles();
        vehicles.values().forEach(vehicle -> vehicle.register(addon));

        new ThrottleControl(
                AIRCRAFT_GROUP,
                new SlimefunItemStack(
                        "AIRCRAFT_THROTTLE_UP",
                        new CustomItemStack(Material.LIME_DYE, ChatColor.WHITE + "Throttle up")
                ),
                RecipeType.NULL,
                new ItemStack[]{},
                2
        ).register(addon);

        new ThrottleControl(
                AIRCRAFT_GROUP,
                new SlimefunItemStack(
                        "AIRCRAFT_THROTTLE_DOWN",
                        new CustomItemStack(Material.RED_DYE, ChatColor.WHITE + "Throttle down")
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
