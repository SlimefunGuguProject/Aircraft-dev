package org.metamechanists.aircraft.slimefun;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.utils.Keys;
import org.metamechanists.aircraft.vehicle.VehicleEntitySchema;
import org.metamechanists.kinematiccore.api.entity.KinematicEntitySchema;
import org.metamechanists.kinematiccore.api.item.ItemStackBuilder;


public final class Items {
    private static final String DIAMOND_COLOR = "<color:#eec250>";
    private static final String KEY_COLOR = "<color:#b4b4b4>";
    private static final String VALUE_COLOR = "<color:#2182ff>";
    private static final String UNIT_COLOR = "<color:#416a7f>";

    private static final SlimefunItemStack THROTTLE_UP_STACK = new SlimefunItemStack(
            "AIRCRAFT_THROTTLE_UP",
            new CustomItemStack(Material.LIME_DYE, ChatColor.WHITE + "Throttle up"));
    private static final AircraftControl THROTTLE_UP = new AircraftControl(
            Groups.CONTROLS,
            THROTTLE_UP_STACK,
            RecipeType.NULL,
            new ItemStack[]{},
            "THROTTLE_UP");

    private static final SlimefunItemStack THROTTLE_DOWN_STACK = new SlimefunItemStack(
            "AIRCRAFT_THROTTLE_DOWN",
            new CustomItemStack(Material.RED_DYE, ChatColor.WHITE + "Throttle down"));
    private static final AircraftControl THROTTLE_DOWN = new AircraftControl(
            Groups.CONTROLS,
            THROTTLE_DOWN_STACK,
            RecipeType.NULL,
            new ItemStack[]{},
            "THROTTLE_DOWN");

    private static final SlimefunItemStack STEER_LEFT_STACK = new SlimefunItemStack(
            "AIRCRAFT_STEER_LEFT",
            new CustomItemStack(Material.MUSIC_DISC_STAL, ChatColor.WHITE + "Steer left"));
    private static final AircraftControl STEER_LEFT = new AircraftControl(
            Groups.CONTROLS,
            STEER_LEFT_STACK,
            RecipeType.NULL,
            new ItemStack[]{},
            "STEER_LEFT");

    private static final SlimefunItemStack STEER_RIGHT_STACK = new SlimefunItemStack(
            "AIRCRAFT_STEER_RIGHT",
            new CustomItemStack(Material.MUSIC_DISC_STAL, ChatColor.WHITE + "Steer right"));
    private static final AircraftControl STEER_RIGHT = new AircraftControl(
            Groups.CONTROLS,
            STEER_RIGHT_STACK,
            RecipeType.NULL,
            new ItemStack[]{},
            "STEER_RIGHT");

    private static final SlimefunItemStack STRAFE_LEFT_STACK  = new SlimefunItemStack(
            "AIRCRAFT_STRAFE_LEFT",
            new CustomItemStack(Material.ECHO_SHARD, ChatColor.WHITE + "Strafe left"));
    private static final AircraftControl STRAFE_LEFT = new AircraftControl(
            Groups.CONTROLS,
            STRAFE_LEFT_STACK,
            RecipeType.NULL,
            new ItemStack[]{},
            "STRAFE_LEFT");

    private static final SlimefunItemStack STRAFE_RIGHT_STACK = new SlimefunItemStack(
            "AIRCRAFT_STRAFE_RIGHT",
            new CustomItemStack(Material.AMETHYST_SHARD, ChatColor.WHITE + "Strafe right"));
    private static final AircraftControl STRAFE_RIGHT = new AircraftControl(
            Groups.CONTROLS,
            STRAFE_RIGHT_STACK,
            RecipeType.NULL,
            new ItemStack[]{},
            "STRAFE_RIGHT");

    private static final SlimefunItemStack CRUDE_AIRPLANE_STACK = new SlimefunItemStack(
            "AIRCRAFT_CRUDE_AIRPLANE",
            new ItemStackBuilder(Material.FEATHER)
                    .name("Crude Airplane")
                    .loreLine(ItemStackBuilder.VEHICLE)
                    .loreLine("")
                    .loreLine("<color:gray>Slow and inefficient, but at least it flies")
                    .loreLine("")
                    .build()
    );

    private static final SlimefunItemStack CRUDE_AIRSHIP_STACK = new SlimefunItemStack(
            "AIRCRAFT_CRUDE_AIRSHIP",
            new ItemStackBuilder(Material.FEATHER)
                    .name("Crude Airship")
                    .loreLine(ItemStackBuilder.VEHICLE)
                    .loreLine("")
                    .loreLine("<color:#c9c9c9>Slow and inefficient, but at least it flies")
                    .loreLine("")
                    .build()
            );

    private static final SlimefunItemStack CRUDE_DRONE_STACK = new SlimefunItemStack(
            "AIRCRAFT_CRUDE_DRONE",
            new ItemStackBuilder(Material.FEATHER)
                    .name("Crude Drone")
                    .loreLine(ItemStackBuilder.VEHICLE)
                    .loreLine("")
                    .loreLine("<color:#c9c9c9>Slow and inefficient, but at least it flies")
                    .loreLine("")
                    .build()
    );

    private static final SlimefunItemStack METACOIN_UFO = new SlimefunItemStack(
            "AIRCRAFT_METACOIN_UFO",
            new ItemStackBuilder(Material.GOLD_INGOT)
                    .name("Metacoin™ UFO")
                    .loreLine(ItemStackBuilder.VEHICLE)
                    .loreLine("")
                    .loreLine("<color:#c9c9c9>The transportation option of the future!")
                    .loreLine("<color:#c9c9c9>Embrace the future of blockchain technology")
                    .loreLine("<color:#c9c9c9>TO THE MOON!!!")
                    .loreLine("")
                    .build()
    );

    private static @NotNull VehicleItem crudeAirplane(VehicleEntitySchema schema) {
        return new VehicleItem(
                "AIRCRAFT_CRUDE_AIRPLANE",
                CRUDE_AIRPLANE_STACK,
                schema,
                Groups.VEHICLES,
                RecipeType.NULL,
                new ItemStack[]{});
    }

    private static @NotNull VehicleItem crudeAirship(VehicleEntitySchema schema) {
        return new VehicleItem(
                "AIRCRAFT_CRUDE_AIRSHIP",
                CRUDE_AIRSHIP_STACK,
                schema,
                Groups.VEHICLES,
                RecipeType.NULL,
                new ItemStack[]{});
    }

    private static @NotNull VehicleItem crudeDrone(VehicleEntitySchema schema) {
        return new VehicleItem(
                "AIRCRAFT_CRUDE_DRONE",
                CRUDE_DRONE_STACK,
                schema,
                Groups.VEHICLES,
                RecipeType.NULL,
                new ItemStack[]{});
    }

    private static @NotNull VehicleItem metacoinUfo(VehicleEntitySchema schema) {
        return new VehicleItem(
                "AIRCRAFT_METACOIN_UFO",
                METACOIN_UFO,
                schema,
                Groups.VEHICLES,
                RecipeType.NULL,
                new ItemStack[]{});
    }

    private Items() {}

    private static @Nullable VehicleEntitySchema loadVehicle(@NotNull String id, @NotNull ItemStack itemStack) {
        try {
            return new VehicleEntitySchema(id, itemStack);
        } catch (RuntimeException e) {
            Aircraft.getInstance().getLogger().severe("Failed to load aircraft " + id);
            e.printStackTrace();
            return null;
        }
    }

    public static void init() {
        JavaPlugin plugin = Aircraft.getInstance();
        SlimefunAddon addon = (SlimefunAddon) plugin;

        THROTTLE_UP.register(addon);
        THROTTLE_DOWN.register(addon);
        STEER_LEFT.register(addon);
        STEER_RIGHT.register(addon);
        STRAFE_LEFT.register(addon);
        STRAFE_RIGHT.register(addon);

        initAircraftItems(addon);
    }

    private static void initAircraftItems(SlimefunAddon addon) {
        VehicleEntitySchema crudeAirplaneSchema = loadVehicle("crude_airplane", CRUDE_AIRPLANE_STACK);
        if (crudeAirplaneSchema != null) {
            crudeAirplane(crudeAirplaneSchema).register(addon);
        }

        VehicleEntitySchema crudeAirshipSchema = loadVehicle("crude_airship", CRUDE_AIRSHIP_STACK);
        if (crudeAirshipSchema != null) {
            crudeAirship(crudeAirshipSchema).register(addon);
        }

        VehicleEntitySchema crudeDroneSchema = loadVehicle("crude_drone", CRUDE_DRONE_STACK);
        if (crudeDroneSchema != null) {
            crudeDrone(crudeDroneSchema).register(addon);
        }

        VehicleEntitySchema metacoinUfoSchema = loadVehicle("metacoin_ufo", METACOIN_UFO);
        if (metacoinUfoSchema != null) {
            metacoinUfo(metacoinUfoSchema).register(addon);
        }
    }

    public static void reload() {
        for (String id : KinematicEntitySchema.registeredSchemasByAddon(Aircraft.getInstance())) {
            KinematicEntitySchema schema = KinematicEntitySchema.get(id);
            if (schema != null) {
                Aircraft.getInstance().getLogger().info("yeeted " + id);
                schema.unregister();
            }
        }

        SlimefunAddon addon = Aircraft.getInstance();

        VehicleEntitySchema crudeAirplaneSchema = loadVehicle("crude_airplane", CRUDE_AIRPLANE_STACK);
        if (crudeAirplaneSchema != null) {
            SlimefunItem newItem = crudeAirplane(crudeAirplaneSchema);
            ((VehicleItem) Slimefun.getRegistry().getSlimefunItemIds().get(newItem.getId())).schema = crudeAirplaneSchema;
        }

        VehicleEntitySchema crudeAirshipSchema = loadVehicle("crude_airship", CRUDE_AIRSHIP_STACK);
        if (crudeAirshipSchema != null) {
            SlimefunItem newItem = crudeAirship(crudeAirshipSchema);
            ((VehicleItem) Slimefun.getRegistry().getSlimefunItemIds().get(newItem.getId())).schema = crudeAirshipSchema;
        }

        VehicleEntitySchema crudeDroneSchema = loadVehicle("crude_drone", CRUDE_DRONE_STACK);
        if (crudeDroneSchema != null) {
            SlimefunItem newItem = crudeDrone(crudeDroneSchema);
            ((VehicleItem) Slimefun.getRegistry().getSlimefunItemIds().get(newItem.getId())).schema = crudeDroneSchema;
        }

        VehicleEntitySchema metacoinUfo = loadVehicle("metacoin_ufo", METACOIN_UFO);
        if (metacoinUfo != null) {
            SlimefunItem newItem = metacoinUfo(metacoinUfo);
            ((VehicleItem) Slimefun.getRegistry().getSlimefunItemIds().get(newItem.getId())).schema = metacoinUfo;
        }
    }

    private static @NotNull String keyValueUnit(String key, String value, String unit) {
        return DIAMOND_COLOR + ItemStackBuilder.DIAMOND
                + " " + KEY_COLOR + key
                + " " + VALUE_COLOR + value
                + " " + UNIT_COLOR + unit;
    }
}
