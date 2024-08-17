package org.metamechanists.aircraft.vehicles.forces;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.vehicles.VehicleState;


public record SpatialForce(SpatialForceType type, Vector3d force, Vector3d absoluteLocation, Vector3d relativeLocation) {
    public Vector3d getTorqueVector(@NotNull VehicleState state) {
        return new Vector3d(relativeLocation).cross(Utils.rotate(force,state.rotation));
    }
}
