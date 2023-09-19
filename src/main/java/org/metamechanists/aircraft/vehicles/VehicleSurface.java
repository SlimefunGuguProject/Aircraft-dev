package org.metamechanists.aircraft.vehicles;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.metamechanists.aircraft.utils.Utils;


public class VehicleSurface {
    private static final double AIR_DENSITY = 1.0;

    private final double liftCoefficient;
    private final double dragCoefficient;
    private final double area;
    private final Vector3d relativeNormal;
    private final Vector3d relativeLocation;

    public VehicleSurface(final double dragCoefficient, final double liftCoefficient, final double area,
                          final Vector3d relativeNormal, final Vector3d relativeLocation) {
        this.dragCoefficient = dragCoefficient;
        this.liftCoefficient = liftCoefficient;
        this.area = area;
        this.relativeNormal = relativeNormal;
        this.relativeLocation = relativeLocation;
    }

    private double getRelativeArea(final @NotNull Vector3d normal, final @NotNull Vector3d airflowVelocity) {
        return new Vector3d(normal).angleCos(airflowVelocity) * area;
    }

    public SpatialForce getLiftForce(final @NotNull Vector3d rotation, final @NotNull Vector3d velocity, final @NotNull Vector3d angularVelocity) {
        final Vector3d location = Utils.rotateByEulerAngles(new Vector3d(relativeLocation), rotation);
        final Vector3d normal = Utils.rotateByEulerAngles(new Vector3d(relativeNormal), rotation);
        final Vector3d angularVelocityVector = new Vector3d(location).cross(angularVelocity).mul(2);
        final Vector3d airflowVelocity = new Vector3d(velocity).add(angularVelocityVector).mul(-1);

        if (relativeLocation.length() - new Vector3d(0.7, 0.0, 0.6).length() < 0.000001) {
            Bukkit.broadcastMessage("" + new Vector3d(angularVelocity).normalize() + angularVelocity.length() + angularVelocityVector);
        }

        // Check the airflow isn't coming *out* of the surface as opposed to going into it
        // Also check that 1) airflow is not zero 2) airflow and normal are not in opposite directions - these cause NaN values
        if (velocity.length() < 0.000001 || normal.angle(airflowVelocity) < Math.PI / 2 || normal.angle(airflowVelocity) > (Math.PI - 0.001)) {
            return new SpatialForce(new Vector3d(), location);
        }

        // L = 0.5 * Cl * ρ * A * V^2,
        // L = lift force
        // Cl = coefficient of lift
        // ρ = air density
        // A = surface area facing airflow
        // V = aircraft velocity
        final double aircraftSpeed = velocity.length();
        final Vector3d perpendicularDirection = new Vector3d(normal).cross(airflowVelocity);
        final Vector3d liftDirection = new Vector3d(perpendicularDirection).cross(airflowVelocity).normalize();
        final Vector3d force = liftDirection.mul(
                Math.sin(2.0*normal.angle(airflowVelocity))
                        * 0.5
                        * liftCoefficient
                        * AIR_DENSITY
                        * getRelativeArea(normal, airflowVelocity)
                        * (aircraftSpeed * aircraftSpeed));
        return new SpatialForce(force, location);
    }

    public SpatialForce getDragForce(final @NotNull Vector3d rotation, final @NotNull Vector3d velocity, final @NotNull Vector3d angularVelocity) {
        final Vector3d location = Utils.rotateByEulerAngles(new Vector3d(relativeLocation), rotation);
        final Vector3d normal = Utils.rotateByEulerAngles(new Vector3d(relativeNormal), rotation);
        final Vector3d angularVelocityVector = new Vector3d(location).cross(angularVelocity).mul(2);
        final Vector3d airflowVelocity = new Vector3d(velocity).add(angularVelocityVector).mul(-1);

        // Check the airflow isn't coming *out* of the surface as opposed to going into it
        // Also check that 1) airflow is not zero 2) airflow and normal are not in opposite directions - these cause NaN values
        if (velocity.length() < 0.000001 || normal.angle(airflowVelocity) < Math.PI / 2 || normal.angle(airflowVelocity) < 0.001) {
            return new SpatialForce(new Vector3d(), location);
        }

        // D = 0.5 * Cd * ρ * A * V^2, where
        // D = drag force
        // Cd = coefficient of drag
        // ρ = air density
        // A = surface area facing airflow
        // V = aircraft velocity
        final double aircraftSpeed = velocity.length();
        final Vector3d dragDirection = new Vector3d(airflowVelocity).mul(-1).normalize();
        final Vector3d force = dragDirection.mul(
                Math.sin(normal.angle(airflowVelocity))
                        * 0.5
                        * dragCoefficient
                        * AIR_DENSITY
                        * getRelativeArea(normal, airflowVelocity)
                        * (aircraftSpeed * aircraftSpeed));
        return new SpatialForce(force, location);
    }
}
