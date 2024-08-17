package org.metamechanists.aircraft.vehicles.components;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedCuboid;
import org.metamechanists.metalib.yaml.YamlTraverser;


public class FixedComponent extends BaseComponent {
    protected FixedComponent(@NotNull YamlTraverser traverser, Vector3f translation) {
        super(traverser, translation);
    }

    @Override
    public ModelAdvancedCuboid getCuboid(@NotNull VehicleState state) {
        return getCuboid(new Vector3d(), new Vector3d());
    }

    @Override
    public void onKey(@NotNull VehicleState state, char key) {}

    @Override
    public void update(@NotNull VehicleState state) {}
}
