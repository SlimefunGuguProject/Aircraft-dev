package org.metamechanists.aircraft.vehicles;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.vehicles.forces.SpatialForce;
import org.metamechanists.aircraft.vehicles.forces.VehicleForces;
import org.metamechanists.aircraft.vehicles.hud.VehicleHud;
import org.metamechanists.aircraft.vehicles.surfaces.ControlSurfaceOrientation;
import org.metamechanists.displaymodellib.builders.InteractionBuilder;
import org.metamechanists.displaymodellib.models.components.ModelComponent;
import org.metamechanists.displaymodellib.models.components.ModelCuboid;
import org.metamechanists.displaymodellib.models.components.ModelDiamond;
import org.metamechanists.displaymodellib.models.components.ModelItem;
import org.metamechanists.displaymodellib.models.components.ModelText;
import org.metamechanists.displaymodellib.sefilib.entity.display.DisplayGroup;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


public class Vehicle extends SlimefunItem {
    private final String id;
    @Getter
    private VehicleConfig config;
    private static final boolean ENABLE_DEBUG_ARROWS = false;

    public static final int PHYSICS_UPDATES_PER_SECOND = 20;
    public static final int AIRCRAFT_TICK_INTERVAL = 4;

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

        player.setInvisible(true);
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
                new Quaterniond().rotationY(Math.toRadians(-90.0-player.getEyeLocation().getYaw())),
                new Vector3d(),
                config.initializeOrientations(),
                null,
                null
        );

        state.componentGroup = new DisplayGroup(block.getLocation());
        state.hudGroup = new DisplayGroup(block.getLocation());


        Pig pig = (Pig) block.getWorld().spawnEntity(block.getLocation().clone().toCenterLocation().add(new Vector(0, -0.5, 0)), EntityType.PIG);
        pig.setInvulnerable(true);
        pig.setGravity(false);
        pig.setInvisible(true);
        pig.setSilent(true);
        pig.addPassenger(state.componentGroup.getParentDisplay());
        pig.addPassenger(state.hudGroup.getParentDisplay());

        createInteraction(pig);
        
        state.write(pig);

        PersistentDataTraverser traverser = new PersistentDataTraverser(pig);
        traverser.set("id", id);
        traverser.set("player", player.getUniqueId());

        Storage.add(pig.getUniqueId());
    }

    private static @Nullable Float modelComponentviewRange(ModelComponent component) {
        if (component instanceof ModelCuboid cuboid) {
            return cuboid.getMain().getViewRange();
        }

        if (component instanceof ModelDiamond diamond) {
            return diamond.getMain().getViewRange();
        }

        if (component instanceof ModelText text) {
            return text.getMain().getViewRange();
        }

        if (component instanceof ModelItem item) {
            return item.getMain().getViewRange();
        }

        return null;
    }

    private static void updateDisplayGroup(Pig pig, VehicleState state,
                                           Map<String, ModelComponent> previousExpected,
                                           Map<String, ModelComponent> expected,
                                           @NotNull DisplayGroup actual) {
        // Remove components that shouldn't exist any more
        List<String> toRemove = actual.getDisplays()
                .keySet()
                .stream()
                .filter(displayName -> !expected.containsKey(displayName))
                .toList(); // to avoid modifying while iterating

        for (String displayName : toRemove) {
            //noinspection DataFlowIssue
            actual.removeDisplay(displayName).remove();
        }

        // Add new components that do not yet exist
        for (Entry<String, ModelComponent> entry : expected.entrySet()) {
            if (!actual.getDisplays().containsKey(entry.getKey())) {
                Display display = entry.getValue().build(pig.getLocation());
                actual.addDisplay(entry.getKey(), display);
                pig.addPassenger(display);
            }
        }

        // Update transformations + interoplations
        for (Entry<String, Display> entry : actual.getDisplays().entrySet()) {
            ModelComponent expectedComponent = expected.get(entry.getKey());
            ModelComponent previousExpectedComponent = previousExpected.get(entry.getKey());

            Float viewRange = modelComponentviewRange(expectedComponent);
            Float previousViewRange = modelComponentviewRange(previousExpectedComponent);

            Aircraft.getInstance().getLogger().warning(String.valueOf(viewRange));

            if (viewRange != null) {
                entry.getValue().setViewRange(viewRange);
            }

            if (viewRange == null || previousViewRange != null && viewRange != 0 && previousViewRange != 0) {
                entry.getValue().setInterpolationDelay(0);
                entry.getValue().setInterpolationDuration(AIRCRAFT_TICK_INTERVAL);
            }

            if (viewRange == null || viewRange != 0) {
                entry.getValue().setTransformationMatrix(Utils.getComponentMatrix(expectedComponent, state.rotation));
            }
        }
    }

    private void updateOrientations(@NotNull VehicleState state) {
        // Remove orientations that no longer exist
        Map<String, ControlSurfaceOrientation> expected = config.initializeOrientations();
        List<String> toRemove = state.orientations.keySet()
                .stream()
                .filter(key -> !expected.containsKey(key))
                .toList(); // to avoid modifying while iterating
        toRemove.forEach(expected::remove);

        // Add new orientations that do not yet exist
        for (Entry<String, ControlSurfaceOrientation> entry : expected.entrySet()) {
            if (!state.orientations.containsKey(entry.getKey())) {
                state.orientations.put(entry.getKey(), entry.getValue());
            }
        }
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
        // must be done on main thread
        Bukkit.getScheduler().scheduleSyncDelayedTask(Aircraft.getInstance(), () -> {
            VehicleState state = VehicleState.fromPig(pig);
            if (state == null) {
                Aircraft.getInstance().getLogger().warning("Failed to handle onKey: vehicle state of " + pig.getUniqueId() + " is null");
                return;
            }

            config.onKey(state, key);
            state.write(pig);
        });
    }

    public static boolean isOnGround(@NotNull Pig pig) {
        return pig.wouldCollideUsing(pig.getBoundingBox().shift(new Vector(0.0, -0.1, 0.0)));
    }

    private void doPhysicsUpdate(Pig pig, VehicleState state) {
        boolean isOnGround = isOnGround(pig);

        Set<SpatialForce> forces = VehicleForces.getForces(config, state, isOnGround);

        state.velocity.mul(1.0 - config.getVelocityDamping());
        Vector3d acceleration = VehicleForces.getAcceleration(config, forces);
        VehicleForces.cancelVelocityAndAcceleration(pig, state.velocity, acceleration);
        state.velocity.add(new Vector3d(acceleration).div(PHYSICS_UPDATES_PER_SECOND));

        state.angularVelocity.mul(1.0 - config.getAngularVelocityDamping());
        Vector3d angularAcceleration = VehicleForces.getAngularAcceleration(config, state, forces);
        if (isOnGround) {
            angularAcceleration.x -= config.getGroundRollDamping() * state.roll();
            angularAcceleration.z -= config.getGroundPitchDamping() * state.pitch();
        }
        state.angularVelocity.add(new Vector3d(angularAcceleration).div(PHYSICS_UPDATES_PER_SECOND));
        if (state.angularVelocity.length() > 0.00001) { // Check to avoid dividing by 0 in normalize
            state.rotation.rotateAxis(state.angularVelocity.length() / PHYSICS_UPDATES_PER_SECOND, new Vector3d(state.angularVelocity).normalize());
        }
    }

    public void tickAircraft(@NotNull Pig pig) {
        VehicleState state = VehicleState.fromPig(pig);
        if (state == null) {
            return;
        }

        Map<String, ModelComponent> aircraftComponents = config.getCuboids(state);
        Map<String, ModelComponent> hudComponents = VehicleHud.build(state);

        updateOrientations(state);
        updateDisplayGroup(pig, state, Storage.previousAircraftComponents(pig), aircraftComponents, state.componentGroup);
        updateDisplayGroup(pig, state, Storage.previousHudComponents(pig), hudComponents, state.hudGroup);

        Storage.updatePreviousComponents(pig, aircraftComponents, hudComponents);

        for (int i = 0; i < AIRCRAFT_TICK_INTERVAL; i++) {
            doPhysicsUpdate(pig, state);
        }

        if (ENABLE_DEBUG_ARROWS) {
            VehicleDebug.tickDebug(pig, config, state, isOnGround(pig));
        }

        config.update(state);

        state.write(pig);

        Vector3d pigVelocity = new Vector3d(state.velocity);
        if (pigVelocity.length() > 100) {
            pigVelocity.set(0);
        }
        pig.setVelocity(Vector.fromJOML(state.absoluteVelocity().div(PHYSICS_UPDATES_PER_SECOND)));

        VehicleHud.update(state, pig.getLocation());

        if (pig.wouldCollideUsing(pig.getBoundingBox().expand(0.1, 0.1, 0.1))) {
//            remove(pig, componentGroup, hudGroup);
        }
    }
}
