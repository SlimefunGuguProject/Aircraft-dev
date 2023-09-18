package org.metamechanists.aircraft.vehicles;

import lombok.Getter;
import org.joml.Vector3d;


public class SpatialForce {
    @Getter
    private final Vector3d force;
    private final Vector3d relativeLocation;

    public SpatialForce(final Vector3d force, final Vector3d relativeLocation) {
        this.force = force;
        this.relativeLocation = relativeLocation;
    }
    
    public Vector3d getTorqueVector() {
        return new Vector3d(relativeLocation).cross(force);
    }
}
