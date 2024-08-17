package org.metamechanists.aircraft.vehicles.components;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedCuboid;
import org.metamechanists.metalib.yaml.YamlTraverser;
import org.metamechanists.metalib.yaml.YamlTraverser.ErrorSetting;

import static java.lang.Math.sin;


public class HingeComponent extends BaseComponent {
    private final Vector3d rotationAxis;
    private final double rotationRate;
    private final double rotationMax;
    private final char keyUp;
    private final char keyDown;

    @SuppressWarnings("DataFlowIssue")
    protected HingeComponent(@NotNull YamlTraverser traverser, @NotNull YamlTraverser hingedTraverser, Vector3f translation) {
        super(traverser, translation);

        rotationAxis = hingedTraverser.getVector3d("rotationAxis", ErrorSetting.LOG_MISSING_KEY);
        rotationRate = hingedTraverser.get("rotationRate", ErrorSetting.LOG_MISSING_KEY);
        rotationMax = hingedTraverser.get("rotationMax", ErrorSetting.LOG_MISSING_KEY);
        keyUp = hingedTraverser.getChar("keyUp", ErrorSetting.LOG_MISSING_KEY);
        keyDown = hingedTraverser.getChar("keyDown", ErrorSetting.LOG_MISSING_KEY);
    }

    protected Vector3d getRotation(@NotNull VehicleState state) {
        return new Quaterniond()
                .fromAxisAngleRad(new Vector3d(rotationAxis), state.orientations.get(name).getAngle())
                .getEulerAnglesXYZ(new Vector3d());
    }

    @Override
    public ModelAdvancedCuboid getCuboid(@NotNull VehicleState state) {
        Vector3d translation = new Vector3d(0.0, -size.x / 2.0, 0.0)
                .mul(sin(state.orientations.get(name).getAngle()));
        return getCuboid(getRotation(state), translation);
    }

    @Override
    public void onKey(@NotNull VehicleState state, char key) {
        if (key == keyUp) {
            state.orientations.get(name).adjust(rotationRate, rotationMax);
        } else if (key == keyDown) {
            state.orientations.get(name).adjust(-rotationRate, rotationMax);
        }
    }

    @Override
    public void update(@NotNull VehicleState state) {
        state.orientations.get(name).moveTowardsCenter(rotationRate);
    }
}
