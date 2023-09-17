package org.metamechanists.aircraft.vehicles;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;
import org.metamechanists.aircraft.utils.id.simple.DisplayGroupId;
import org.metamechanists.aircraft.utils.models.ModelBuilder;
import org.metamechanists.metalib.sefilib.entity.display.DisplayGroup;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.toRadians;


public class Vehicle extends SlimefunItem {
    private final String name;
    private final VehicleDescription description;

    private static final double MAX_VELOCITY = 50.0;

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
        final DisplayGroup hudGroup = buildHud(block.getLocation());

        final Pig pig = (Pig) block.getWorld().spawnEntity(block.getLocation(), EntityType.PIG);
        pig.setInvulnerable(true);
        pig.setGravity(false);
        pig.setInvisible(true);
        pig.setSilent(true);

        pig.addPassenger(componentGroup.getParentDisplay());
        pig.addPassenger(hudGroup.getParentDisplay());
        componentGroup.getDisplays().values().forEach(pig::addPassenger);
        hudGroup.getDisplays().values().forEach(pig::addPassenger);
        pig.addPassenger(player);

        final PersistentDataTraverser traverser = new PersistentDataTraverser(pig);
        traverser.set("name", name);
        traverser.set("velocity", new Vector3d());
        traverser.set("angularVelocity", new Quaterniond());
        traverser.set("rotation", new Quaterniond().rotateY(toRadians(-90.0-player.getEyeLocation().getYaw())));
        traverser.set("player", player.getUniqueId());
        traverser.set("componentGroupId", new DisplayGroupId(componentGroup.getParentUUID()));
        traverser.set("hudGroupId", new DisplayGroupId(hudGroup.getParentUUID()));
        traverser.setControlSurfaceOrientations("orientations", description.initializeOrientations());

        VehicleStorage.add(pig.getUniqueId());
    }
    private @NotNull DisplayGroup buildComponents(final Location location) {
        final ModelBuilder builder = new ModelBuilder();
        description.getCuboids(description.initializeOrientations()).forEach(builder::add);
        return builder.buildAtBlockCenter(location);
    }
    private @NotNull DisplayGroup buildHud(final Location location) {
        final ModelBuilder builder = new ModelBuilder();
        description.getHud().forEach(builder::add);
        return builder.buildAtBlockCenter(location);
    }

    private static @NotNull Optional<Player> getPilot(final @NotNull Pig pig) {
        return pig.getPassengers().stream()
                .filter(entity -> entity instanceof Player)
                .map(Player.class::cast)
                .findFirst();
    }

    private static void remove(final @NotNull Pig pig, final @NotNull DisplayGroup componentGroup, final @NotNull DisplayGroup hudGroup) {
        componentGroup.remove();
        hudGroup.remove();
        VehicleStorage.remove(pig.getUniqueId());
        pig.getLocation().createExplosion(4);
        getPilot(pig).ifPresent(pilot -> {
            pilot.eject();
            componentGroup.getParentDisplay().removePassenger(pilot);
        });
        pig.remove();
    }

    private Vector3d getAcceleration(final @NotNull Set<SpatialForce> forces) {
        final Vector3d resultantForce = new Vector3d();
        forces.stream().map(SpatialForce::getForce).forEach(resultantForce::add);
        return new Vector3d(resultantForce).div(description.getMass()).div(400);
    }

    private Quaterniond getAngularAcceleration(final @NotNull Set<SpatialForce> forces, final @NotNull Quaterniond rotation) {
        final Set<Vector3d> torqueVectors = forces.stream().map(SpatialForce::getTorqueVector).collect(Collectors.toSet());
        final Vector3d resultantTorque = new Vector3d();
        torqueVectors.forEach(resultantTorque::add);

        final Quaterniond negativeRotation = new Quaterniond().rotateAxis(-rotation.angle(), rotation.x, rotation.y, rotation.z);
        resultantTorque.rotate(negativeRotation);

        final Vector3d resultantAngularAccelerationVector = new Vector3d(resultantTorque).div(description.getMomentOfInertia()).div(400);
        return new Quaterniond().fromAxisAngleRad(new Vector3d(resultantAngularAccelerationVector).normalize(), resultantAngularAccelerationVector.length()) ;
    }

    public void tickAircraft(final @NotNull Pig pig) {
        final PersistentDataTraverser traverser = new PersistentDataTraverser(pig);
        final Vector3d velocity = traverser.getVector3d("velocity");
        final Quaterniond rotation = traverser.getQuaterniond("rotation");
        final Quaterniond angularVelocity = traverser.getQuaterniond("angularVelocity");
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

        if (velocity.length() > MAX_VELOCITY) {
            velocity.set(0, 0, 0);
        }
        description.applyVelocityDampening(velocity);
        velocity.add(getAcceleration(forces));

        final Quaterniond angularAcceleration = getAngularAcceleration(forces, rotation);
        if (angularAcceleration.angle() != 0) {
            angularVelocity.mul(angularAcceleration);
        }
        description.applyAngularVelocityDampening(angularVelocity);
        rotation.mul(angularVelocity);

        description.moveHingeComponentsToCenter(orientations);

        traverser.set("velocity", velocity);
        traverser.set("angularVelocity", angularVelocity);
        traverser.set("rotation", rotation);
        traverser.setControlSurfaceOrientations("orientations", orientations);

        pig.setVelocity(Vector.fromJOML(velocity));
        description.getCuboids(orientations).forEach((cuboidName, cuboid) -> componentGroup.getDisplays().get(cuboidName).setTransformationMatrix(cuboid.getMatrix(rotation)));
        description.updateHud(rotation, pig.getLocation().getBlockY(), hudGroup);

        getPilot(pig).ifPresent(pilot -> {
            final Location location = pilot.getLocation();
            location.setPitch(pilot.getEyeLocation().getPitch() + 20);
            pilot.teleport(location, TeleportFlag.Relative.PITCH);
        });

        if (pig.wouldCollideUsing(pig.getBoundingBox().expand(0.1, -0.1, 0.1))) {
            remove(pig, componentGroup, hudGroup);
        }
    }

    private @NotNull Set<SpatialForce> getForces(final Vector3d velocity, final Quaterniond rotation, final Quaterniond angularVelocity, final @NotNull Map<String, ControlSurfaceOrientation> orientations) {
        final Set<SpatialForce> forces = new HashSet<>();
        forces.add(getWeightForce());
        forces.add(getThrustForce(rotation));
        forces.addAll(getDragForces(rotation, velocity, angularVelocity, orientations));
        forces.addAll(getLiftForces(rotation, velocity, angularVelocity, orientations));
        return forces;
    }
    private @NotNull SpatialForce getWeightForce() {
        return new SpatialForce(new Vector3d(0, -0.5 * description.getMass(), 0), new Vector3d(0, 0, 0));
    }
    private static @NotNull SpatialForce getThrustForce(final @NotNull Quaterniond rotation) {
        return new SpatialForce(new Vector3d(0.15, 0, 0).rotate(rotation), new Vector3d(0, 0, 0));
    }
    private Set<SpatialForce> getDragForces(final Quaterniond rotation, final Vector3d velocity, final Quaterniond angularVelocity, final @NotNull Map<String, ControlSurfaceOrientation> orientations) {
        return description.getSurfaces(orientations).stream()
                .map(vehicleSurface -> vehicleSurface.getDragForce(rotation, velocity, angularVelocity))
                .collect(Collectors.toSet());
    }
    private @NotNull Set<SpatialForce> getLiftForces(final Quaterniond rotation, final Vector3d velocity, final Quaterniond angularVelocity, final @NotNull Map<String, ControlSurfaceOrientation> orientations) {
        return description.getSurfaces(orientations).stream()
                .map(vehicleSurface -> vehicleSurface.getLiftForce(rotation, velocity, angularVelocity))
                .collect(Collectors.toSet());
    }

    public void onKey(final @NotNull PersistentDataTraverser traverser, final char key) {
        final Map<String, ControlSurfaceOrientation> orientations = traverser.getControlSurfaceOrientations("orientations");
        description.adjustHingeComponents(orientations, key);
        traverser.setControlSurfaceOrientations("orientations", orientations);
    }
}
