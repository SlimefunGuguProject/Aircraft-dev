package org.metamechanists.aircraft.vehicle;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.vehicle.component.base.ItemComponent;
import org.metamechanists.aircraft.vehicle.component.base.VehicleComponent;
import org.metamechanists.aircraft.vehicle.component.hud.bottompanel.BottomPanel;
import org.metamechanists.aircraft.vehicle.component.hud.compass.Compass;
import org.metamechanists.aircraft.vehicle.component.hud.horizon.Horizon;
import org.metamechanists.aircraft.vehicle.component.vehicle.HingedComponent;
import org.metamechanists.aircraft.vehicle.forces.SpatialForce;
import org.metamechanists.aircraft.vehicle.forces.SpatialForceType;
import org.metamechanists.kinematiccore.api.entity.KinematicEntity;
import org.metamechanists.kinematiccore.api.storage.EntityStorage;
import org.metamechanists.kinematiccore.api.storage.StateReader;
import org.metamechanists.kinematiccore.api.storage.StateWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@SuppressWarnings("WeakerAccess")
@Getter
public class VehicleEntity extends KinematicEntity<Pig, VehicleEntitySchema> {
    public static final int TICK_INTERVAL = 2;
    private static final int PHYSICS_INTERVAL = 1;
    private static final int PHYSICS_UPDATES_PER_SECOND = 20 / PHYSICS_INTERVAL;
    private static final int PHYSICS_UPDATES_PER_AIRCRAFT_UPDATE = TICK_INTERVAL / PHYSICS_INTERVAL;
    private static final int MIN_THROTTLE = 0;
    private static final int MAX_THROTTLE = 100;

    private int throttle;
    private final Vector3d velocity;
    private final Quaterniond rotation;
    private final Vector3d angularVelocity;
    private final Map<String, UUID> components;
    private final Map<String, UUID> hud;
    private final Set<UUID> itemDisplays;
    private final Set<UUID> textDisplays;
    private @Nullable UUID interaction;
    private @Nullable UUID horizon;
    private @Nullable UUID compass;
    private @Nullable UUID bottomPanel;

    public VehicleEntity(@NotNull VehicleEntitySchema schema, @NotNull Block block, @NotNull Player player) {
        super(schema, () -> {
            Location location = block.getLocation().clone().toCenterLocation().add(new Vector(0, 0.5, 0));
            Pig pig = (Pig) block.getWorld().spawnEntity(location, EntityType.PIG);
            pig.setInvulnerable(true);
            pig.setGravity(false);
            pig.setInvisible(true);
            pig.setSilent(true);
            return pig;
        });

        throttle = 0;
        velocity = new Vector3d();
        rotation = new Quaterniond().rotationY(Math.toRadians(-90.0-player.getEyeLocation().getYaw()));
        angularVelocity = new Vector3d();
        components = new HashMap<>();
        hud = new HashMap<>();
        itemDisplays = new HashSet<>();
        textDisplays = new HashSet<>();
        interaction = new VehicleInteractor(this).uuid();
        if (schema.getHorizonSchema() != null) {
            horizon = new Horizon(schema.getHorizonSchema(), this).uuid();
        }
        if (schema.getCompassSchema() != null) {
            compass = new Compass(schema.getCompassSchema(), this).uuid();
        }
        if (schema.getBottomPanelSchema() != null) {
            bottomPanel = new BottomPanel(schema.getBottomPanelSchema(), this).uuid();
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public VehicleEntity(StateReader reader) {
        super(reader);
        throttle = reader.get("throttle", Integer.class);
        velocity = reader.get("velocity", Vector3d.class);
        rotation = reader.get("rotation", Quaterniond.class);
        angularVelocity = reader.get("angularVelocity", Vector3d.class);
        interaction = reader.get("interaction", UUID.class);
        components = reader.get("components", new HashMap<>());
        hud = reader.get("hud", new HashMap<>());
        itemDisplays = reader.get("itemDisplays", new HashSet<>());
        textDisplays = reader.get("textDisplays", new HashSet<>());
        horizon = reader.get("horizon", UUID.class);
        compass = reader.get("compass", UUID.class);
        bottomPanel = reader.get("bottomPanel ", UUID.class);
    }

    @Override
    public void write(@NotNull StateWriter writer) {
        writer.set("throttle", throttle);
        writer.set("velocity", velocity);
        writer.set("rotation", rotation);
        writer.set("angularVelocity", angularVelocity);
        writer.set("interaction", interaction);
        writer.set("components", components);
        writer.set("hud", hud);
        writer.set("itemDisplays", new HashSet<>());
        writer.set("textDisplays", new HashSet<>());
        writer.set("horizon", horizon);
        writer.set("compass", compass);
        writer.set("bottomPanel", bottomPanel);
    }

    @Override
    public void tick(@NotNull Pig pig, long tick) {
        if (tick % TICK_INTERVAL != 0) {
            return;
        }

        Map<String, VehicleComponent.VehicleComponentSchema> expectedComponents = schema().getComponents();

        // TODO do this for hud components too, in another method
        // Remove any additional vehicle components
        for (String id : components.keySet()) {
            if (!expectedComponents.containsKey(id)) {
                KinematicEntity<?, ?> kinematicEntity = EntityStorage.kinematicEntity(components.remove(id));
                if (kinematicEntity != null) {
                    kinematicEntity.remove();
                }
            }
        }

        // Add any new vehicle components
        //noinspection KeySetIterationMayUseEntrySet
        for (String id : expectedComponents.keySet()) {
            if (!components.containsKey(id)) {
                components.put(id, expectedComponents.get(id).build(this).uuid());
            }
        }

        // Do physics update (before component updates for immediate effect)
        for (int i = 0; i < PHYSICS_UPDATES_PER_AIRCRAFT_UPDATE; i++) {
            updatePhysics(pig);
        }

        // Update components
        for (UUID uuid : components.values()) {
            if (EntityStorage.kinematicEntity(uuid) instanceof ItemComponent<?> component) {
                component.update(this);
            }
        }

        // Update HUD
        if (horizon != null) {
            if (EntityStorage.kinematicEntity(horizon) instanceof Horizon horizon) {
                horizon.update(this);
            }
        }
        if (compass != null) {
            if (EntityStorage.kinematicEntity(compass) instanceof Compass compass) {
                compass.update(this);
            }
        }
        if (bottomPanel != null) {
            if (EntityStorage.kinematicEntity(bottomPanel ) instanceof BottomPanel bottomPanel) {
                bottomPanel.update(this);
            }
        }

        // Update pig velocity
        Vector pigVelocity = Vector.fromJOML(absoluteVelocity().div(PHYSICS_UPDATES_PER_SECOND));
        pig.setVelocity(pigVelocity);
    }

    private static void removeRedundantComponents(@NotNull Map<String, UUID> components, @NotNull Set<String> expected) {
        for (String id : components.keySet()) {
            if (!expected.contains(id)) {
                KinematicEntity<?, ?> kinematicEntity = EntityStorage.kinematicEntity(components.remove(id));
                if (kinematicEntity != null) {
                    kinematicEntity.remove();
                }
            }
        }
    }

    public void onKey(char key) {
        for (UUID uuid : components.values()) {
            if (EntityStorage.kinematicEntity(uuid) instanceof HingedComponent component) {
                component.onKey(key);
            }
        }
    }

    @SuppressWarnings("Convert2streamapi")
    public Set<VehicleSurface> getSurfaces() {
        Set<VehicleSurface> surfaces = new HashSet<>();
        for (UUID uuid : components.values()) {
            if (EntityStorage.kinematicEntity(uuid) instanceof VehicleComponent<?> component) {
                surfaces.addAll(component.getSurfaces());
            }
        }
        return surfaces;
    }

    public static boolean isOnGround(@NotNull Pig pig) {
        return pig.wouldCollideUsing(pig.getBoundingBox().shift(new Vector(0.0, -0.1, 0.0)));
    }

    private void updatePhysics(Pig pig) {
        boolean isOnGround = isOnGround(pig);

        Set<SpatialForce> forces = getForces(isOnGround);

        velocity.mul(1.0 - schema().getVelocityDamping());
        Vector3d acceleration = acceleration(forces);
        velocity.add(new Vector3d(acceleration).div(PHYSICS_UPDATES_PER_SECOND));
        cancelVelocityAndAcceleration(pig, velocity, acceleration);

        angularVelocity.mul(1.0 - schema().getAngularVelocityDamping());
        Vector3d angularAcceleration = angularAcceleration(forces);
        if (isOnGround) {
            angularAcceleration.x -= schema().getGroundRollDamping() * roll();
            angularAcceleration.z -= schema().getGroundPitchDamping() * pitch();
        }
        angularVelocity.add(new Vector3d(angularAcceleration).div(PHYSICS_UPDATES_PER_SECOND));
        if (angularVelocity.length() > 0.00001) { // Check to avoid dividing by 0 in normalize
            rotation.rotateAxis(angularVelocity.length() / PHYSICS_UPDATES_PER_SECOND, new Vector3d(angularVelocity).normalize());
        }
    }

    public Vector3d acceleration(@NotNull Set<SpatialForce> forces) {
        Vector3d resultantForce = new Vector3d();
        forces.stream().map(SpatialForce::force).forEach(resultantForce::add);
        return new Vector3d(resultantForce).div(schema().getMass());
    }

    public Vector3d angularAcceleration(@NotNull Set<SpatialForce> forces) {
        Set<Vector3d> torqueVectors = forces.stream().map(SpatialForce::getTorqueVector).collect(Collectors.toSet());
        Vector3d resultantTorque = new Vector3d();
        torqueVectors.forEach(resultantTorque::add);

        return new Vector3d(resultantTorque).div(schema().getMomentOfInertia());
    }

    public void cancelVelocityAndAcceleration(@NotNull Pig pig, @NotNull Vector3d velocity, Vector3d acceleration) {
        velocity.rotate(rotation);

        if (pig.wouldCollideUsing(pig.getBoundingBox().shift(new Vector(-0.1, 0.0, 0.0)))) {
            velocity.x = Math.max(velocity.x, 0.0);
        }

        if (pig.wouldCollideUsing(pig.getBoundingBox().shift(new Vector(0.1, 0.0, 0.0)))) {
            velocity.x = Math.min(velocity.x, 0.0);
        }

        if (pig.wouldCollideUsing(pig.getBoundingBox().shift(new Vector(0.0, -0.1, 0.0)))) {
            velocity.y = Math.max(velocity.y, 0.0);
        }

        if (pig.wouldCollideUsing(pig.getBoundingBox().shift(new Vector(0.0, 0.1, 0.0)))) {
            velocity.y = Math.min(velocity.y, 0.0);
        }

        if (pig.wouldCollideUsing(pig.getBoundingBox().shift(new Vector(0.0, 0.0, -0.1)))) {
            velocity.z = Math.max(velocity.z, 0.0);
        }

        if (pig.wouldCollideUsing(pig.getBoundingBox().shift(new Vector(0.0, 0.0, 0.1)))) {
            velocity.z = Math.min(velocity.z, 0.0);
        }

        velocity.rotate(new Quaterniond(rotation).invert());
    }

    public @NotNull Set<SpatialForce> getForces(boolean isOnGround) {
        Set<SpatialForce> forces = new HashSet<>();
        forces.add(getWeightForce());
        forces.add(getThrustForce());
        forces.addAll(getDragForces());
        forces.addAll(getLiftForces());
        forces.add(getFrictionForce(isOnGround, acceleration(forces).mul(schema().getMass())));
        return forces;
    }

    private @NotNull SpatialForce getWeightForce() {
        Vector3d force = Utils.rotateBackwards(new Vector3d(0, schema().getGravityAcceleration() * schema().getMass(), 0), rotation);
        Vector3d location = schema().getWeightLocation();
        return new SpatialForce(SpatialForceType.WEIGHT, force, location);
    }

    private @NotNull SpatialForce getThrustForce() {
        double throttleFraction = throttle / 100.0;
        Vector3d force = new Vector3d(throttleFraction * schema().getThrustForce(), 0, 0);
        Vector3d location = schema().getThrustLocation();
        return new SpatialForce(SpatialForceType.THRUST, force, location);
    }

    @SuppressWarnings("Convert2streamapi")
    private @NotNull Set<SpatialForce> getDragForces() {
        Set<SpatialForce> set = new HashSet<>();
        for (VehicleSurface vehicleSurface : getSurfaces()) {
            set.add(vehicleSurface.getDragForce(schema().getAirDensity(), velocity, angularVelocity));
        }
        return set;
    }

    @SuppressWarnings("Convert2streamapi")
    private @NotNull Set<SpatialForce> getLiftForces() {
        Set<SpatialForce> set = new HashSet<>();
        for (VehicleSurface vehicleSurface : getSurfaces()) {
            set.add(vehicleSurface.getLiftForce(schema().getAirDensity(), velocity, angularVelocity));
        }
        return set;
    }

    private @NotNull SpatialForce getFrictionForce(boolean isOnGround, Vector3d force) {
        if (!isOnGround || velocity.length() < 0.0001) {
            return new SpatialForce(SpatialForceType.FRICTION, new Vector3d(), new Vector3d());
        }

        double horizontalForce = new Vector3d(velocity.x, 0.0, velocity.z)
                .mul(schema().getMass())
                .length();
        double horizontalVelocity = new Vector3d(velocity.x, 0.0, velocity.z)
                .length();

        double frictionAmount = Math.abs(force.y) * schema().getFrictionCoefficient();
        if (horizontalVelocity < 0.01) {
            // Stationary; limiting equilibrium
            frictionAmount = Math.min(frictionAmount, horizontalForce);
        }

        Vector3d frictionForce = new Vector3d(velocity)
                .normalize()
                .mul(-frictionAmount);
        return new SpatialForce(SpatialForceType.FRICTION, frictionForce, new Vector3d());
    }

    public Vector3d absoluteVelocity() {
        return new Vector3d(velocity).rotate(rotation);
    }

    public void steer(double direction) {
        angularVelocity.y += direction * schema().getSteeringSpeed();
    }

    public void adjustThrottle(int increment) {
        int newThrottle = throttle + increment;
        if (newThrottle >= MIN_THROTTLE && newThrottle <= MAX_THROTTLE) {
            throttle = newThrottle;
        }
    }

    public double getVelocityPitch() {
        Vector3d absoluteVelocity = absoluteVelocity();
        double pitch = absoluteVelocity.angle(new Vector3d(absoluteVelocity.x, 0, absoluteVelocity.z));
        return absoluteVelocity.y > 0 ? pitch : -pitch;
    }

    // https://stackoverflow.com/questions/5782658/extracting-yaw-from-a-quaternion
    public double roll() {
        Vector3d lookingAtSide = new Vector3d(0, 0, 1).rotate(rotation);
        double roll = lookingAtSide.angle(new Vector3d(lookingAtSide.x, 0, lookingAtSide.z));
        return lookingAtSide.y > 0 ? -roll : roll;
    }

    public double pitch() {
        Vector3d lookingAtForward = new Vector3d(1, 0, 0).rotate(rotation);
        double pitch = lookingAtForward.angle(new Vector3d(lookingAtForward.x, 0, lookingAtForward.z));
        return lookingAtForward.y > 0 ? pitch : -pitch;
    }

    public double yaw() {
        Vector3d lookingAtForward = new Vector3d(1, 0, 0).rotate(rotation);
        double yaw = new Vector3d(lookingAtForward.x, 0, lookingAtForward.z).angle(new Vector3d(1, 0, 0));
        return lookingAtForward.z > 0 ? -yaw : yaw;
    }

    public int altitude() {
        Pig pig = entity();
        assert pig != null;
        return pig.getLocation().getBlockY();
    }

    public void mount(@NotNull Player player) {
        Pig pig = entity();
        assert pig != null;
        player.setInvisible(true);
        pig.addPassenger(player);

        assert interaction != null;
        KinematicEntity<?, ?> interaction = EntityStorage.kinematicEntity(this.interaction);
        assert interaction != null;
        interaction.remove();
    }

    public void unmount(@NotNull Player player) {
        throttle = 0;
        interaction = new VehicleInteractor(this).uuid();
        player.setInvisible(false);
    }
}
