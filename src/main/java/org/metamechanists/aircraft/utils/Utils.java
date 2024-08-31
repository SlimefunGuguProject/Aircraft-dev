package org.metamechanists.aircraft.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelComponent;
import org.metamechanists.displaymodellib.models.components.ModelItem;
import org.metamechanists.displaymodellib.models.components.ModelText;


@UtilityClass
public class Utils {
    public final int BRIGHTNESS_ON = 15;
    private final Vector3f PLAYER_HEAD_OFFSET = new Vector3f(0, 1.2F, 0);

    public double clampToRange(double value, double min, double max) {
        return Math.max(Math.min(value, max), min);
    }

    public ModelItem getDefaultModelItem(@NotNull VehicleState state) {
        Quaternionf quaternionf = new Quaternionf();
        state.rotation.get(quaternionf);
        return new ModelItem()
                .translate(PLAYER_HEAD_OFFSET)
                .rotate(quaternionf);
    }

    public ModelText getDefaultModelText(@NotNull VehicleState state) {
        Quaternionf quaternionf = new Quaternionf();
        state.rotation.get(quaternionf);
        return new ModelText()
                .translate(PLAYER_HEAD_OFFSET)
                .rotate(quaternionf);
    }

    public Vector3d rotateBackwards(@NotNull Vector3d vector, @NotNull Quaterniond rotation) {
        return new Vector3d(vector).rotate(new Quaterniond(rotation).invert());
    }
}
