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
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.utils.id.simple.DisplayGroupId;
import org.metamechanists.aircraft.utils.models.ModelBuilder;
import org.metamechanists.aircraft.utils.models.components.ModelCuboid;
import org.metamechanists.metalib.sefilib.entity.display.DisplayGroup;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class Glider extends SlimefunItem {
    private static final double DRAG_COEFFICIENT_BODY = 1.00;
    private static final double DRAG_COEFFICIENT_WING = 0.60;
    private static final double LIFT_COEFFICIENT_BODY = 0.30;
    private static final double LIFT_COEFFICIENT_WING = 1.20;

    public static final double MAX_CONTROL_SURFACE_ROTATION = Math.PI / 12;
    private static final double CONTROL_SURFACE_ROTATION_RATE = Math.PI / 24;

    private static final Vector3d STARTING_VELOCITY = new Vector3d(0.0, 0.00001, 0.0); // must start off with some velocity to prevent NaN issues
    private static final Vector3d STARTING_ANGULAR_VELOCITY = new Vector3d(0.0, 0.0, 0.0); // roll, yaw, pitch
    private static final Vector3d STARTING_ROTATION = new Vector3d(0, 0, 0); // roll, yaw, pitch
    private static final double MAX_VELOCITY = 50.0;

    private static final double MASS = 10.0;
    private static final double MOMENT_OF_INERTIA = MASS * 0.001; // silly approximation

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
            if (event.getClickedBlock().isPresent() && !event.getPlayer().isInsideVehicle()) {
                if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    event.getPlayer().getInventory().getItemInMainHand().subtract();
                }
                place(event.getClickedBlock().get().getRelative(event.getClickedFace()), event.getPlayer());
            }
        };
    }

    private static @NotNull Set<AircraftSurface> getSurfaces(final @NotNull ControlSurfaces controlSurfaces) {
        final Set<AircraftSurface> surfaces = new HashSet<>();
        surfaces.addAll(modelMain().getSurfaces("main", DRAG_COEFFICIENT_BODY, LIFT_COEFFICIENT_BODY));
        surfaces.addAll(modelWingFront1().getSurfaces("wingFront1", DRAG_COEFFICIENT_WING, LIFT_COEFFICIENT_WING));
        surfaces.addAll(modelWingFront2().getSurfaces("wingFront2", DRAG_COEFFICIENT_WING, LIFT_COEFFICIENT_WING));
        surfaces.addAll(modelWingBack1().getSurfaces("wingBack1", DRAG_COEFFICIENT_WING, LIFT_COEFFICIENT_WING));
        surfaces.addAll(modelWingBack2().getSurfaces("wingBack2", DRAG_COEFFICIENT_WING, LIFT_COEFFICIENT_WING));
        surfaces.addAll(modelTail().getSurfaces("tail", DRAG_COEFFICIENT_WING, LIFT_COEFFICIENT_WING));
        surfaces.addAll(modelAileron1(controlSurfaces.aileron1.getAngle()).getSurfaces("aileron1", DRAG_COEFFICIENT_WING, LIFT_COEFFICIENT_WING));
        surfaces.addAll(modelAileron2(controlSurfaces.aileron2.getAngle()).getSurfaces("aileron2", DRAG_COEFFICIENT_WING, LIFT_COEFFICIENT_WING));
        surfaces.addAll(modelElevator1(controlSurfaces.elevator1.getAngle()).getSurfaces("elevator1", DRAG_COEFFICIENT_WING, LIFT_COEFFICIENT_WING));
        surfaces.addAll(modelElevator2(controlSurfaces.elevator2.getAngle()).getSurfaces("elevator2", DRAG_COEFFICIENT_WING, LIFT_COEFFICIENT_WING));
        return surfaces;
    }

    private static ModelCuboid modelMain() {
        return new ModelCuboid()
                .material(Material.WHITE_CONCRETE)
                .size(2.0F, 0.4F, 0.4F)
                .location(-0.1F, 0, 0);
    }
    private static ModelCuboid modelWingFront1() {
        return new ModelCuboid()
                .material(Material.GRAY_CONCRETE)
                .size(0.6F, 0.01F, 1.4F)
                .location(0.7F, 0.0F, 0.6F);
    }
    private static ModelCuboid modelWingFront2() {
        return new ModelCuboid()
                .material(Material.GRAY_CONCRETE)
                .size(0.6F, 0.01F, 1.4F)
                .location(0.7F, 0.0F, -0.6F);
    }
    private static ModelCuboid modelWingBack1() {
        return new ModelCuboid()
                .material(Material.BLUE_CONCRETE)
                .size(0.4F, 0.01F, 0.8F)
                .location(-0.8F, 0.0F, 0.6F);
    }
    private static ModelCuboid modelWingBack2() {
        return new ModelCuboid()
                .material(Material.BLUE_CONCRETE)
                .size(0.4F, 0.01F, 0.8F)
                .location(-0.8F, 0.0F, -0.6F);
    }
    private static ModelCuboid modelTail() {
        return new ModelCuboid()
                .material(Material.BLUE_CONCRETE)
                .size(0.4F, 0.8F, 0.01F)
                .location(-1.1F, 0.4F, 0.0F);
    }
    private static ModelCuboid modelAileron1(final double rotation) {
        return new ModelCuboid()
                .material(Material.ORANGE_CONCRETE)
                .size(0.2F, 0.01F, 1.4F)
                .location(0.3F, (float)(-0.1 * Math.sin(rotation)), 0.6F)
                .rotation(new Vector3d(0, 0, rotation));
    }
    private static ModelCuboid modelAileron2(final double rotation) {
        return new ModelCuboid()
                .material(Material.ORANGE_CONCRETE)
                .size(0.2F, 0.01F, 1.4F)
                .location(0.3F, (float)(-0.1 * Math.sin(rotation)), -0.6F)
                .rotation(new Vector3d(0, 0, rotation));
    }
    private static ModelCuboid modelElevator1(final double rotation) {
        return new ModelCuboid()
                .material(Material.ORANGE_CONCRETE)
                .size(0.2F, 0.01F, 0.8F)
                .location(-1.1F, (float)(-0.1 * Math.sin(rotation)), 0.6F)
                .rotation(new Vector3d(0, 0, rotation));
    }
    private static ModelCuboid modelElevator2(final double rotation) {
        return new ModelCuboid()
                .material(Material.ORANGE_CONCRETE)
                .size(0.2F, 0.01F, 0.8F)
                .location(-1.1F, (float)(-0.1 * Math.sin(rotation)), -0.6F)
                .rotation(new Vector3d(0, 0, rotation));
    }

    private static void place(final @NotNull Block block, final @NotNull Player player) {
        final DisplayGroup componentGroup = buildAircraft(block.getLocation());

        final Pig pig = (Pig) block.getWorld().spawnEntity(block.getLocation(), EntityType.PIG);
        pig.setInvulnerable(true);
        pig.setGravity(false);
        pig.setInvisible(true);
        pig.setSilent(false);

        pig.addPassenger(componentGroup.getParentDisplay());
        componentGroup.getDisplays().values().forEach(pig::addPassenger);
        pig.addPassenger(player);

        final PersistentDataTraverser traverser = new PersistentDataTraverser(pig);
        traverser.set("velocity", STARTING_VELOCITY); // must start off with some velocity to prevent NaN issues
        traverser.set("angularVelocity", STARTING_ANGULAR_VELOCITY); // roll, yaw, pitch
        traverser.set("rotation", STARTING_ROTATION); // roll, yaw, pitch
        traverser.set("player", player.getUniqueId());
        traverser.set("componentGroupId", new DisplayGroupId(componentGroup.getParentUUID()));
        traverser.set("controlSurfaces", new ControlSurfaces());

        VehicleStorage.add(pig.getUniqueId());
    }
    private static @NotNull DisplayGroup buildAircraft(final Location location) {
        return new ModelBuilder()
                .rotation(STARTING_ROTATION.x, STARTING_ROTATION.y, STARTING_ROTATION.z)
                .add("main", modelMain())
                .add("wing_front_1", modelWingFront1())
                .add("wing_front_2", modelWingFront2())
                .add("wing_back_1", modelWingBack1())
                .add("wing_back_2", modelWingBack2())
                .add("tail", modelTail())
                .add("aileron_1", modelAileron1(0))
                .add("aileron_2", modelAileron2(0))
                .add("elevator_1", modelElevator1(0))
                .add("elevator_2", modelElevator2(0))
                .buildAtBlockCenter(location);
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

    public static void tickAircraft(final @NotNull Pig pig) {
        final PersistentDataTraverser traverser = new PersistentDataTraverser(pig);
        Vector3d velocity = traverser.getVector3d("velocity");
        final Vector3d angularVelocity = traverser.getVector3d("angularVelocity"); //new Vector3d(0.05, 0.05, 0.0);
        final Vector3d rotation = traverser.getVector3d("rotation");
        final DisplayGroupId componentGroupId = traverser.getDisplayGroupId("componentGroupId");
        final ControlSurfaces controlSurfaces = traverser.getControlSurfaces("controlSurfaces");
        if (velocity == null || angularVelocity == null || rotation == null || componentGroupId == null || componentGroupId.get().isEmpty()) {
            return;
        }

        final DisplayGroup componentGroup = componentGroupId.get().get();
        final Set<SpatialForce> forces = getForces(velocity, rotation, controlSurfaces);
        final Set<Vector3d> torqueVectors = forces.stream().map(force -> force.getTorqueVector(rotation)).collect(Collectors.toSet());

        // Newton's 2nd law to calculate resultant force and then acceleration
        final Vector3d resultantForce = new Vector3d();
        forces.stream().map(SpatialForce::getForce).forEach(resultantForce::add);
        Vector3d resultantAcceleration = new Vector3d(resultantForce).div(MASS);

        // Sum torque vectors to find resultant torque
        final Vector3d resultantTorque = new Vector3d();
        torqueVectors.forEach(resultantTorque::add);
        final Vector3d resultantAngularAcceleration = new Vector3d(resultantTorque).div(MOMENT_OF_INERTIA);

        // visualise forces
        final DisplayGroupId displayGroupId = traverser.getDisplayGroupId("forceGroupId");
        if (displayGroupId == null) {
            final ModelBuilder builder = new ModelBuilder();
            forces.forEach(force -> builder.add(force.getId(), force.visualise()));
            builder.add("velocity", new SpatialForce("main", ForceType.VELOCITY, velocity, new Vector3d()).visualise());
            builder.add("angularVelocity", new SpatialForce("main", ForceType.ANGULAR_VELOCITY, new Vector3d(angularVelocity), new Vector3d()).visualise());
            final DisplayGroup forceGroup = builder.buildAtBlockCenter(pig.getLocation());
            traverser.set("forceGroupId", new DisplayGroupId(forceGroup.getParentUUID()));
            pig.addPassenger(forceGroup.getParentDisplay());
            forceGroup.getDisplays().values().forEach(pig::addPassenger);
        } else {
            final Optional<DisplayGroup> forceGroup = displayGroupId.get();
            if (forceGroup.isPresent()) {
                for (final SpatialForce force : forces) {
                    final Display display = forceGroup.get().getDisplays().get(force.getId());
                    display.setTransformationMatrix(force.visualise().getMatrix(new Vector3d()));
                }
                forceGroup.get().getDisplays().get("velocity")
                        .setTransformationMatrix(new SpatialForce("main", ForceType.VELOCITY, velocity, new Vector3d()).visualise().getMatrix(new Vector3d()));
                forceGroup.get().getDisplays().get("angularVelocity")
                        .setTransformationMatrix(new SpatialForce("main", ForceType.ANGULAR_VELOCITY, new Vector3d(angularVelocity), new Vector3d()).visualise().getMatrix(new Vector3d()));
            }
            forceGroup.ifPresent(displayGroup -> forces.forEach(force -> displayGroup.getDisplays().get(force.getId()).setTransformationMatrix(force.visualise().getMatrix(new Vector3d()))));
        }

        if (velocity.length() > MAX_VELOCITY) {
            velocity = new Vector3d();
            resultantAcceleration = new Vector3d();
        }

        // Control surfaces
        controlSurfaces.aileron1.moveTowardsCenter(CONTROL_SURFACE_ROTATION_RATE);
        controlSurfaces.aileron2.moveTowardsCenter(CONTROL_SURFACE_ROTATION_RATE);
        controlSurfaces.elevator1.moveTowardsCenter(CONTROL_SURFACE_ROTATION_RATE);
        controlSurfaces.elevator2.moveTowardsCenter(CONTROL_SURFACE_ROTATION_RATE);

        // Euler integration
        traverser.set("is_aircraft", true);
        traverser.set("velocity", velocity.add(new Vector3d(resultantAcceleration).div(400)).mul(0.98));
        traverser.set("angularVelocity", angularVelocity.add(new Vector3d(resultantAngularAcceleration).div(400)).mul(0.95));
        traverser.set("rotation", rotation.add(angularVelocity));
        traverser.set("controlSurfaces", controlSurfaces);

        tickPig(pig, velocity);
        tickAircraftDisplays(componentGroup, rotation, controlSurfaces);

        if (pig.wouldCollideUsing(pig.getBoundingBox().expand(0.1, -0.1, 0.1))) {
            remove(pig, componentGroup);
        }
    }
    private static void tickPig(final @NotNull Pig pig, final Vector3d velocity) {
        pig.setVelocity(Vector.fromJOML(velocity));
    }
    private static void tickAircraftDisplays(final @NotNull DisplayGroup group, final Vector3d rotation, final @NotNull ControlSurfaces controlSurfaces) {
        group.getDisplays().get("main").setTransformationMatrix(modelMain().getMatrix(rotation));
        group.getDisplays().get("wing_front_1").setTransformationMatrix(modelWingFront1().getMatrix(rotation));
        group.getDisplays().get("wing_front_2").setTransformationMatrix(modelWingFront2().getMatrix(rotation));
        group.getDisplays().get("wing_back_1").setTransformationMatrix(modelWingBack1().getMatrix(rotation));
        group.getDisplays().get("wing_back_2").setTransformationMatrix(modelWingBack2().getMatrix(rotation));
        group.getDisplays().get("tail").setTransformationMatrix(modelTail().getMatrix(rotation));
        group.getDisplays().get("aileron_1").setTransformationMatrix(modelAileron1(controlSurfaces.aileron1.getAngle()).getMatrix(rotation));
        group.getDisplays().get("aileron_2").setTransformationMatrix(modelAileron2(controlSurfaces.aileron2.getAngle()).getMatrix(rotation));
        group.getDisplays().get("elevator_1").setTransformationMatrix(modelElevator1(controlSurfaces.elevator1.getAngle()).getMatrix(rotation));
        group.getDisplays().get("elevator_2").setTransformationMatrix(modelElevator2(controlSurfaces.elevator2.getAngle()).getMatrix(rotation));
    }

    private static @NotNull Set<SpatialForce> getForces(final Vector3d velocity, final Vector3d rotation, final @NotNull ControlSurfaces controlSurfaces) {
        final Set<SpatialForce> forces = new HashSet<>();
        forces.add(getWeightForce());
        forces.add(getThrustForce(rotation));
        forces.addAll(getDragForces(rotation, velocity, controlSurfaces));
        forces.addAll(getLiftForces(rotation, velocity, controlSurfaces));
        return forces;
    }
    private static @NotNull SpatialForce getWeightForce() {
        return new SpatialForce("main", ForceType.WEIGHT, new Vector3d(0, -0.5 * MASS, 0), new Vector3d(0, 0, 0));
    }
    private static @NotNull SpatialForce getThrustForce(final @NotNull Vector3d rotation) {
        return new SpatialForce("main", ForceType.THRUST, Utils.rotate(new Vector3d(50, 0, 0), rotation), new Vector3d(0, 0, 0));
    }
    private static Set<SpatialForce> getDragForces(final Vector3d rotation, final Vector3d velocity, final @NotNull ControlSurfaces controlSurfaces) {
        return getSurfaces(controlSurfaces).stream()
                .map(aircraftSurface -> aircraftSurface.getDragForce(rotation, velocity))
                .collect(Collectors.toSet());
    }
    private static @NotNull Set<SpatialForce> getLiftForces(final Vector3d rotation, final Vector3d velocity, final @NotNull ControlSurfaces controlSurfaces) {
        return getSurfaces(controlSurfaces).stream()
                .map(aircraftSurface -> aircraftSurface.getLiftForce(rotation, velocity))
                .collect(Collectors.toSet());
    }

    public static void onKeyW(final @NotNull PersistentDataTraverser traverser) {
        final ControlSurfaces controlSurfaces = traverser.getControlSurfaces("controlSurfaces");
        controlSurfaces.elevator1.adjust(CONTROL_SURFACE_ROTATION_RATE);
        controlSurfaces.elevator2.adjust(CONTROL_SURFACE_ROTATION_RATE);
        traverser.set("controlSurfaces", controlSurfaces);
    }
    public static void onKeyS(final @NotNull PersistentDataTraverser traverser) {
        final ControlSurfaces controlSurfaces = traverser.getControlSurfaces("controlSurfaces");
        controlSurfaces.elevator1.adjust(-CONTROL_SURFACE_ROTATION_RATE);
        controlSurfaces.elevator2.adjust(-CONTROL_SURFACE_ROTATION_RATE);
        traverser.set("controlSurfaces", controlSurfaces);
    }
    public static void onKeyA(final @NotNull PersistentDataTraverser traverser) {
        final ControlSurfaces controlSurfaces = traverser.getControlSurfaces("controlSurfaces");
        controlSurfaces.aileron1.adjust(CONTROL_SURFACE_ROTATION_RATE);
        controlSurfaces.aileron2.adjust(-CONTROL_SURFACE_ROTATION_RATE);
        traverser.set("controlSurfaces", controlSurfaces);
    }
    public static void onKeyD(final @NotNull PersistentDataTraverser traverser) {
        final ControlSurfaces controlSurfaces = traverser.getControlSurfaces("controlSurfaces");
        controlSurfaces.aileron1.adjust(-CONTROL_SURFACE_ROTATION_RATE);
        controlSurfaces.aileron2.adjust(CONTROL_SURFACE_ROTATION_RATE);
        traverser.set("controlSurfaces", controlSurfaces);
    }
}
