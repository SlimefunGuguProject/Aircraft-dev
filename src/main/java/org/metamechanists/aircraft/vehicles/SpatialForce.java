package org.metamechanists.aircraft.vehicles;

import lombok.Getter;
import org.joml.Vector3d;


public record SpatialForce(Vector3d force, Vector3d absoluteLocation) {

    public Vector3d getTorqueVector() {
        return new Vector3d(absoluteLocation).cross(force);
    }
}
