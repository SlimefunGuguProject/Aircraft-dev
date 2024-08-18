package org.metamechanists.aircraft.vehicles.forces;

import org.bukkit.entity.Pig;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.vehicles.VehicleConfig;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.aircraft.vehicles.forces.SpatialForce;
import org.metamechanists.aircraft.vehicles.forces.SpatialForceType;
import org.metamechanists.aircraft.vehicles.surfaces.VehicleSurface;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public final class VehicleForces {
    private VehicleForces() {}

    public static Vector3d getAcceleration(@NotNull VehicleConfig config, @NotNull Set<SpatialForce> forces) {
        Vector3d resultantForce = new Vector3d();
        forces.stream().map(SpatialForce::force).forEach(resultantForce::add);
        return new Vector3d(resultantForce).div(config.getMass());
    }

    public static Vector3d getAngularAcceleration(@NotNull VehicleConfig config, @NotNull VehicleState state, @NotNull Set<SpatialForce> forces) {
        Set<Vector3d> torqueVectors = forces.stream().map(SpatialForce::getTorqueVector).collect(Collectors.toSet());
        Vector3d resultantTorque = new Vector3d();
        torqueVectors.forEach(resultantTorque::add);

        return new Vector3d(resultantTorque).div(config.getMomentOfInertia());
    }

    public static void cancelVelocityAndAcceleration(@NotNull Pig pig, Vector3d velocity, Vector3d acceleration) {
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

    public static @NotNull Set<SpatialForce> getForces(VehicleConfig config, VehicleState state, boolean isOnGround) {
        Set<SpatialForce> forces = new HashSet<>();
        forces.add(getWeightForce(config, state));
        forces.add(getThrustForce(config, state));
        forces.addAll(getDragForces(config, state));
        forces.addAll(getLiftForces(config, state));
        forces.add(getFrictionForce(config, state, isOnGround, getAcceleration(config, forces).mul(config.getMass())));
        return forces;
    }

    private static @NotNull SpatialForce getWeightForce(@NotNull VehicleConfig config, @NotNull VehicleState state) {
        Vector3d force = Utils.rotateBackwards(new Vector3d(0, config.getGravityAcceleration() * config.getMass(), 0), state.rotation);
        Vector3d location = config.getWeightLocation();
        return new SpatialForce(SpatialForceType.WEIGHT, force, location);
    }

    private static @NotNull SpatialForce getFrictionForce(@NotNull VehicleConfig config, VehicleState state, boolean isOnGround, Vector3d force) {
        if (!isOnGround || state.velocity.length() < 0.0001) {
            return new SpatialForce(SpatialForceType.FRICTION, new Vector3d(), new Vector3d());
        }

        double horizontalForce = new Vector3d(state.velocity.x, 0.0, state.velocity.z)
                .mul(config.getMass())
                .length();
        double horizontalVelocity = new Vector3d(state.velocity.x, 0.0, state.velocity.z)
                .length();

        double frictionAmount = Math.abs(force.y) * config.getFrictionCoefficient();
        if (horizontalVelocity < 0.01) {
            // Stationary; limiting equilibrium
            frictionAmount = Math.min(frictionAmount, horizontalForce);
        }

        Vector3d frictionForce = new Vector3d(state.velocity)
                .normalize()
                .mul(-frictionAmount);
        return new SpatialForce(SpatialForceType.FRICTION, frictionForce, new Vector3d());
    }

    private static @NotNull SpatialForce getThrustForce(@NotNull VehicleConfig config, @NotNull VehicleState state) {
        double throttleFraction = state.throttle / 100.0;
        Vector3d force = new Vector3d(throttleFraction * config.getThrustForce(), 0, 0);
        Vector3d location = config.getThrustLocation();
        return new SpatialForce(SpatialForceType.THRUST, force, location);
    }

    @SuppressWarnings("Convert2streamapi")
    private static @NotNull Set<SpatialForce> getDragForces(@NotNull VehicleConfig config, @NotNull VehicleState state) {
        Set<SpatialForce> set = new HashSet<>();
        for (VehicleSurface vehicleSurface : config.getSurfaces(state)) {
            set.add(vehicleSurface.getDragForce(config.getAirDensity(), state));
        }
        return set;
    }

    @SuppressWarnings("Convert2streamapi")
    private static @NotNull Set<SpatialForce> getLiftForces(@NotNull VehicleConfig config, @NotNull VehicleState state) {
        Set<SpatialForce> set = new HashSet<>();
        for (VehicleSurface vehicleSurface : config.getSurfaces(state)) {
            set.add(vehicleSurface.getLiftForce(config.getAirDensity(), state));
        }
        return set;
    }
}
