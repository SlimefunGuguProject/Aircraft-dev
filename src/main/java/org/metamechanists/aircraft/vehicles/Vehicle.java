package org.metamechanists.aircraft.vehicles;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Cod;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.utils.id.simple.DisplayGroupId;
import org.metamechanists.displaymodellib.models.ModelBuilder;
import org.metamechanists.displaymodellib.sefilib.entity.display.DisplayGroup;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.toRadians;


public class Vehicle extends SlimefunItem {
    private final String name;
    private VehicleDescription description;

    public static final SlimefunItemStack GLIDER = new SlimefunItemStack(
            "ACR_TEST_AIRCRAFT",
            Material.FEATHER,
            "&4&ljustin don't hurt me",
            "&cpls");

    public Vehicle(final ItemGroup itemGroup, final SlimefunItemStack item, final RecipeType recipeType, final ItemStack[] recipe, final String name, final VehicleDescription description) {
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
            final Player player = event.getPlayer();
            if (event.getClickedBlock().isPresent() && !player.isInsideVehicle()) {
                if (player.getGameMode() != GameMode.CREATIVE) {
                    player.getInventory().getItemInMainHand().subtract();
                }
                place(event.getClickedBlock().get().getRelative(event.getClickedFace()), player);
            }
        };
    }

    private void place(final @NotNull Block block, final @NotNull Player player) {
        final DisplayGroup componentGroup = buildComponents(block.getLocation());
        final DisplayGroup hudGroup = buildHud(block.getLocation(), new Vector3d());

        final Cod seat = (Cod) block.getWorld().spawnEntity(block.getLocation(), EntityType.COD);
        seat.setInvulnerable(true);
        seat.setGravity(false);
        seat.setInvisible(true);
        seat.setSilent(true);

        seat.addPassenger(componentGroup.getParentDisplay());
        seat.addPassenger(hudGroup.getParentDisplay());
        componentGroup.getDisplays().values().forEach(seat::addPassenger);
        hudGroup.getDisplays().values().forEach(seat::addPassenger);
        seat.addPassenger(player);

        final PersistentDataTraverser traverser = new PersistentDataTraverser(seat);
        traverser.set("name", name);
        traverser.set("velocity", new Vector3d());
        traverser.set("angularVelocity", new Vector3d());
        traverser.set("rotation", new Vector3d(0, toRadians(-90.0-player.getEyeLocation().getYaw()), 0));
        traverser.set("player", player.getUniqueId());
        traverser.set("componentGroupId", new DisplayGroupId(componentGroup.getParentUUID()));
        traverser.set("hudGroupId", new DisplayGroupId(hudGroup.getParentUUID()));
        traverser.setControlSurfaceOrientations("orientations", description.initializeOrientations());

        VehicleStorage.add(seat.getUniqueId());
    }
    private @NotNull DisplayGroup buildComponents(final Location location) {
        final ModelBuilder builder = new ModelBuilder();
        description.getCuboids(description.initializeOrientations()).forEach(builder::add);
        return builder.buildAtBlockCenter(location);
    }
    private @NotNull DisplayGroup buildHud(final Location location, final Vector3d rotation) {
        final ModelBuilder builder = new ModelBuilder();
        description.getHud(rotation).forEach(builder::add);
        return builder.buildAtBlockCenter(location);
    }

    private static @NotNull Optional<Player> getPilot(final @NotNull Cod seat) {
        return seat.getPassengers().stream()
                .filter(entity -> entity instanceof Player)
                .map(Player.class::cast)
                .findFirst();
    }

    private static void remove(final @NotNull Cod seat, final @NotNull DisplayGroup componentGroup, final @NotNull DisplayGroup hudGroup) {
        componentGroup.remove();
        hudGroup.remove();
        VehicleStorage.remove(seat.getUniqueId());
        seat.getLocation().createExplosion(4);
        getPilot(seat).ifPresent(pilot -> {
            pilot.eject();
            componentGroup.getParentDisplay().removePassenger(pilot);
        });
        seat.remove();
    }

    private Vector3d getAcceleration(final @NotNull Set<SpatialForce> forces) {
        final Vector3d resultantForce = new Vector3d();
        forces.stream().map(SpatialForce::getForce).forEach(resultantForce::add);
        return new Vector3d(resultantForce).div(description.getMass()).div(400);
    }

    private Vector3d getAngularAcceleration(final @NotNull Set<SpatialForce> forces, final @NotNull Vector3d rotation) {
        final Set<Vector3d> torqueVectors = forces.stream().map(SpatialForce::getTorqueVector).collect(Collectors.toSet());
        final Vector3d resultantTorque = new Vector3d();
        torqueVectors.forEach(resultantTorque::add);

        return new Vector3d(resultantTorque).div(description.getMomentOfInertia()).div(400);
    }

    public void tickAircraft(final @NotNull Cod seat) {
        final PersistentDataTraverser traverser = new PersistentDataTraverser(seat);
        final Vector3d velocity = traverser.getVector3d("velocity");
        final Vector3d rotation = traverser.getVector3d("rotation");
        final Vector3d angularVelocity = traverser.getVector3d("angularVelocity");
        final Map<String, ControlSurfaceOrientation> orientations = traverser.getControlSurfaceOrientations("orientations");
        final DisplayGroupId componentGroupId = traverser.getDisplayGroupId("componentGroupId");
        final DisplayGroupId hudGroupId = traverser.getDisplayGroupId("hudGroupId");
        if (velocity == null || angularVelocity == null || rotation == null || orientations == null
                || componentGroupId == null || componentGroupId.get().isEmpty()
                || hudGroupId == null || hudGroupId.get().isEmpty()) {
            return;
        }

        final DisplayGroup componentGroup = componentGroupId.get().get();
        final DisplayGroup hudGroup = hudGroupId.get().get();
        final Set<SpatialForce> forces = getForces(velocity, rotation, angularVelocity, orientations);

        description.applyVelocityDampening(velocity);
        velocity.add(getAcceleration(forces));

        angularVelocity.add(getAngularAcceleration(forces, rotation));
        description.applyAngularVelocityDampening(angularVelocity);

        final Quaterniond rotationQuaternion = Utils.getRotationEulerAngles(rotation);
        final Quaterniond negativeRotation = new Quaterniond().rotateAxis(-rotationQuaternion.angle(), rotationQuaternion.x, rotationQuaternion.y, rotationQuaternion.z);
        final Vector3d relativeAngularVelocity = new Vector3d(angularVelocity).rotate(negativeRotation);

        rotation.set(Utils.getRotationEulerAngles(rotation)
                .mul(Utils.getRotationAngleAxis(relativeAngularVelocity))
                .getEulerAnglesXYZ(new Vector3d()));

        description.moveHingeComponentsToCenter(orientations);

        traverser.set("velocity", velocity);
        traverser.set("angularVelocity", angularVelocity);
        traverser.set("rotation", rotation);
        traverser.setControlSurfaceOrientations("orientations", orientations);

        seat.setVelocity(Vector.fromJOML(velocity));
        description.getCuboids(orientations).forEach((cuboidName, cuboid) -> componentGroup.getDisplays().get(cuboidName).setTransformationMatrix(Utils.getRotatedMatrix(cuboid, rotation)));
        description.updateHud(rotation, seat.getLocation().getBlockY(), hudGroup);

        getPilot(seat).ifPresent(pilot -> {});

        if (seat.wouldCollideUsing(seat.getBoundingBox().expand(0.1, -0.1, 0.1))) {
            remove(seat, componentGroup, hudGroup);
        }
    }

    private @NotNull Set<SpatialForce> getForces(final Vector3d velocity, final Vector3d rotation, final Vector3d angularVelocity, final @NotNull Map<String, ControlSurfaceOrientation> orientations) {
        final Set<SpatialForce> forces = new HashSet<>();
        forces.add(getWeightForce(rotation));
        forces.add(getThrustForce(rotation));
        forces.addAll(getDragForces(rotation, velocity, angularVelocity, orientations));
        forces.addAll(getLiftForces(rotation, velocity, angularVelocity, orientations));
        return forces;
    }
    private @NotNull SpatialForce getWeightForce(final @NotNull Vector3d rotation) {
        return new SpatialForce(
                new Vector3d(0, -10 * description.getMass(), 0),
                Utils.rotateByEulerAngles(new Vector3d(description.getCenterOfMass()), rotation));
    }
    private @NotNull SpatialForce getThrustForce(final @NotNull Vector3d rotation) {
        return new SpatialForce(
                Utils.rotateByEulerAngles(new Vector3d(description.getThrust(), 0, 0), rotation),
                Utils.rotateByEulerAngles(new Vector3d(description.getCenterOfMass()), rotation));
    }
    private Set<SpatialForce> getDragForces(final Vector3d rotation, final Vector3d velocity, final Vector3d angularVelocity, final @NotNull Map<String, ControlSurfaceOrientation> orientations) {
        return description.getSurfaces(orientations).stream()
                .map(vehicleSurface -> vehicleSurface.getDragForce(description.getAirDensity(), rotation, velocity, angularVelocity))
                .collect(Collectors.toSet());
    }
    private @NotNull Set<SpatialForce> getLiftForces(final Vector3d rotation, final Vector3d velocity, final Vector3d angularVelocity, final @NotNull Map<String, ControlSurfaceOrientation> orientations) {
        return description.getSurfaces(orientations).stream()
                .map(vehicleSurface -> vehicleSurface.getLiftForce(description.getAirDensity(), rotation, velocity, angularVelocity))
                .collect(Collectors.toSet());
    }

    public void onKey(final @NotNull PersistentDataTraverser traverser, final char key) {
        final Map<String, ControlSurfaceOrientation> orientations = traverser.getControlSurfaceOrientations("orientations");
        description.adjustHingeComponents(orientations, key);
        traverser.setControlSurfaceOrientations("orientations", orientations);
    }
}
