package org.metamechanists.aircraft.vehicles.components;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelCuboid;
import org.metamechanists.metalib.yaml.YamlTraverser;
import org.metamechanists.metalib.yaml.YamlTraverser.ErrorSetting;

import static java.lang.Math.sin;


public class PropellerComponent extends BaseComponent {
    private final Vector3d rotationAxis;
    private final double maxRotationSpeed;

    @SuppressWarnings("DataFlowIssue")
    protected PropellerComponent(@NotNull YamlTraverser traverser, @NotNull YamlTraverser propellerTraverser, String name, Vector3f location, Vector3d rotation) {
        super(traverser, name, location, rotation);

        rotationAxis = propellerTraverser.getVector3d("rotationAxis", ErrorSetting.LOG_MISSING_KEY);
        maxRotationSpeed = propellerTraverser.get("maxRotationSpeed", ErrorSetting.LOG_MISSING_KEY);
    }

    private Vector3d getRotation(@NotNull VehicleState state) {
        return new Quaterniond()
                .fromAxisAngleRad(new Vector3d(rotationAxis), state.orientations.get(name).getAngle())
                .getEulerAnglesXYZ(new Vector3d());
    }

    @Override
    public ModelCuboid getCuboid(@NotNull VehicleState state) {
        Vector3d translation = new Vector3d(0.0, -size.x / 2.0, 0.0)
                .mul(sin(state.orientations.get(name).getAngle()));
        return getCuboid(getRotation(state), translation);
    }

    @Override
    public void onKey(@NotNull VehicleState state, char key) {}

    @Override
    public void update(@NotNull VehicleState state) {
        double fraction = state.throttle / 100.0;
        state.orientations.get(name).spin(fraction * maxRotationSpeed);
    }
}
