package org.metamechanists.aircraft.vehicles;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;


public class AircraftSurface {
    private static final double AIR_DENSITY = 1.204;

    private final double liftCoefficient;
    private final double dragCoefficient;
    private final double area;
    private final Vector3d normal;
    private final Vector3d relativeLocation;

    public AircraftSurface(final double dragCoefficient, final double liftCoefficient, final double area, final Vector3d normal, final Vector3d relativeLocation) {
        this.dragCoefficient = dragCoefficient;
        this.liftCoefficient = liftCoefficient;
        this.area = area;
        this.normal = normal;
        this.relativeLocation = relativeLocation;
    }

    public double getRelativeArea(final @NotNull Vector3d aircraftVelocity) {
        final Vector3d airflowVelocity = aircraftVelocity.mul(-1);
        return new Vector3d(normal).dot(airflowVelocity) * area;
    }

    public SpatialForce getDragForce(final @NotNull Vector3d aircraftVelocity) {
        // Check the airflow isn't coming *out* of the surface as opposed to going into it
        final double angle = normal.angle(aircraftVelocity);
        if (normal.angle(aircraftVelocity) > Math.PI) {
            return new SpatialForce(new Vector3d(), relativeLocation);
        }

        // D = 0.5 * Cd * ρ * A * V^2, where
        // D = drag force
        // Cd = coefficient of drag
        // ρ = air density
        // A = surface area facing airflow
        // V = aircraft velocity
        final double speed = aircraftVelocity.length();
        final Vector3d dragDirection = new Vector3d(aircraftVelocity).mul(-1).normalize();
        final Vector3d force = dragDirection.mul(0.5 * dragCoefficient * AIR_DENSITY * getRelativeArea(aircraftVelocity) * speed * speed);
        return new SpatialForce(force, relativeLocation);
    }

    public SpatialForce getLiftForce(final @NotNull Vector3d aircraftVelocity) {
        // Check the airflow isn't coming *out* of the surface as opposed to going into it
        final double angle = normal.angle(aircraftVelocity);
        if (normal.angle(aircraftVelocity) > Math.PI) {
            return new SpatialForce(new Vector3d(), relativeLocation);
        }

        // L = 0.5 * Cl * ρ * A * V^2,
        // L = lift force
        // Cl = coefficient of lift
        // ρ = air density
        // A = surface area facing airflow
        // V = aircraft velocity
        final double speed = aircraftVelocity.length();
        final Vector3d perpendicularDirection = new Vector3d(aircraftVelocity).cross(normal);
        final Vector3d liftDirection = new Vector3d(perpendicularDirection).cross(aircraftVelocity);

        final Vector3d force = liftDirection.mul(0.5 * dragCoefficient * AIR_DENSITY * getRelativeArea(aircraftVelocity) * speed * speed);
        return new SpatialForce(force, relativeLocation);
    }
}
