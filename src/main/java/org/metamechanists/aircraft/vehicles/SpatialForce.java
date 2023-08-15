package org.metamechanists.aircraft.vehicles;

import org.joml.Vector3d;


public record SpatialForce(Vector3d force, Vector3d relativeLocation) {
    public Vector3d getTorqueVector() {
        return new Vector3d(force).cross(relativeLocation);
    }
}
