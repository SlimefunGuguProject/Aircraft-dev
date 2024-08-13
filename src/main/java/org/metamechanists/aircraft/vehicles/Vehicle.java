package org.metamechanists.aircraft.vehicles;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.utils.id.simple.DisplayGroupId;
import org.metamechanists.aircraft.vehicles.forces.SpatialForce;
import org.metamechanists.aircraft.vehicles.forces.SpatialForceType;
import org.metamechanists.aircraft.vehicles.hud.VehicleHud;
import org.metamechanists.aircraft.vehicles.surfaces.ControlSurfaceOrientation;
import org.metamechanists.displaymodellib.builders.InteractionBuilder;
import org.metamechanists.displaymodellib.models.ModelBuilder;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedCuboid;
import org.metamechanists.displaymodellib.models.components.ModelCuboid;
import org.metamechanists.displaymodellib.sefilib.entity.display.DisplayGroup;
import org.metamechanists.displaymodellib.transformations.TransformationMatrixBuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.Math.toRadians;


public class Vehicle extends SlimefunItem {
    private final String id;
    private VehicleDescription description;
    private static final boolean ENABLE_DEBUG_ARROWS = false;

    public Vehicle(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, String id, VehicleDescription description) {
        super(itemGroup, item, recipeType, recipe);
        this.id = id;
        this.description = description;
        addItemHandler(onItemUse());
    }

    public void reload() {
        description = new VehicleDescription(description.getPath());
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
        DisplayGroup componentGroup = buildComponents(block.getLocation());
        VehicleState state = new VehicleState(
                0,
                new Vector3d(),
                new Vector3d(0, toRadians(-90.0-player.getEyeLocation().getYaw()), 0),
                new Vector3d(),
                description.initializeOrientations(),
                componentGroup,
                null
        );

        state.hudGroup = buildHud(state, block.getLocation());

        Pig pig = (Pig) block.getWorld().spawnEntity(block.getLocation().clone().toCenterLocation().add(new Vector(0, -0.5, 0)), EntityType.PIG);
        pig.setInvulnerable(true);
        pig.setGravity(false);
        pig.setInvisible(true);
        pig.setSilent(true);
        pig.addPassenger(componentGroup.getParentDisplay());
        pig.addPassenger(state.hudGroup.getParentDisplay());

        componentGroup.getDisplays().values().forEach(pig::addPassenger);
        state.hudGroup.getDisplays().values().forEach(pig::addPassenger);

        createInteraction(pig);
        
        state.write(pig);

        PersistentDataTraverser traverser = new PersistentDataTraverser(pig);
        traverser.set("id", id);
        traverser.set("player", player.getUniqueId());

        Storage.add(pig.getUniqueId());
    }

    private @NotNull DisplayGroup buildComponents(Location location) {
        ModelBuilder builder = new ModelBuilder();
        description.getCuboids(description.initializeOrientations()).forEach(builder::add);
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

    private Vector3d getAcceleration(@NotNull Set<SpatialForce> forces) {
        Vector3d resultantForce = new Vector3d();
        forces.stream().map(SpatialForce::force).forEach(resultantForce::add);
        return new Vector3d(resultantForce).div(description.getMass());
    }

    private Vector3d getAngularAcceleration(@NotNull Set<SpatialForce> forces) {
        Set<Vector3d> torqueVectors = forces.stream().map(SpatialForce::getTorqueVector).collect(Collectors.toSet());
        Vector3d resultantTorque = new Vector3d();
        torqueVectors.forEach(resultantTorque::add);

        return new Vector3d(resultantTorque).div(description.getMomentOfInertia());
    }

    private static void cancelVelocityAndAcceleration(Vector3d velocity, Vector3d acceleration, @NotNull Pig pig) {
        if (pig.wouldCollideUsing(pig.getBoundingBox().shift(new Vector(-0.1, 0.0, 0.0)))) {
            velocity.x = Math.max(velocity.x, 0.0);
            acceleration.x = Math.max(acceleration.x, 0.0);
        }

        if (pig.wouldCollideUsing(pig.getBoundingBox().shift(new Vector(0.1, 0.0, 0.0)))) {
            velocity.x = Math.min(velocity.x, 0.0);
            acceleration.x = Math.min(acceleration.x, 0.0);
        }

        if (pig.wouldCollideUsing(pig.getBoundingBox().shift(new Vector(0.0, -0.1, 0.0)))) {
            velocity.y = Math.max(velocity.y, 0.0);
            acceleration.y = Math.max(acceleration.y, 0.0);
        }

        if (pig.wouldCollideUsing(pig.getBoundingBox().shift(new Vector(0.0, 0.1, 0.0)))) {
            velocity.y = Math.min(velocity.y, 0.0);
            acceleration.y = Math.min(acceleration.y, 0.0);
        }

        if (pig.wouldCollideUsing(pig.getBoundingBox().shift(new Vector(0.0, 0.0, -0.1)))) {
            velocity.z = Math.max(velocity.z, 0.0);
            acceleration.z = Math.max(acceleration.z, 0.0);
        }

        if (pig.wouldCollideUsing(pig.getBoundingBox().shift(new Vector(0.0, 0.0, 0.1)))) {
            velocity.z = Math.min(velocity.z, 0.0);
            acceleration.z = Math.min(acceleration.z, 0.0);
        }
    }

    private void tickDebug(Pig pig, VehicleState state, boolean isOnGround) {
        PersistentDataTraverser traverser = new PersistentDataTraverser(pig);
        DisplayGroupId forceArrowGroupId = traverser.getDisplayGroupId("forceArrowGroupId");
        DisplayGroup forceArrowGroup;
        if (forceArrowGroupId == null || forceArrowGroupId.get().isEmpty()) {
            forceArrowGroup = new DisplayGroup(pig.getLocation());
            forceArrowGroupId = new DisplayGroupId(forceArrowGroup.getParentUUID());
            traverser.set("forceArrowGroupId", forceArrowGroupId);
            pig.addPassenger(forceArrowGroup.getParentDisplay());
        } else {
            forceArrowGroup = forceArrowGroupId.get().get();
        }

        Set<String> notUpdated = new HashSet<>(forceArrowGroup.getDisplays().keySet());
        for (SpatialForce force : getForces(isOnGround, state)) {
            String id = force.relativeLocation().toString() + force.type().toString();
            notUpdated.remove(id);
            Display display = forceArrowGroup.getDisplays().get(id);

            if (display == null) {
                Material material = switch (force.type()) {
                    case DRAG -> Material.BLUE_CONCRETE;
                    case LIFT -> Material.LIME_CONCRETE;
                    case WEIGHT -> Material.ORANGE_CONCRETE;
                    case THRUST -> Material.PURPLE_CONCRETE;
                    case FRICTION -> Material.YELLOW_CONCRETE;
                };

                display = new ModelCuboid()
                        .material(material)
                        .brightness(15)
                        .size(0.1F, 0.01F, 0.01F)
                        .build(pig.getLocation());
                forceArrowGroup.addDisplay(id, display);
                pig.addPassenger(display);
            }

            display.setTransformationMatrix(new TransformationMatrixBuilder()
                    .translate(new Vector3f((float) force.absoluteLocation().x, (float) force.absoluteLocation().y, (float) force.absoluteLocation().z))
                    .translate(0.0F, 1.2F, 0.0F)
                    .lookAlong(new Vector3f((float) force.force().x, (float) force.force().y, (float) force.force().z))
                    .translate(0.0F, 0.0F, (float) force.force().length())
                    .scale(0.1F, 0.1F, 2.0F * (float) force.force().length())
                    .buildForBlockDisplay());
        }

        for (String id : notUpdated) {
            Display display = forceArrowGroup.removeDisplay(id);
            if (display != null) {
                display.remove();
            }
        }
    }

    public void tickAircraft(@NotNull Pig pig) {
        VehicleState state = VehicleState.fromPig(pig);
        if (state == null) {
            return;
        }

        boolean isOnGround = pig.wouldCollideUsing(pig.getBoundingBox().shift(new Vector(0.0, -0.1, 0.0)));

        Set<SpatialForce> forces = getForces(isOnGround, state);

        state.velocity.mul(1.0 - description.getVelocityDamping());
        Vector3d acceleration = getAcceleration(forces);
        cancelVelocityAndAcceleration(state.velocity, acceleration, pig);
        state.velocity.add(new Vector3d(acceleration).div(20));

        state.angularVelocity.mul(1.0 - description.getAngularVelocityDamping());
        Vector3d angularAcceleration = getAngularAcceleration(forces);
        state.angularVelocity.add(new Vector3d(angularAcceleration).div(20));

        Quaterniond rotationQuaternion = Utils.getRotationEulerAngles(state.rotation);
        Quaterniond negativeRotation = new Quaterniond().rotateAxis(-rotationQuaternion.angle(), rotationQuaternion.x, rotationQuaternion.y, rotationQuaternion.z);
        Vector3d relativeAngularVelocity = new Vector3d(state.angularVelocity).rotate(negativeRotation);

        if (isOnGround) {
            angularAcceleration.y -= description.getGroundYawDamping() * state.getYaw();
            relativeAngularVelocity.z -= description.getGroundPitchDamping() * state.getPitch();
        }

        state.rotation.set(Utils.getRotationEulerAngles(state.rotation)
                .mul(Utils.getRotationAngleAxis(new Vector3d(relativeAngularVelocity).div(20)))
                .getEulerAnglesXYZ(new Vector3d()));

        if (ENABLE_DEBUG_ARROWS) {
            tickDebug(pig, state, isOnGround);
        }

        description.moveHingeComponentsToCenter(state.orientations);

        state.write(pig);

        Vector3d pigVelocity = new Vector3d(state.velocity);
        if (pigVelocity.length() > 100) {
            pigVelocity.set(0);
        }
        pig.setVelocity(Vector.fromJOML(new Vector3d(pigVelocity).div(20)));

        for (Entry<String, ModelAdvancedCuboid> entry : description.getCuboids(state.orientations).entrySet()) {
            Display display = state.componentGroup.getDisplays().get(entry.getKey());
            if (display != null) {
                display.setTransformationMatrix(Utils.getComponentMatrix(entry.getValue(), state.rotation));
            }
        }

        VehicleHud.update(state, pig.getLocation(), acceleration.length(), angularAcceleration.length());

        if (pig.wouldCollideUsing(pig.getBoundingBox().expand(0.1, 0.1, 0.1))) {
//            remove(pig, componentGroup, hudGroup);
        }
    }

    private @NotNull Set<SpatialForce> getForces(boolean isOnGround, VehicleState state) {
        Set<SpatialForce> forces = new HashSet<>();
        forces.add(getWeightForce(state));
        forces.add(getThrustForce(state));
        forces.addAll(getDragForces(state));
        forces.addAll(getLiftForces(state));
        forces.add(getFrictionForce(isOnGround, state, getAcceleration(forces).mul(description.getMass())));
        return forces;
    }

    private @NotNull SpatialForce getWeightForce(@NotNull VehicleState state) {
        return new SpatialForce(
                SpatialForceType.WEIGHT,
                new Vector3d(0, description.getGravityAcceleration() * description.getMass(), 0),
                description.getWeightLocation(),
                Utils.rotateByEulerAngles(description.getWeightLocation(), state.rotation));
    }

    private @NotNull SpatialForce getFrictionForce(boolean isOnGround, VehicleState state, Vector3d force) {
        if (!isOnGround || state.velocity.length() < 0.0001) {
            return new SpatialForce(SpatialForceType.FRICTION, new Vector3d(), new Vector3d(), new Vector3d());
        }

        double horizontalForce = new Vector3d(state.velocity.x, 0.0, state.velocity.z)
                .mul(description.getMass())
                .length();
        double horizontalVelocity = new Vector3d(state.velocity.x, 0.0, state.velocity.z)
                .length();

        double frictionAmount = Math.abs(force.y) * description.getFrictionCoefficient();
        if (horizontalVelocity < 0.01) {
            // Stationary; limiting equilibrium
            frictionAmount = Math.min(frictionAmount, horizontalForce);
        }

        Vector3d frictionForce = new Vector3d(state.velocity)
                .normalize()
                .mul(-frictionAmount);
        return new SpatialForce(SpatialForceType.FRICTION, frictionForce, new Vector3d(), new Vector3d());
    }

    private @NotNull SpatialForce getThrustForce(@NotNull VehicleState state) {
        double throttleFraction = state.throttle / 100.0;
        return new SpatialForce(
                SpatialForceType.THRUST,
                Utils.rotateByEulerAngles(new Vector3d(throttleFraction * description.getThrust(), 0, 0), state.rotation),
                description.getThrustLocation(),
                Utils.rotateByEulerAngles(description.getThrustLocation(), state.rotation));
    }

    private Set<SpatialForce> getDragForces(@NotNull VehicleState state) {
        return description.getSurfaces(state.orientations).stream()
                .map(vehicleSurface -> vehicleSurface.getDragForce(description.getAirDensity(), state))
                .collect(Collectors.toSet());
    }

    private @NotNull Set<SpatialForce> getLiftForces(@NotNull VehicleState state) {
        return description.getSurfaces(state.orientations).stream()
                .map(vehicleSurface -> vehicleSurface.getLiftForce(description.getAirDensity(), state))
                .collect(Collectors.toSet());
    }


    public void onKey(@NotNull PersistentDataTraverser traverser, char key) {
        Map<String, ControlSurfaceOrientation> orientations = traverser.getControlSurfaceOrientations("orientations");
        description.adjustHingeComponents(orientations, key);
        traverser.setControlSurfaceOrientations("orientations", orientations);
    }
}
