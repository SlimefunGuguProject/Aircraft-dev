package org.metamechanists.aircraft.slimefun;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.vehicle.VehicleEntitySchema;
import org.metamechanists.kinematiccore.api.entity.KinematicEntitySchema;
import org.metamechanists.kinematiccore.api.item.ItemStackBuilder;


public final class Items {
    private static final String DIAMOND_COLOR = "<color:#eec250>";
    private static final String KEY_COLOR = "<color:#b4b4b4>";
    private static final String VALUE_COLOR = "<color:#2182ff>";
    private static final String UNIT_COLOR = "<color:#416a7f>";

    private static final SlimefunItemStack CRUDE_AIRPLANE_WING = new SlimefunItemStack(
            "AIRCRAFT_AIRPLANE_WING",
            new CustomItemStack(Material.WHITE_WOOL, org.bukkit.ChatColor.WHITE + "Crude Airplane Wing")
    );

    private static final SlimefunItemStack CRUDE_AIRPLANE_FRAME = new SlimefunItemStack(
            "AIRCRAFT_AIRPLANE_FRAME",
            new CustomItemStack(Material.OAK_LOG, org.bukkit.ChatColor.WHITE + "Crude Airplane Frame")
    );

    private static final SlimefunItemStack CRUDE_AIRPLANE_TAIL = new SlimefunItemStack(
            "AIRCRAFT_AIRPLANE_TAIL",
            new CustomItemStack(Material.OAK_WOOD, org.bukkit.ChatColor.WHITE + "Crude Airplane Tail")
    );

    private static final SlimefunItemStack CRUDE_AIRPLANE_AVIONICS = new SlimefunItemStack(
            "AIRCRAFT_AIRPLANE_AVIONICS",
            new CustomItemStack(Material.REPEATER, org.bukkit.ChatColor.WHITE + "Crude Airplane Avionics")
    );

    private static final SlimefunItemStack CRUDE_AIRPLANE_ENGINE = new SlimefunItemStack(
            "AIRCRAFT_AIRPLANE_ENGINE",
            new CustomItemStack(Material.PISTON, org.bukkit.ChatColor.WHITE + "Crude Airplane Engine")
    );

    private static final SlimefunItemStack CRUDE_AIRSHIP_BALLOON = new SlimefunItemStack(
            "AIRCRAFT_AIRSHIP_BALLOON",
            new CustomItemStack(Material.WHITE_WOOL, org.bukkit.ChatColor.WHITE + "Crude Airship Balloon")
    );

    private static final SlimefunItemStack CRUDE_AIRSHIP_FRAME = new SlimefunItemStack(
            "AIRCRAFT_AIRSHIP_FRAME",
            new CustomItemStack(Material.OAK_LOG, org.bukkit.ChatColor.WHITE + "Crude Airship Frame")
    );

    private static final SlimefunItemStack CRUDE_AIRSHIP_AVIONICS = new SlimefunItemStack(
            "AIRCRAFT_AIRSHIP_AVIONICS",
            new CustomItemStack(Material.REPEATER, org.bukkit.ChatColor.WHITE + "Crude Airship Avionics")
    );

    private static final SlimefunItemStack CRUDE_AIRSHIP_ENGINE = new SlimefunItemStack(
            "AIRCRAFT_AIRSHIP_AVIONICS",
            new CustomItemStack(Material.PISTON, org.bukkit.ChatColor.WHITE + "Crude Airship Engine")
    );

    private static final SlimefunItemStack CRUDE_DRONE_FRAME = new SlimefunItemStack(
            "AIRCRAFT_DRONE_FRAME",
            new CustomItemStack(Material.OAK_LOG, org.bukkit.ChatColor.WHITE + "Crude Airship Frame")
    );

    private static final SlimefunItemStack CRUDE_DRONE_ROTOR = new SlimefunItemStack(
            "AIRCRAFT_DRONE_ROTOR",
            new CustomItemStack(Material.GRAY_CONCRETE, org.bukkit.ChatColor.WHITE + "Crude Drone Rotor")
    );

    private static final SlimefunItemStack CRUDE_DRONE_ENGINE = new SlimefunItemStack(
            "AIRCRAFT_DRONE_ENGINE",
            new CustomItemStack(Material.PISTON, org.bukkit.ChatColor.WHITE + "Crude Drone Engine")
    );

    private static final SlimefunItemStack CRUDE_DRONE_AVIONICS = new SlimefunItemStack(
            "AIRCRAFT_DRONE_ENGINE",
            new CustomItemStack(Material.REPEATER, org.bukkit.ChatColor.WHITE + "Crude Drone Avionics")
    );

    private static final SlimefunItemStack HOVERDUCK_AVIONICS = new SlimefunItemStack(
            "AIRCRAFT_HOVERDUCK_AVIONICS",
            new CustomItemStack(Material.TARGET, org.bukkit.ChatColor.WHITE + "Hoverduck Avionics")
    );

    private static final SlimefunItemStack HOVERDUCK_ANTIGRAV_SYSTEM = new SlimefunItemStack(
            "AIRCRAFT_HOVERDUCK_ANTIGRAV_SYSTEM",
            new CustomItemStack(Material.LIGHT_BLUE_CONCRETE, org.bukkit.ChatColor.WHITE + "Hoverduck Antigrav System")
    );

    private static final SlimefunItemStack CESSNA_FRAME = new SlimefunItemStack(
            "AIRCRAFT_CESSNA_FRAME",
            new CustomItemStack(Material.IRON_BLOCK, org.bukkit.ChatColor.WHITE + "Cessna Frame")
    );

    private static final SlimefunItemStack CESSNA_AVIONICS = new SlimefunItemStack(
            "AIRCRAFT_CESSNA_AVIONICS",
            new CustomItemStack(Material.REPEATER, org.bukkit.ChatColor.WHITE + "Cessna Avionics")
    );

    private static final SlimefunItemStack CESSNA_WING = new SlimefunItemStack(
            "AIRCRAFT_CESSNA_WING",
            new CustomItemStack(Material.WHITE_WOOL, org.bukkit.ChatColor.WHITE + "Cessna Wing")
    );

    private static final SlimefunItemStack CESSNA_TAIL = new SlimefunItemStack(
            "AIRCRAFT_CESSNA_TAIL",
            new CustomItemStack(Material.IRON_BLOCK, org.bukkit.ChatColor.WHITE + "Cessna Tail")
    );

    private static final SlimefunItemStack CESSNA_ENGINE = new SlimefunItemStack(
            "AIRCRAFT_CESSNA_ENGINE",
            new CustomItemStack(Material.PISTON, org.bukkit.ChatColor.WHITE + "Cessna Engine")
    );

    private static final SlimefunItemStack THROTTLE_UP_STACK = new SlimefunItemStack(
            "AIRCRAFT_THROTTLE_UP",
            new CustomItemStack(Material.LIME_DYE, ChatColor.WHITE + "Throttle up"));
    private static final AircraftControl THROTTLE_UP = new AircraftControl(
            Groups.CONTROLS,
            THROTTLE_UP_STACK,
            RecipeType.NULL,
            new ItemStack[]{
                    SlimefunItems.BASIC_CIRCUIT_BOARD, null, null,
                    null, null, null,
                    null, null, null,
            },
            "THROTTLE_UP");

    private static final SlimefunItemStack THROTTLE_DOWN_STACK = new SlimefunItemStack(
            "AIRCRAFT_THROTTLE_DOWN",
            new CustomItemStack(Material.RED_DYE, ChatColor.WHITE + "Throttle down"));
    private static final AircraftControl THROTTLE_DOWN = new AircraftControl(
            Groups.CONTROLS,
            THROTTLE_DOWN_STACK,
            RecipeType.NULL,
            new ItemStack[]{
                    null, SlimefunItems.BASIC_CIRCUIT_BOARD, null,
                    null, null, null,
                    null, null, null,
            },
            "THROTTLE_DOWN");

    private static final SlimefunItemStack STEER_LEFT_STACK = new SlimefunItemStack(
            "AIRCRAFT_STEER_LEFT",
            new CustomItemStack(Material.MUSIC_DISC_STAL, ChatColor.WHITE + "Steer left"));
    private static final AircraftControl STEER_LEFT = new AircraftControl(
            Groups.CONTROLS,
            STEER_LEFT_STACK,
            RecipeType.NULL,
            new ItemStack[]{
                    null, null, SlimefunItems.BASIC_CIRCUIT_BOARD,
                    null, null, null,
                    null, null, null,
            },
            "STEER_LEFT");

    private static final SlimefunItemStack STEER_RIGHT_STACK = new SlimefunItemStack(
            "AIRCRAFT_STEER_RIGHT",
            new CustomItemStack(Material.MUSIC_DISC_STAL, ChatColor.WHITE + "Steer right"));
    private static final AircraftControl STEER_RIGHT = new AircraftControl(
            Groups.CONTROLS,
            STEER_RIGHT_STACK,
            RecipeType.NULL,
            new ItemStack[]{
                    null, null, null,
                    SlimefunItems.BASIC_CIRCUIT_BOARD, null, null,
                    null, null, null,
            },
            "STEER_RIGHT");

    private static final SlimefunItemStack STRAFE_LEFT_STACK  = new SlimefunItemStack(
            "AIRCRAFT_STRAFE_LEFT",
            new CustomItemStack(Material.ECHO_SHARD, ChatColor.WHITE + "Strafe left"));
    private static final AircraftControl STRAFE_LEFT = new AircraftControl(
            Groups.CONTROLS,
            STRAFE_LEFT_STACK,
            RecipeType.NULL,
            new ItemStack[]{
                    null, null, null,
                    null, SlimefunItems.BASIC_CIRCUIT_BOARD, null,
                    null, null, null,
            },
            "STRAFE_LEFT");

    private static final SlimefunItemStack STRAFE_RIGHT_STACK = new SlimefunItemStack(
            "AIRCRAFT_STRAFE_RIGHT",
            new CustomItemStack(Material.AMETHYST_SHARD, ChatColor.WHITE + "Strafe right"));
    private static final AircraftControl STRAFE_RIGHT = new AircraftControl(
            Groups.CONTROLS,
            STRAFE_RIGHT_STACK,
            RecipeType.NULL,
            new ItemStack[]{
                    null, null, null,
                    null, null, SlimefunItems.BASIC_CIRCUIT_BOARD,
                    null, null, null,
            },
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
            new ItemStackBuilder(Material.PAPER)
                    .name("Crude Airship")
                    .loreLine(ItemStackBuilder.VEHICLE)
                    .loreLine("")
                    .loreLine("<color:#c9c9c9>Slow and inefficient, but at least it flies")
                    .loreLine("")
                    .build()
    );

    private static final SlimefunItemStack CRUDE_DRONE_STACK = new SlimefunItemStack(
            "AIRCRAFT_CRUDE_DRONE",
            new ItemStackBuilder(Material.PHANTOM_MEMBRANE)
                    .name("Crude Drone")
                    .loreLine(ItemStackBuilder.VEHICLE)
                    .loreLine("")
                    .loreLine("<color:#c9c9c9>Slow and inefficient, but at least it flies")
                    .loreLine("")
                    .build()
    );

    private static final SlimefunItemStack CESSNA_STACK = new SlimefunItemStack(
            "AIRCRAFT_CESSNA",
            new ItemStackBuilder(Material.IRON_NUGGET)
                    .name("Crude Airplane")
                    .loreLine(ItemStackBuilder.VEHICLE)
                    .loreLine("")
                    .loreLine("<color:gray>An upgrade over the crude aircraft")
                    .loreLine("")
                    .build()
    );

    private static final SlimefunItemStack HOVERDUCK_STACK = new SlimefunItemStack(
            "AIRCRAFT_HOVERDUCK",
            new ItemStackBuilder(Material.GOLD_INGOT)
                    .name("Hoverduck™")
                    .loreLine(ItemStackBuilder.VEHICLE)
                    .loreLine("")
                    .loreLine("<color:#c9c9c9>Quack quack")
                    .loreLine("")
                    .build()
    );

    private static final SlimefunItemStack METACOIN_UFO_STACK = new SlimefunItemStack(
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
                RecipeType.ENHANCED_CRAFTING_TABLE,
                new ItemStack[]{
                        null, CRUDE_AIRPLANE_WING, null,
                        CRUDE_AIRPLANE_TAIL, CRUDE_AIRPLANE_FRAME, CRUDE_AIRPLANE_ENGINE,
                        null, CRUDE_AIRPLANE_WING, CRUDE_AIRPLANE_AVIONICS,
                });
    }

    private static @NotNull VehicleItem crudeAirship(VehicleEntitySchema schema) {
        return new VehicleItem(
                "AIRCRAFT_CRUDE_AIRSHIP",
                CRUDE_AIRSHIP_STACK,
                schema,
                Groups.VEHICLES,
                RecipeType.ENHANCED_CRAFTING_TABLE,
                new ItemStack[]{
                        CRUDE_AIRSHIP_BALLOON, CRUDE_AIRSHIP_BALLOON, CRUDE_AIRSHIP_BALLOON,
                        new ItemStack(Material.GRAY_CONCRETE), null, new ItemStack(Material.GRAY_CONCRETE),
                        CRUDE_AIRSHIP_ENGINE, CRUDE_AIRSHIP_FRAME, CRUDE_AIRSHIP_AVIONICS,
                });
    }

    private static @NotNull VehicleItem crudeDrone(VehicleEntitySchema schema) {
        return new VehicleItem(
                "AIRCRAFT_CRUDE_DRONE",
                CRUDE_DRONE_STACK,
                schema,
                Groups.VEHICLES,
                RecipeType.NULL,
                new ItemStack[]{
                        CRUDE_DRONE_ROTOR, CRUDE_DRONE_FRAME, CRUDE_DRONE_ROTOR,
                        null, CRUDE_DRONE_ENGINE, CRUDE_DRONE_AVIONICS,
                        CRUDE_DRONE_ROTOR, CRUDE_DRONE_FRAME, CRUDE_DRONE_ROTOR,

                });
    }

    private static @NotNull VehicleItem cessna(VehicleEntitySchema schema) {
        return new VehicleItem(
                "AIRCRAFT_CESSNA",
                CESSNA_STACK,
                schema,
                Groups.VEHICLES,
                RecipeType.NULL,
                new ItemStack[]{
                        null, CESSNA_WING, null,
                        CESSNA_TAIL, CESSNA_FRAME, CESSNA_ENGINE,
                        null, CESSNA_WING, CESSNA_AVIONICS,
                });
    }

    private static @NotNull VehicleItem hoverduck(VehicleEntitySchema schema) {
        return new VehicleItem(
                "AIRCRAFT_HOVERDUCK",
                HOVERDUCK_STACK,
                schema,
                Groups.VEHICLES,
                RecipeType.NULL,
                new ItemStack[]{
                        new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.YELLOW_WOOL),
                        new ItemStack(Material.YELLOW_WOOL), HOVERDUCK_AVIONICS, new ItemStack(Material.YELLOW_WOOL),
                        new ItemStack(Material.YELLOW_WOOL), HOVERDUCK_ANTIGRAV_SYSTEM, new ItemStack(Material.YELLOW_WOOL),
                });
    }

    private static @NotNull VehicleItem metacoinUfo(VehicleEntitySchema schema) {
        return new VehicleItem(
                "AIRCRAFT_METACOIN_UFO",
                METACOIN_UFO_STACK,
                schema,
                Groups.VEHICLES,
                RecipeType.NULL,
                new ItemStack[]{
                        new ItemStack(Material.DEAD_BUSH), new ItemStack(Material.DEAD_BUSH), new ItemStack(Material.DEAD_BUSH),
                        new ItemStack(Material.DEAD_BUSH), getItem("INFINITY_SINGULARITY"), new ItemStack(Material.DEAD_BUSH),
                        new ItemStack(Material.DEAD_BUSH), new ItemStack(Material.DEAD_BUSH), new ItemStack(Material.DEAD_BUSH),
                });
    }

    private static ItemStack getItem(String id) {
        return Slimefun.getRegistry().getSlimefunItemIds().get(id).getItem();
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

        VehicleEntitySchema cessnaSchema = loadVehicle("cessna", CESSNA_STACK);
        if (cessnaSchema != null) {
            cessna(cessnaSchema).register(addon);
        }

        VehicleEntitySchema hoverduckSchema = loadVehicle("hoverduck", HOVERDUCK_STACK);
        if (hoverduckSchema != null) {
            hoverduck(hoverduckSchema).register(addon);
        }

        VehicleEntitySchema metacoinUfoSchema = loadVehicle("metacoin_ufo", METACOIN_UFO_STACK);
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

        VehicleEntitySchema cessnaSchema = loadVehicle("cessna", CESSNA_STACK);
        if (cessnaSchema != null) {
            SlimefunItem newItem = cessna(cessnaSchema);
            ((VehicleItem) Slimefun.getRegistry().getSlimefunItemIds().get(newItem.getId())).schema = cessnaSchema;
        }

        VehicleEntitySchema hoverduckSchema = loadVehicle("hoverduck", HOVERDUCK_STACK);
        if (hoverduckSchema != null) {
            SlimefunItem newItem = hoverduck(hoverduckSchema);
            ((VehicleItem) Slimefun.getRegistry().getSlimefunItemIds().get(newItem.getId())).schema = hoverduckSchema;
        }

        VehicleEntitySchema metacoinUfo = loadVehicle("metacoin_ufo", METACOIN_UFO_STACK);
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
