package org.metamechanists.aircraft.vehicles;

import lombok.Getter;
import org.joml.Vector3d;


public class SpatialForce {
    @Getter
    private final Vector3d force;
    private final Vector3d absoluteLocation;

    public SpatialForce(Vector3d force, Vector3d absoluteLocation) {
        this.force = force;
        this.absoluteLocation = absoluteLocation;
    }
    
    public Vector3d getTorqueVector() {
        return new Vector3d(absoluteLocation).cross(force);
    }
}
