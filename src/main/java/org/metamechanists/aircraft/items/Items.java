package org.metamechanists.aircraft.items;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.utils.Keys;
import org.metamechanists.aircraft.vehicle.VehicleEntitySchema;
import org.metamechanists.kinematiccore.api.storage.EntitySchemas;

import java.util.HashSet;
import java.util.Set;


public final class Items {
    private static final ItemGroup AIRCRAFT_GROUP = new ItemGroup(Keys.AIRCRAFT,
            new CustomItemStack(Material.COMPASS, "&aAircraft"));

    private static final SlimefunItemStack THROTTLE_UP_STACK = new SlimefunItemStack(
                    "AIRCRAFT_THROTTLE_UP",
                    new CustomItemStack(Material.LIME_DYE, ChatColor.WHITE + "Throttle up"));
    private static final ThrottleControl THROTTLE_UP = new ThrottleControl(
            AIRCRAFT_GROUP,
            THROTTLE_UP_STACK,
            RecipeType.NULL,
            new ItemStack[]{},
            5);

    private static final SlimefunItemStack THROTTLE_DOWN_STACK = new SlimefunItemStack(
            "AIRCRAFT_THROTTLE_DOWN",
            new CustomItemStack(Material.RED_DYE, ChatColor.WHITE + "Throttle down"));
    private static final ThrottleControl THROTTLE_DOWN = new ThrottleControl(
            AIRCRAFT_GROUP,
            THROTTLE_DOWN_STACK,
            RecipeType.NULL,
            new ItemStack[]{},
            -5);

    private static final SlimefunItemStack STEER_LEFT_STACK = new SlimefunItemStack(
                        "AIRCRAFT_STEER_LEFT",
                        new CustomItemStack(Material.MUSIC_DISC_STAL, ChatColor.WHITE + "Steer left"));
    private static final SteerControl STEER_LEFT = new SteerControl(
            AIRCRAFT_GROUP,
            STEER_LEFT_STACK,
            RecipeType.NULL,
            new ItemStack[]{},
            1);

    private static final SlimefunItemStack STEER_RIGHT_STACK = new SlimefunItemStack(
                        "AIRCRAFT_STEER_RIGHT",
                        new CustomItemStack(Material.MUSIC_DISC_STAL, ChatColor.WHITE + "Steer right"));
    private static final SteerControl STEER_RIGHT = new SteerControl(
            AIRCRAFT_GROUP,
            STEER_RIGHT_STACK,
            RecipeType.NULL,
            new ItemStack[]{},
            -1);

    private static final SlimefunItemStack CRUDE_AIRCRAFT_STACK = new SlimefunItemStack(
            "AIRCRAFT_CRUDE_AIRCRAFT",
            new CustomItemStack(Material.FEATHER, ChatColor.WHITE + "Crude Aircraft"));
    private static @NotNull VehicleItem crudeAircraft(VehicleEntitySchema schema) {
        return new VehicleItem(
                schema,
                AIRCRAFT_GROUP,
                CRUDE_AIRCRAFT_STACK,
                RecipeType.NULL,
                new ItemStack[]{});
    }

    private static final Set<String> loadedAircraft = new HashSet<>();

    private Items() {}

    private static @Nullable VehicleEntitySchema loadVehicle(@NotNull String id) {
        try {
            VehicleEntitySchema schema = new VehicleEntitySchema(id);
            loadedAircraft.add(schema.getId());
            return schema;
        } catch (RuntimeException e) {
            Aircraft.getInstance().getLogger().severe("Failed to load aircraft " + id);
            e.printStackTrace();
            return null;
        }
    }

    public static void initialize() {
        JavaPlugin plugin = Aircraft.getInstance();
        SlimefunAddon addon = (SlimefunAddon) plugin;

        THROTTLE_UP.register(addon);
        THROTTLE_DOWN.register(addon);
        STEER_LEFT.register(addon);
        STEER_RIGHT.register(addon);

        VehicleEntitySchema schema = loadVehicle("crude_aircraft");
        if (schema != null) {
            crudeAircraft(schema).register(addon);
        }
    }

    public static void reload() {
        for (String id : loadedAircraft) {
            if (EntitySchemas.schema(id) instanceof VehicleEntitySchema schema) {
                schema.unregister();
            }
            // hacky but works
            new VehicleEntitySchema(id.replaceAll("aircraft:", ""));
        }
    }
}
