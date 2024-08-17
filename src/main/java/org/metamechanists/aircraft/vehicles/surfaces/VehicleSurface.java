package org.metamechanists.aircraft.vehicles.surfaces;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.vehicles.forces.SpatialForce;
import org.metamechanists.aircraft.vehicles.forces.SpatialForceType;
import org.metamechanists.aircraft.vehicles.VehicleState;


public class VehicleSurface {
    private final double liftCoefficient;
    private final double dragCoefficient;
    private final double area;
    private final Vector3d relativeNormal;
    private final Vector3d relativeLocation;

    public VehicleSurface(double dragCoefficient, double liftCoefficient, double area,
                          Vector3d relativeNormal, Vector3d relativeLocation) {
        this.dragCoefficient = dragCoefficient;
        this.liftCoefficient = liftCoefficient;
        this.area = area;
        this.relativeNormal = relativeNormal;
        this.relativeLocation = relativeLocation;
    }

    private double getRelativeArea(@NotNull Vector3d normal, @NotNull Vector3d airflowVelocity) {
        return new Vector3d(normal).angleCos(airflowVelocity) * area;
    }

    public SpatialForce getLiftForce(double airDensity, @NotNull VehicleState state) {
        Vector3d location = Utils.rotate(new Vector3d(relativeLocation), state.rotation);
        Vector3d normal = Utils.rotate(new Vector3d(relativeNormal), state.rotation);
        Vector3d angularVelocityVector = new Vector3d(state.angularVelocity).cross(location);
        Vector3d airflowVelocity = new Vector3d(state.velocity).add(angularVelocityVector).mul(-1);

        // Check the airflow isn't coming *out* of the surface as opposed to going into it
        // Also check that 1) airflow is not zero 2) airflow and normal are not in opposite directions - these cause NaN values
        if (state.velocity.length() < 0.000001 || normal.angle(airflowVelocity) < Math.PI / 2 || normal.angle(airflowVelocity) > (Math.PI - 0.001)) {
            return new SpatialForce(SpatialForceType.LIFT, new Vector3d(), location, relativeLocation);
        }

        // L = 0.5 * Cl * ρ * A * V^2,
        // L = lift force
        // Cl = coefficient of lift
        // ρ = air density
        // A = surface area facing airflow
        // V = aircraft velocity
        double aircraftSpeed = state.velocity.length();
        Vector3d perpendicularDirection = new Vector3d(normal).cross(airflowVelocity);
        Vector3d liftDirection = new Vector3d(perpendicularDirection).cross(airflowVelocity).normalize();
        Vector3d force = liftDirection.mul(
                Math.sin(2.0*normal.angle(airflowVelocity))
                        * 0.5
                        * liftCoefficient
                        * airDensity
                        * getRelativeArea(normal, airflowVelocity)
                        * (aircraftSpeed * aircraftSpeed));
        return new SpatialForce(SpatialForceType.LIFT, force, location, relativeLocation);
    }

    public SpatialForce getDragForce(double airDensity, @NotNull VehicleState state) {
        Vector3d location = Utils.rotate(new Vector3d(relativeLocation), state.rotation);
        Vector3d normal = Utils.rotate(new Vector3d(relativeNormal), state.rotation);
        Vector3d angularVelocityVector = new Vector3d(state.angularVelocity).cross(location);
        Vector3d airflowVelocity = new Vector3d(state.velocity).add(angularVelocityVector).mul(-1);

        // Check the airflow isn't coming *out* of the surface as opposed to going into it
        // Also check that 1) airflow is not zero 2) airflow and normal are not in opposite directions - these cause NaN values
        if (state.velocity.length() < 0.000001 || normal.angle(airflowVelocity) < Math.PI / 2 || normal.angle(airflowVelocity) < 0.001) {
            return new SpatialForce(SpatialForceType.DRAG, new Vector3d(), location, relativeLocation);
        }

        // D = 0.5 * Cd * ρ * A * V^2, where
        // D = drag force
        // Cd = coefficient of drag
        // ρ = air density
        // A = surface area facing airflow
        // V = aircraft velocity
        double aircraftSpeed = state.velocity.length();
        Vector3d dragDirection = new Vector3d(airflowVelocity).mul(-1).normalize();
        Vector3d force = dragDirection.mul(
                Math.sin(normal.angle(airflowVelocity))
                        * 0.5
                        * dragCoefficient
                        * airDensity
                        * getRelativeArea(normal, airflowVelocity)
                        * (aircraftSpeed * aircraftSpeed));
        return new SpatialForce(SpatialForceType.DRAG, force, location, relativeLocation);
    }
}
