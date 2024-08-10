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
import org.metamechanists.aircraft.utils.PersistentDataTraverser;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.utils.id.simple.DisplayGroupId;
import org.metamechanists.displaymodellib.builders.InteractionBuilder;
import org.metamechanists.displaymodellib.models.ModelBuilder;
import org.metamechanists.displaymodellib.sefilib.entity.display.DisplayGroup;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.Math.toRadians;


public class Vehicle extends SlimefunItem {
    private final String name;
    private VehicleDescription description;

    public static final SlimefunItemStack LIGHT_AIRCRAFT = new SlimefunItemStack(
            "AIRCRAFT_LIGHT_AIRCRAFT",
            Material.FEATHER,
            "&4&ljustin don't hurt me",
            "&cpls");

    public Vehicle(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, String name, VehicleDescription description) {
        super(itemGroup, item, recipeType, recipe);
        this.name = name;
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

    public static void mount(@NotNull Pig seat, @NotNull Player player) {
        UUID uuid = new PersistentDataTraverser(seat).getUuid("interactionId");
        if (uuid != null) {
            Entity interaction = Bukkit.getEntity(uuid);
            if (interaction != null) {
                interaction.remove();
            }
        }

        new PersistentDataTraverser(seat).unset("interactionId");

        seat.addPassenger(player);
        player.setInvisible(true);
    }

    public static void unMount(@NotNull Pig seat, @NotNull Player player) {
        createInteraction(seat);
        player.setInvisible(false);
    }

    private static void createInteraction(@NotNull Pig seat) {
        Interaction interaction = new InteractionBuilder()
                .width(1.2F)
                .height(1.2F)
                .build(seat.getLocation());

        new PersistentDataTraverser(interaction).set("seatId", seat.getUniqueId());
        new PersistentDataTraverser(seat).set("interactionId", interaction.getUniqueId());

        seat.addPassenger(interaction);
    }

    private void place(@NotNull Block block, @NotNull Player player) {
        DisplayGroup componentGroup = buildComponents(block.getLocation());
        DisplayGroup hudGroup = buildHud(block.getLocation(), new Vector3d());


        Pig seat = (Pig) block.getWorld().spawnEntity(block.getLocation(), EntityType.PIG);
        seat.setInvulnerable(true);
        seat.setGravity(false);
        seat.setInvisible(true);
        seat.setSilent(true);
        seat.addPassenger(componentGroup.getParentDisplay());
        seat.addPassenger(hudGroup.getParentDisplay());

        componentGroup.getDisplays().values().forEach(seat::addPassenger);
        hudGroup.getDisplays().values().forEach(seat::addPassenger);

        createInteraction(seat);

        PersistentDataTraverser traverser = new PersistentDataTraverser(seat);
        traverser.set("name", name);
        traverser.set("throttle", 0);
        traverser.set("velocity", new Vector3d());
        traverser.set("angularVelocity", new Vector3d());
        traverser.set("rotation", new Vector3d(0, toRadians(-90.0-player.getEyeLocation().getYaw()), 0));
        traverser.set("player", player.getUniqueId());
        traverser.set("componentGroupId", new DisplayGroupId(componentGroup.getParentUUID()));
        traverser.set("hudGroupId", new DisplayGroupId(hudGroup.getParentUUID()));
        traverser.setControlSurfaceOrientations("orientations", description.initializeOrientations());

        Storage.add(seat.getUniqueId());
    }
    private @NotNull DisplayGroup buildComponents(Location location) {
        ModelBuilder builder = new ModelBuilder();
        description.getCuboids(description.initializeOrientations()).forEach(builder::add);
        return builder.buildAtBlockCenter(location);
    }
    private static @NotNull DisplayGroup buildHud(Location location, Vector3d rotation) {
        ModelBuilder builder = new ModelBuilder();
        VehicleHud.getHud(rotation).forEach(builder::add);
        return builder.buildAtBlockCenter(location);
    }

    private static @NotNull Optional<Player> getPilot(@NotNull Pig seat) {
        return seat.getPassengers().stream()
                .filter(entity -> entity instanceof Player)
                .map(Player.class::cast)
                .findFirst();
    }

    private static void remove(@NotNull Pig seat, @NotNull Interaction interaction, @NotNull DisplayGroup componentGroup, @NotNull DisplayGroup hudGroup) {
        interaction.remove();
        componentGroup.remove();
        hudGroup.remove();
        Storage.remove(seat.getUniqueId());
        seat.getLocation().createExplosion(4);
        getPilot(seat).ifPresent(pilot -> {
            pilot.setInvisible(false);
            pilot.eject();
            componentGroup.getParentDisplay().removePassenger(pilot);
        });
        seat.remove();
    }

    private Vector3d getAcceleration(@NotNull Set<SpatialForce> forces) {
        Vector3d resultantForce = new Vector3d();
        forces.stream().map(SpatialForce::getForce).forEach(resultantForce::add);
        return new Vector3d(resultantForce).div(description.getMass()).div(20);
    }

    private Vector3d getAngularAcceleration(@NotNull Set<SpatialForce> forces) {
        Set<Vector3d> torqueVectors = forces.stream().map(SpatialForce::getTorqueVector).collect(Collectors.toSet());
        Vector3d resultantTorque = new Vector3d();
        torqueVectors.forEach(resultantTorque::add);

        return new Vector3d(resultantTorque).div(description.getMomentOfInertia()).div(20);
    }

    private static void cancelVelocity(Vector3d velocity, @NotNull Pig seat) {
        if (seat.wouldCollideUsing(seat.getBoundingBox().shift(new Vector(-0.1, 0.0, 0.0)))) {
            velocity.x = Math.max(velocity.x, 0.0);
        }

        if (seat.wouldCollideUsing(seat.getBoundingBox().shift(new Vector(0.1, 0.0, 0.0)))) {
            velocity.x = Math.min(velocity.x, 0.0);
        }

        if (seat.wouldCollideUsing(seat.getBoundingBox().shift(new Vector(0.0, -0.1, 0.0)))) {
            velocity.y = Math.max(velocity.y, 0.0);
        }

        if (seat.wouldCollideUsing(seat.getBoundingBox().shift(new Vector(0.0, 0.1, 0.0)))) {
            velocity.y = Math.min(velocity.y, 0.0);
        }

        if (seat.wouldCollideUsing(seat.getBoundingBox().shift(new Vector(0.0, 0.0, -0.1)))) {
            velocity.z = Math.max(velocity.z, 0.0);
        }

        if (seat.wouldCollideUsing(seat.getBoundingBox().shift(new Vector(0.0, 0.0, 0.1)))) {
            velocity.z = Math.min(velocity.z, 0.0);
        }
    }

    public void tickAircraft(@NotNull Pig seat) {
        PersistentDataTraverser traverser = new PersistentDataTraverser(seat);
        int throttle = traverser.getInt("throttle");
        Vector3d velocity = traverser.getVector3d("velocity");
        Vector3d rotation = traverser.getVector3d("rotation");
        Vector3d angularVelocity = traverser.getVector3d("angularVelocity");
        Map<String, ControlSurfaceOrientation> orientations = traverser.getControlSurfaceOrientations("orientations");
        DisplayGroupId componentGroupId = traverser.getDisplayGroupId("componentGroupId");
        DisplayGroupId hudGroupId = traverser.getDisplayGroupId("hudGroupId");
        if (velocity == null || angularVelocity == null || rotation == null || orientations == null
                || componentGroupId == null || componentGroupId.get().isEmpty()
                || hudGroupId == null || hudGroupId.get().isEmpty()) {
            return;
        }

        DisplayGroup componentGroup = componentGroupId.get().get();
        DisplayGroup hudGroup = hudGroupId.get().get();
        Set<SpatialForce> forces = getForces(throttle, velocity, rotation, angularVelocity, orientations);

        description.applyVelocityDampening(velocity);
        Vector3d acceleration = getAcceleration(forces);
        velocity.add(acceleration);

        cancelVelocity(velocity, seat);

        boolean isOnGround = seat.wouldCollideUsing(seat.getBoundingBox().shift(new Vector(0.0, -0.1, 0.0)));
        if (isOnGround) {
            Vector3d friction = new Vector3d(velocity)
                    .normalize()
                    .mul(-1.0)
                    .mul(Math.abs(acceleration.y))
                    .mul(description.getFrictionCoefficient());
            velocity.sub(friction);
        }

        angularVelocity.add(getAngularAcceleration(forces));
        description.applyAngularVelocityDampening(angularVelocity);

        Quaterniond rotationQuaternion = Utils.getRotationEulerAngles(rotation);
        Quaterniond negativeRotation = new Quaterniond().rotateAxis(-rotationQuaternion.angle(), rotationQuaternion.x, rotationQuaternion.y, rotationQuaternion.z);
        Vector3d relativeAngularVelocity = new Vector3d(angularVelocity).rotate(negativeRotation);

        rotation.set(Utils.getRotationEulerAngles(rotation)
                .mul(Utils.getRotationAngleAxis(new Vector3d(relativeAngularVelocity).div(20)))
                .getEulerAnglesXYZ(new Vector3d()));

        description.moveHingeComponentsToCenter(orientations);

        traverser.set("velocity", velocity);
        traverser.set("angularVelocity", angularVelocity);
        traverser.set("rotation", rotation);
        traverser.setControlSurfaceOrientations("orientations", orientations);

        Vector3d seatLocation = new Vector3d(description.getAbsoluteCenterOfMass(rotation)).mul(-1); // todo maybe must be absolute?
        Vector3d angularSeatVelocityVector = new Vector3d(angularVelocity).cross(seatLocation).div(20);
        Vector3d seatVelocity = new Vector3d(velocity).div(20).add(angularSeatVelocityVector);
        if (seatVelocity.length() > 5) {
            seatVelocity.set(0);
        }
        seat.setVelocity(Vector.fromJOML(seatVelocity));
        description.getCuboids(orientations).forEach((cuboidName, cuboid) -> componentGroup.getDisplays().get(cuboidName)
                        .setTransformationMatrix(Utils.getComponentMatrix(cuboid, rotation, description.getAbsoluteCenterOfMass(rotation))));
        VehicleHud.updateHud(rotation, seat.getLocation().getBlockY(), hudGroup);

        getPilot(seat).ifPresent(pilot -> {});

        if (seat.wouldCollideUsing(seat.getBoundingBox().expand(0.1, -0.1, 0.1))) {
//            remove(seat, componentGroup, hudGroup);
        }
    }

    private @NotNull Set<SpatialForce> getForces(int throttle, Vector3d velocity, Vector3d rotation, Vector3d angularVelocity, @NotNull Map<String, ControlSurfaceOrientation> orientations) {
        Set<SpatialForce> forces = new HashSet<>();
        forces.add(getWeightForce());
        forces.add(getThrustForce(throttle, rotation));
        forces.addAll(getDragForces(rotation, velocity, angularVelocity, orientations));
        forces.addAll(getLiftForces(rotation, velocity, angularVelocity, orientations));
        return forces;
    }
    private @NotNull SpatialForce getWeightForce() {
        return new SpatialForce(
                new Vector3d(0, description.getGravityAcceleration() * description.getMass(), 0),
                new Vector3d(0, 0, 0));
    }
    private @NotNull SpatialForce getThrustForce(int throttle, @NotNull Vector3d rotation) {
        double throttleFraction = throttle / 100.0;
        return new SpatialForce(
                Utils.rotateByEulerAngles(new Vector3d(throttleFraction * description.getThrust(), 0, 0), rotation),
                new Vector3d(0, 0, 0));
    }
    private Set<SpatialForce> getDragForces(Vector3d rotation, Vector3d velocity, Vector3d angularVelocity, @NotNull Map<String, ControlSurfaceOrientation> orientations) {
        return description.getSurfaces(orientations).stream()
                .map(vehicleSurface -> vehicleSurface.getDragForce(description.getAirDensity(), rotation, velocity, angularVelocity))
                .collect(Collectors.toSet());
    }
    private @NotNull Set<SpatialForce> getLiftForces(Vector3d rotation, Vector3d velocity, Vector3d angularVelocity, @NotNull Map<String, ControlSurfaceOrientation> orientations) {
        return description.getSurfaces(orientations).stream()
                .map(vehicleSurface -> vehicleSurface.getLiftForce(description.getAirDensity(), rotation, velocity, angularVelocity))
                .collect(Collectors.toSet());
    }

    public void onKey(@NotNull PersistentDataTraverser traverser, char key) {
        Map<String, ControlSurfaceOrientation> orientations = traverser.getControlSurfaceOrientations("orientations");
        description.adjustHingeComponents(orientations, key);
        traverser.setControlSurfaceOrientations("orientations", orientations);
    }
}
