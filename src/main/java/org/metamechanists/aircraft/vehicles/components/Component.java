package org.metamechanists.aircraft.vehicles.components;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelItem;


public interface Component {
    String getName();
    Vector3f getSize();
    Vector3f getLocation();
    Vector3d getRotation();
    ModelItem getCuboid(@NotNull VehicleState state);
    void onKey(@NotNull VehicleState state, char key);
    void update(@NotNull VehicleState state);
}
