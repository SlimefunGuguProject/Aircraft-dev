package org.metamechanists.aircraft.vehicles.forces;

import org.joml.Vector3d;


public record SpatialForce(SpatialForceType type, Vector3d force, Vector3d absoluteLocation, Vector3d relativeLocation) {
    public Vector3d getTorqueVector() {
        return new Vector3d(relativeLocation).cross(force);
    }
}
