package org.metamechanists.aircraft.vehicles;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;
import org.metamechanists.aircraft.utils.id.simple.DisplayGroupId;
import org.metamechanists.aircraft.utils.models.ModelBuilder;
import org.metamechanists.aircraft.utils.models.components.ModelCuboid;
import org.metamechanists.metalib.sefilib.entity.display.DisplayGroup;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class Glider extends SlimefunItem {
    private static final double AERODYNAMIC_COEFFICIENT = 1.2;
    private static final double DRAG_COEFFICIENT_WING = 0.8;
    private static final double LIFT_COEFFICIENT_BODY = 0.2;
    private static final double LIFT_COEFFICIENT_WING = 1.0;

    private static final Vector3d STARTING_VELOCITY = new Vector3d(0.0, 0.00001, 0.0); // must start off with some velocity to prevent NaN issues
    private static final Vector3d STARTING_ANGULAR_VELOCITY = new Vector3d(0.0, 0.0, 0.0); // roll, yaw, pitch
    private static final Vector3d STARTING_ROTATION = new Vector3d(0.0, 0.0, 0.0); // roll, yaw, pitch

    private static final double MASS = 0.01;
    private static final double MOMENT_OF_INERTIA = MASS; // silly approximation

    public static final SlimefunItemStack GLIDER = new SlimefunItemStack(
            "ACR_GLIDER",
            Material.FEATHER,
            "&4&ljustin don't hurt me",
            "&cpls");

    public Glider(final ItemGroup itemGroup, final SlimefunItemStack item, final RecipeType recipeType, final ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        addItemHandler(onItemUse());
    }

    private static @NotNull ItemUseHandler onItemUse() {
        return event -> {
            if (event.getClickedBlock().isPresent()) {
                event.cancel();
                place(event.getClickedBlock().get().getRelative(event.getClickedFace()));
            }
        };
    }

    private static @NotNull Set<AircraftSurface> getSurfaces() {
        final Set<AircraftSurface> surfaces = new HashSet<>();
        surfaces.addAll(modelMain().getSurfaces(AERODYNAMIC_COEFFICIENT, LIFT_COEFFICIENT_BODY));
        surfaces.addAll(modelWingFront1().getSurfaces(DRAG_COEFFICIENT_WING, LIFT_COEFFICIENT_WING));
        surfaces.addAll(modelWingFront2().getSurfaces(DRAG_COEFFICIENT_WING, LIFT_COEFFICIENT_WING));
        surfaces.addAll(modelWingBack1().getSurfaces(DRAG_COEFFICIENT_WING, LIFT_COEFFICIENT_WING));
        surfaces.addAll(modelWingBack2().getSurfaces(DRAG_COEFFICIENT_WING, LIFT_COEFFICIENT_WING));
        surfaces.addAll(modelRudder().getSurfaces(DRAG_COEFFICIENT_WING, LIFT_COEFFICIENT_WING));
        return surfaces;
    }

    private static ModelCuboid modelMain() {
        return new ModelCuboid()
                .material(Material.WHITE_CONCRETE)
                .size(2.0F, 0.4F, 0.4F)
                .location(-0.5F, 0, 0);
    }
    private static ModelCuboid modelWingFront1() {
        return new ModelCuboid()
                .material(Material.GRAY_CONCRETE)
                .size(0.6F, 0.1F, 1.2F)
                .location(-0.3F, 0.0F, 0.6F);
    }
    private static ModelCuboid modelWingFront2() {
        return new ModelCuboid()
                .material(Material.GRAY_CONCRETE)
                .size(0.6F, 0.1F, 1.2F)
                .location(-0.3F, 0.0F, -0.6F);
    }
    private static ModelCuboid modelWingBack1() {
        return new ModelCuboid()
                .material(Material.BLUE_CONCRETE)
                .size(0.4F, 0.1F, 0.8F)
                .location(-1.3F, 0.0F, 0.6F);
    }
    private static ModelCuboid modelWingBack2() {
        return new ModelCuboid()
                .material(Material.BLUE_CONCRETE)
                .size(0.4F, 0.1F, 0.8F)
                .location(-1.3F, 0.0F, -0.6F);
    }
    private static ModelCuboid modelRudder() {
        return new ModelCuboid()
                .material(Material.BLUE_CONCRETE)
                .size(0.4F, 0.8F, 0.1F)
                .location(-1.3F, 0.6F, 0.0F);
    }

    private static void place(final @NotNull Block block) {
        final DisplayGroup componentGroup = buildAircraft(block.getLocation());
        final DisplayGroup forceArrowGroup = buildForceArrows(componentGroup, block.getLocation());
        VehicleStorage.add(new DisplayGroupId(componentGroup.getParentUUID()), new DisplayGroupId(forceArrowGroup.getParentUUID()));
    }
    private static @NotNull DisplayGroup buildAircraft(final Location location) {
        final DisplayGroup displayGroup = new ModelBuilder()
                .rotation(0.0, 0.0, 0.0)
                .add("main", modelMain())
                .add("wing_front_1", modelWingFront1())
                .add("wing_front_2", modelWingFront2())
                .add("wing_back_1", modelWingBack1())
                .add("wing_back_2", modelWingBack2())
                .add("rudder", modelRudder())
                .buildAtBlockCenter(location);

        final PersistentDataTraverser traverser = new PersistentDataTraverser(displayGroup.getParentUUID());
        traverser.set("velocity", STARTING_VELOCITY); // must start off with some velocity to prevent NaN issues
        traverser.set("angularVelocity", STARTING_ANGULAR_VELOCITY); // roll, yaw, pitch
        traverser.set("rotation", STARTING_ROTATION); // roll, yaw, pitch
        return displayGroup;
    }
    private static DisplayGroup buildForceArrows(final @NotNull DisplayGroup group, final Location location) {
        final ModelBuilder forceArrowBuilder = new ModelBuilder();
        getForces(STARTING_VELOCITY, STARTING_ROTATION).forEach(force -> forceArrowBuilder.add(force.stringHash(), force.getForceLine(STARTING_ROTATION)));
        forceArrowBuilder.add("velocity", new SpatialForce(STARTING_VELOCITY, new Vector3d(), ForceType.VELOCITY).getForceLine(STARTING_ROTATION));
        return forceArrowBuilder.buildAtBlockCenter(location);
    }

    public static void tickAircraft(final @NotNull AircraftGroup aircraftGroup) {
        final DisplayGroup componentGroup = aircraftGroup.componentGroupId().get().get();
        final DisplayGroup forceArrowGroup = aircraftGroup.forceArrowGroupId().get().get();
        final PersistentDataTraverser traverser = new PersistentDataTraverser(componentGroup.getParentUUID());
        final Vector3d velocity = traverser.getVector3d("velocity");
        final Vector3d angularVelocity = traverser.getVector3d("angularVelocity");
        final Vector3d rotation = traverser.getVector3d("rotation");
        if (velocity == null || angularVelocity == null || rotation == null) {
            return;
        }

        final Set<SpatialForce> forces = getForces(velocity, rotation);
        final Set<Vector3d> torqueVectors = forces.stream().map(SpatialForce::getTorqueVector).collect(Collectors.toSet());

        // Newton's 2nd law to calculate resultant force and then acceleration
        final Vector3d resultantForce = new Vector3d();
        forces.stream().map(SpatialForce::force).forEach(resultantForce::add);
        final Vector3d resultantAcceleration = new Vector3d(resultantForce).div(MASS).div(20);

        // Sum torque vectors to find resultant torque
        final Vector3d resultantTorque = new Vector3d();
        torqueVectors.forEach(resultantTorque::add);
        final Vector3d resultantAngularAcceleration = new Vector3d(resultantTorque).div(MOMENT_OF_INERTIA).div(20);

        // Euler integration
        traverser.set("velocity", velocity.add(resultantAcceleration));
        traverser.set("angular_velocity", angularVelocity.add(resultantAngularAcceleration));
        traverser.set("rotation", rotation.add(angularVelocity));

        // justin's thing??
        componentGroup.getDisplays().values().forEach(display -> display.getPassengers()
                .forEach(passenger -> {
                    passenger.teleportAsync(passenger.getLocation().add(Vector.fromJOML(velocity)));
                    display.addPassenger(passenger);
                }));

        tickAircraftDisplays(componentGroup, velocity, rotation);
        tickForceArrows(forceArrowGroup, velocity, rotation);
    }
    private static void tickAircraftDisplays(final @NotNull DisplayGroup group, final Vector3d velocity, final Vector3d rotation) {
        group.getParentDisplay().teleportAsync(group.getParentDisplay().getLocation().add(Vector.fromJOML(velocity)));
        group.getDisplays().values().forEach(display -> display.teleportAsync(display.getLocation().add(Vector.fromJOML(velocity))));
        group.getDisplays().get("main").setTransformationMatrix(modelMain().getMatrix(rotation));
        group.getDisplays().get("wing_front_1").setTransformationMatrix(modelWingFront1().getMatrix(rotation));
        group.getDisplays().get("wing_front_2").setTransformationMatrix(modelWingFront2().getMatrix(rotation));
        group.getDisplays().get("wing_back_1").setTransformationMatrix(modelWingBack1().getMatrix(rotation));
        group.getDisplays().get("wing_back_2").setTransformationMatrix(modelWingBack2().getMatrix(rotation));
        group.getDisplays().get("rudder").setTransformationMatrix(modelRudder().getMatrix(rotation));
    }
    private static void tickForceArrows(final @NotNull DisplayGroup group, final Vector3d velocity, final Vector3d rotation) {
        group.getParentDisplay().teleportAsync(group.getParentDisplay().getLocation().add(Vector.fromJOML(velocity)));
        group.getDisplays().values().forEach(display -> display.teleportAsync(display.getLocation().add(Vector.fromJOML(velocity))));
        for (final SpatialForce force : getForces(velocity, rotation)) {
            group.getDisplays().get(force.stringHash()).setTransformationMatrix(force.getForceLine(rotation).getMatrix(new Vector3d()));
        }
        group.getDisplays().get("velocity").setTransformationMatrix(
                new SpatialForce(new Vector3d(velocity).div(100), new Vector3d(), ForceType.VELOCITY).getForceLine(rotation).getMatrix(new Vector3d()));
    }

    private static @NotNull Set<SpatialForce> getForces(final Vector3d velocity, final Vector3d rotation) {
        final Set<SpatialForce> forces = new HashSet<>();
        forces.add(getWeightForce());
        forces.addAll(getDragForces(rotation, velocity));
        forces.addAll(getLiftForces(rotation, velocity));
        return forces;
    }
    private static @NotNull SpatialForce getWeightForce() {
        return new SpatialForce(new Vector3d(0, -9.81 * MASS, 0), new Vector3d(0, 0, 0), ForceType.WEIGHT);
    }
    private static Set<SpatialForce> getDragForces(final Vector3d rotation, final Vector3d velocity) {
        return getSurfaces().stream()
                .map(aircraftSurface -> aircraftSurface.getDragForce(rotation, velocity))
                .collect(Collectors.toSet());
    }
    private static Set<SpatialForce> getLiftForces(final Vector3d rotation, final Vector3d velocity) {
        return getSurfaces().stream()
                .map(aircraftSurface -> aircraftSurface.getLiftForce(rotation, velocity))
                .collect(Collectors.toSet());
    }
}
