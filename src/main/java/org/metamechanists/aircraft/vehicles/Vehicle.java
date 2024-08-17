package org.metamechanists.aircraft.vehicles;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.vehicles.forces.SpatialForce;
import org.metamechanists.aircraft.vehicles.hud.VehicleHud;
import org.metamechanists.displaymodellib.builders.InteractionBuilder;
import org.metamechanists.displaymodellib.models.ModelBuilder;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedCuboid;
import org.metamechanists.displaymodellib.sefilib.entity.display.DisplayGroup;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static java.lang.Math.toRadians;


public class Vehicle extends SlimefunItem {
    private final String id;
    private VehicleConfig config;
    private static final boolean ENABLE_DEBUG_ARROWS = false;

    public Vehicle(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, String id, VehicleConfig config) {
        super(itemGroup, item, recipeType, recipe);
        this.id = id;
        this.config = config;
        addItemHandler(onItemUse());
    }

    public void reload() {
        config = new VehicleConfig(config.getPath());
    }

    private @NotNull ItemUseHandler onItemUse() {
        return event -> {
            Player player = event.getPlayer();
            if (event.getClickedBlock().isPresent() && !player.isInsideVehicle()) {
                if (player.getGameMode() != GameMode.CREATIVE) {
                    player.getInventory().getItemInMainHand().subtract();
                }
                place(event.getClickedBlock().get().getRelative(event.getClickedFace()), player);
            }
        };
    }

    public static void mount(@NotNull Pig pig, @NotNull Player player) {
        UUID uuid = new PersistentDataTraverser(pig).getUuid("interactionId");
        if (uuid != null) {
            Entity interaction = Bukkit.getEntity(uuid);
            if (interaction != null) {
                interaction.remove();
            }
        }

        new PersistentDataTraverser(pig).unset("interactionId");

//        player.setInvisible(true);
        pig.addPassenger(player);
    }

    public static void unMount(@NotNull Pig pig, @NotNull Player player) {
        createInteraction(pig);
        player.setInvisible(false);
    }

    private static void createInteraction(@NotNull Pig pig) {
        Interaction interaction = new InteractionBuilder()
                .width(1.2F)
                .height(1.2F)
                .build(pig.getLocation());

        new PersistentDataTraverser(interaction).set("pigId", pig.getUniqueId());
        new PersistentDataTraverser(pig).set("interactionId", interaction.getUniqueId());

        pig.addPassenger(interaction);
    }

    private void place(@NotNull Block block, @NotNull Player player) {
        VehicleState state = new VehicleState(
                0,
                new Vector3d(),
                new Vector3d(0, toRadians(-90.0-player.getEyeLocation().getYaw()), 0),
                new Vector3d(),
                config.initializeOrientations(),
                null,
                null
        );

        state.componentGroup = buildComponents(state, block.getLocation());
        state.hudGroup = buildHud(state, block.getLocation());

        Pig pig = (Pig) block.getWorld().spawnEntity(block.getLocation().clone().toCenterLocation().add(new Vector(0, -0.5, 0)), EntityType.PIG);
        pig.setInvulnerable(true);
        pig.setGravity(false);
        pig.setInvisible(true);
        pig.setSilent(true);
        pig.addPassenger(state.componentGroup.getParentDisplay());
        pig.addPassenger(state.hudGroup.getParentDisplay());

        state.componentGroup.getDisplays().values().forEach(pig::addPassenger);
        state.hudGroup.getDisplays().values().forEach(pig::addPassenger);

        createInteraction(pig);
        
        state.write(pig);

        PersistentDataTraverser traverser = new PersistentDataTraverser(pig);
        traverser.set("id", id);
        traverser.set("player", player.getUniqueId());

        Storage.add(pig.getUniqueId());
    }

    private @NotNull DisplayGroup buildComponents(VehicleState state, Location location) {
        ModelBuilder builder = new ModelBuilder();
        config.getCuboids(state).forEach(builder::add);
        return builder.buildAtBlockCenter(location);
    }

    private static @NotNull DisplayGroup buildHud(VehicleState state, Location location) {
        ModelBuilder builder = new ModelBuilder();
        VehicleHud.build(state).forEach(builder::add);
        return builder.buildAtBlockCenter(location);
    }

    private static @NotNull Optional<Player> getPilot(@NotNull Pig pig) {
        return pig.getPassengers().stream()
                .filter(entity -> entity instanceof Player)
                .map(Player.class::cast)
                .findFirst();
    }

    private static void remove(@NotNull Pig pig, @NotNull Interaction interaction, @NotNull DisplayGroup componentGroup, @NotNull DisplayGroup hudGroup) {
        interaction.remove();
        componentGroup.remove();
        hudGroup.remove();
        Storage.remove(pig.getUniqueId());
        pig.getLocation().createExplosion(4);
        getPilot(pig).ifPresent(pilot -> {
            pilot.setInvisible(false);
            pilot.eject();
            componentGroup.getParentDisplay().removePassenger(pilot);
        });
        pig.remove();
    }

    public void onKey(@NotNull Pig pig, char key) {
        VehicleState state = VehicleState.fromPig(pig);
        if (state == null) {
            Aircraft.getInstance().getLogger().warning("Failed to handle onKey: vehicle state of " + pig.getUniqueId() + " is null");
            return;
        }

        config.onKey(state, key);
        state.write(pig);
    }

    public void tickAircraft(@NotNull Pig pig) {
        VehicleState state = VehicleState.fromPig(pig);
        if (state == null) {
            return;
        }

        boolean isOnGround = pig.wouldCollideUsing(pig.getBoundingBox().shift(new Vector(0.0, -0.1, 0.0)));

        Set<SpatialForce> forces = VehicleForces.getForces(config, state, isOnGround);

        state.velocity.mul(1.0 - config.getVelocityDamping());
        Vector3d acceleration = VehicleForces.getAcceleration(config, forces);
        VehicleForces.cancelVelocityAndAcceleration(pig, state.velocity, acceleration);
        state.velocity.add(new Vector3d(acceleration).div(20));

        state.angularVelocity.mul(1.0 - config.getAngularVelocityDamping());
        Vector3d angularAcceleration = VehicleForces.getAngularAcceleration(config, forces);
        state.angularVelocity.add(new Vector3d(angularAcceleration).div(20));

        Quaterniond rotationQuaternion = Utils.getRotationEulerAngles(state.rotation);
        Quaterniond negativeRotation = new Quaterniond().rotateAxis(-rotationQuaternion.angle(), rotationQuaternion.x, rotationQuaternion.y, rotationQuaternion.z);
        Vector3d relativeAngularVelocity = new Vector3d(state.angularVelocity).rotate(negativeRotation);

        if (isOnGround) {
            angularAcceleration.y -= config.getGroundYawDamping() * state.getYaw();
            relativeAngularVelocity.z -= config.getGroundPitchDamping() * state.getPitch();
        }

        state.rotation.set(Utils.getRotationEulerAngles(state.rotation)
                .mul(Utils.getRotationAngleAxis(new Vector3d(relativeAngularVelocity).div(20)))
                .getEulerAnglesXYZ(new Vector3d()));

        if (ENABLE_DEBUG_ARROWS) {
            VehicleDebug.tickDebug(pig, config, state, isOnGround);
        }

        config.update(state);

        state.write(pig);

        Vector3d pigVelocity = new Vector3d(state.velocity);
        if (pigVelocity.length() > 100) {
            pigVelocity.set(0);
        }
        pig.setVelocity(Vector.fromJOML(new Vector3d(pigVelocity).div(20)));

        for (Entry<String, ModelAdvancedCuboid> entry : config.getCuboids(state).entrySet()) {
            Display display = state.componentGroup.getDisplays().get(entry.getKey());
            if (display != null) {
                display.setTransformationMatrix(Utils.getComponentMatrix(entry.getValue(), state.rotation));
            }
        }

        VehicleHud.update(state, pig.getLocation());

        if (pig.wouldCollideUsing(pig.getBoundingBox().expand(0.1, 0.1, 0.1))) {
//            remove(pig, componentGroup, hudGroup);
        }
    }
}
