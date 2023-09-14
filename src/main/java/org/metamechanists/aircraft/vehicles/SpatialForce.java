package org.metamechanists.aircraft.vehicles;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.models.components.ModelLine;


public class SpatialForce {
    @Getter
    private final Vector3d force;
    private final Vector3d absoluteLocation;

    public SpatialForce(final Vector3d force, final Vector3d absoluteLocation) {
        this.force = force;
        this.absoluteLocation = absoluteLocation;
    }
    
    public Vector3d getTorqueVector() {
        return new Vector3d(absoluteLocation).cross(force);
    }
}
