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

import static java.lang.Math.PI;


public class Vehicle extends SlimefunItem {
    private final String name;
    private final VehicleDescription description;

    private static final Vector3d STARTING_VELOCITY = new Vector3d(0.0, 0.0, 0.0);
    private static final Quaterniond STARTING_ANGULAR_VELOCITY = new Quaterniond();
    private static final Quaterniond STARTING_ROTATION = new Quaterniond().rotateY(PI/2);
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
        final DisplayGroup componentGroup = buildAircraft(block.getLocation());

        final Pig pig = (Pig) block.getWorld().spawnEntity(block.getLocation(), EntityType.PIG);
        pig.setInvulnerable(true);
        pig.setGravity(false);
        pig.setInvisible(true);
        pig.setSilent(true);

        pig.addPassenger(componentGroup.getParentDisplay());
        componentGroup.getDisplays().values().forEach(pig::addPassenger);
        pig.addPassenger(player);

        final PersistentDataTraverser traverser = new PersistentDataTraverser(pig);
        traverser.set("name", name);
        traverser.set("velocity", STARTING_VELOCITY);
        traverser.set("angularVelocity", STARTING_ANGULAR_VELOCITY);
        traverser.set("rotation", STARTING_ROTATION);
        traverser.set("player", player.getUniqueId());
        traverser.set("componentGroupId", new DisplayGroupId(componentGroup.getParentUUID()));
        traverser.setControlSurfaceOrientations("controlSurfaces", description.initializeOrientations());

        VehicleStorage.add(pig.getUniqueId());
    }
    private @NotNull DisplayGroup buildAircraft(final Location location) {
        final ModelBuilder builder = new ModelBuilder().rotation(STARTING_ROTATION.x, STARTING_ROTATION.y, STARTING_ROTATION.z);
        description.getCuboids(description.initializeOrientations()).forEach(builder::add);
        return builder.buildAtBlockCenter(location);
    }

    private static @NotNull Optional<Player> getPilot(final @NotNull Pig pig) {
        return pig.getPassengers().stream()
                .filter(entity -> entity instanceof Player)
                .map(Player.class::cast)
                .findFirst();
    }

    private static void remove(final @NotNull Pig pig, final @NotNull DisplayGroup componentGroup) {
        componentGroup.remove();
        VehicleStorage.remove(pig.getUniqueId());
        pig.getLocation().createExplosion(4);
        getPilot(pig).ifPresent(pilot -> {
            pilot.eject();
            componentGroup.getParentDisplay().removePassenger(pilot);
        });
        pig.remove();
    }

    public void tickAircraft(final @NotNull Pig pig) {
        final PersistentDataTraverser traverser = new PersistentDataTraverser(pig);
        Vector3d velocity = traverser.getVector3d("velocity");
        final Quaterniond rotation = traverser.getQuaterniond("rotation");
        final Quaterniond angularVelocity = traverser.getQuaterniond("angularVelocity");
        final DisplayGroupId componentGroupId = traverser.getDisplayGroupId("componentGroupId");
        final Map<String, ControlSurfaceOrientation> controlSurfaces = traverser.getControlSurfaceOrientations("controlSurfaces");
        if (velocity == null || angularVelocity == null || rotation == null || componentGroupId == null || componentGroupId.get().isEmpty() || controlSurfaces == null) {
            return;
        }

        final DisplayGroup componentGroup = componentGroupId.get().get();
        final Set<SpatialForce> forces = getForces(velocity, rotation, angularVelocity, controlSurfaces);
        final Set<Vector3d> torqueVectors = forces.stream().map(SpatialForce::getTorqueVector).collect(Collectors.toSet());

        // Newton's 2nd law to calculate resultant force and then acceleration
        final Vector3d resultantForce = new Vector3d();
        forces.stream().map(SpatialForce::getForce).forEach(resultantForce::add);
        Vector3d resultantAcceleration = new Vector3d(resultantForce).div(description.getMass());

        // Sum torque vectors to find resultant torque
        final Vector3d resultantTorque = new Vector3d();
        torqueVectors.forEach(resultantTorque::add);
        final Quaterniond negativeRotation = new Quaterniond().rotateAxis(-rotation.angle(), rotation.x, rotation.y, rotation.z);
        resultantTorque.rotate(negativeRotation);
        final Vector3d resultantAngularAccelerationVector = new Vector3d(resultantTorque).div(description.getMomentOfInertia()).div(400);
        final Quaterniond resultantAngularAcceleration = new Quaterniond().fromAxisAngleRad(new Vector3d(resultantAngularAccelerationVector).normalize(), resultantAngularAccelerationVector.length()) ;

        if (velocity.length() > MAX_VELOCITY) {
            velocity = new Vector3d();
            resultantAcceleration = new Vector3d();
        }

        description.moveHingeComponentsToCenter(controlSurfaces);

        resultantAcceleration.div(400);
        description.applyVelocityDampening(velocity);
        velocity.add(new Vector3d(resultantAcceleration).div(400)).mul(0.95);

        if (resultantAngularAcceleration.angle() != 0) {
            angularVelocity.mul(resultantAngularAcceleration);
        }

        description.applyAngularVelocityDampening(angularVelocity);

        rotation.mul(angularVelocity);

        // Euler integration
        traverser.set("velocity", velocity);
        traverser.set("angularVelocity", angularVelocity);
        traverser.set("rotation", rotation);
        traverser.setControlSurfaceOrientations("controlSurfaces", controlSurfaces);

        pig.setVelocity(Vector.fromJOML(velocity));
        description.getCuboids(controlSurfaces).forEach((cuboidName, cuboid) -> componentGroup.getDisplays().get(cuboidName).setTransformationMatrix(cuboid.getMatrix(rotation)));

        if (pig.wouldCollideUsing(pig.getBoundingBox().expand(0.1, -0.1, 0.1))) {
            remove(pig, componentGroup);
        }
    }

    private @NotNull Set<SpatialForce> getForces(final Vector3d velocity, final Quaterniond rotation, final Quaterniond angularVelocity, final @NotNull Map<String, ControlSurfaceOrientation> controlSurfaces) {
        final Set<SpatialForce> forces = new HashSet<>();
        forces.add(getWeightForce());
        forces.add(getThrustForce(rotation));
        forces.addAll(getDragForces(rotation, velocity, angularVelocity, controlSurfaces));
        forces.addAll(getLiftForces(rotation, velocity, angularVelocity, controlSurfaces));
        return forces;
    }
    private @NotNull SpatialForce getWeightForce() {
        return new SpatialForce(new Vector3d(0, -0.5 * description.getMass(), 0), new Vector3d(0, 0, 0));
    }
    private static @NotNull SpatialForce getThrustForce(final @NotNull Quaterniond rotation) {
        return new SpatialForce(new Vector3d(0.15, 0, 0).rotate(rotation), new Vector3d(0, 0, 0));
    }
    private Set<SpatialForce> getDragForces(final Quaterniond rotation, final Vector3d velocity, final Quaterniond angularVelocity, final @NotNull Map<String, ControlSurfaceOrientation> controlSurfaces) {
        return description.getSurfaces(controlSurfaces).stream()
                .map(vehicleSurface -> vehicleSurface.getDragForce(rotation, velocity, angularVelocity))
                .collect(Collectors.toSet());
    }
    private @NotNull Set<SpatialForce> getLiftForces(final Quaterniond rotation, final Vector3d velocity, final Quaterniond angularVelocity, final @NotNull Map<String, ControlSurfaceOrientation> controlSurfaces) {
        return description.getSurfaces(controlSurfaces).stream()
                .map(vehicleSurface -> vehicleSurface.getLiftForce(rotation, velocity, angularVelocity))
                .collect(Collectors.toSet());
    }

    public void onKey(final @NotNull PersistentDataTraverser traverser, final char key) {
        final Map<String, ControlSurfaceOrientation> orientations = traverser.getControlSurfaceOrientations("controlSurfaces");
        description.adjustHingeComponents(orientations, key);
        traverser.setControlSurfaceOrientations("controlSurfaces", orientations);
    }
}
