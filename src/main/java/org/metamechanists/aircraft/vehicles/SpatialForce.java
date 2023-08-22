package org.metamechanists.aircraft.vehicles;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.models.components.ModelLine;


public record SpatialForce(Vector3d force, Vector3d relativeLocation) {
    public Vector3d getTorqueVector() {
        return new Vector3d(force).cross(relativeLocation);
    }
}
