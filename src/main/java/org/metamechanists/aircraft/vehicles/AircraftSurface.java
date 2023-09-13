package org.metamechanists.aircraft.vehicles;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;


public class AircraftSurface {
    private static final double AIR_DENSITY = 1.2;

    private final String name;
    private final double liftCoefficient;
    private final double dragCoefficient;
    private final double area;
    private final Vector3d relativeNormal;
    private final Vector3d relativeLocation;

    public AircraftSurface(final String name, final double dragCoefficient, final double liftCoefficient, final double area, final Vector3d relativeNormal, final Vector3d relativeLocation) {
        this.name = name;
        this.dragCoefficient = dragCoefficient;
        this.liftCoefficient = liftCoefficient;
        this.area = area;
        this.relativeNormal = relativeNormal;
        this.relativeLocation = relativeLocation;
    }

    private double getRelativeArea(final @NotNull Vector3d normal, final @NotNull Vector3d airflowVelocity) {
        return new Vector3d(normal).angleCos(airflowVelocity) * area;
    }

    public SpatialForce getDragForce(final @NotNull Vector3d aircraftRotation, final @NotNull Vector3d aircraftVelocity) {
        final Vector3d normal = new Vector3d(relativeNormal).rotateX(aircraftRotation.x).rotateY(aircraftRotation.y).rotateZ(aircraftRotation.z);
        final Vector3d airflowVelocity = new Vector3d(aircraftVelocity).mul(-1);

        // Check the airflow isn't coming *out* of the surface as opposed to going into it
        if (normal.angle(airflowVelocity) < Math.PI / 2) {
            return new SpatialForce(name, ForceType.DRAG, new Vector3d(), relativeLocation);
        }

        // Check the airflow and normal are not in opposite directions - this causes NaN values
//        if (normal.angle(airflowVelocity) < 0.001) {
//            return new SpatialForce(name, ForceType.DRAG, new Vector3d(), relativeLocation);
//        }

        // D = 0.5 * Cd * ρ * A * V^2, where
        // D = drag force
        // Cd = coefficient of drag
        // ρ = air density
        // A = surface area facing airflow
        // V = aircraft velocity
        final double aircraftSpeed = aircraftVelocity.length();
        final Vector3d dragDirection = new Vector3d(airflowVelocity).mul(-1).normalize();
        //final Vector3d dragDirection = normal;
        final Vector3d force = dragDirection.mul(
                Math.sin(normal.angle(airflowVelocity))
                        * 0.5
                        * dragCoefficient
                        * AIR_DENSITY
                        * getRelativeArea(normal, airflowVelocity)
                        * (aircraftSpeed * aircraftSpeed));
        return new SpatialForce(name, ForceType.DRAG, force, relativeLocation);
    }

    public SpatialForce getLiftForce(final @NotNull Vector3d aircraftRotation, final @NotNull Vector3d aircraftVelocity) {
        final Vector3d normal = new Vector3d(relativeNormal).rotateX(-aircraftRotation.x).rotateY(-aircraftRotation.y).rotateZ(-aircraftRotation.z);
        final Vector3d airflowVelocity = new Vector3d(aircraftVelocity).mul(-1);

        // Check the airflow isn't coming *out* of the surface as opposed to going into it
        if (normal.angle(airflowVelocity) < Math.PI / 2) {
            return new SpatialForce(name, ForceType.LIFT, new Vector3d(), relativeLocation);
        }

        // Check the airflow and normal are not in opposite directions - this causes NaN values
//        if (normal.angle(airflowVelocity) > (Math.PI - 0.001)) {
//            return new SpatialForce(name, ForceType.LIFT, new Vector3d(), relativeLocation);
//        }

        // L = 0.5 * Cl * ρ * A * V^2,
        // L = lift force
        // Cl = coefficient of lift
        // ρ = air density
        // A = surface area facing airflow
        // V = aircraft velocity
        final double aircraftSpeed = aircraftVelocity.length();
        final Vector3d perpendicularDirection = new Vector3d(normal).cross(airflowVelocity);
        final Vector3d liftDirection = new Vector3d(normal).normalize();// new Vector3d(perpendicularDirection).cross(airflowVelocity).normalize();

        final Vector3d force = liftDirection.mul(
                Math.sin(2.0*normal.angle(airflowVelocity))
                        * 0.5
                        * liftCoefficient
                        * AIR_DENSITY
                        * getRelativeArea(normal, airflowVelocity)
                        * (aircraftSpeed * aircraftSpeed));
        return new SpatialForce(name, ForceType.LIFT, force, relativeLocation);
    }
}
