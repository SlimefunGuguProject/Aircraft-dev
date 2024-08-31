package org.metamechanists.aircraft.vehicles.components;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelComponent;
import org.metamechanists.metalib.yaml.YamlTraverser;


public class FixedComponent extends BaseComponent {
    protected FixedComponent(@NotNull YamlTraverser traverser, String name, Vector3f location, Vector3d rotation) {
        super(traverser, name, location, rotation);
    }

    @Override
    public ModelComponent getAircraftModelComponent(@NotNull VehicleState state) {
        return getAircraftModelComponent(state, new Vector3d(), new Vector3d());
    }

    @Override
    public void onKey(@NotNull VehicleState state, char key) {}

    @Override
    public void update(@NotNull VehicleState state) {}
}
