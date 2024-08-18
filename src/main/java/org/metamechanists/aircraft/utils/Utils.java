package org.metamechanists.aircraft.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.displaymodellib.models.components.ModelComponent;


@UtilityClass
public class Utils {
    public final int BRIGHTNESS_ON = 15;
    private final Vector3f PLAYER_HEAD_OFFSET = new Vector3f(0, 1.2F, 0);

    public double clampToRange(double value, double min, double max) {
        return Math.max(Math.min(value, max), min);
    }

    public Matrix4f getComponentMatrix(@NotNull ModelComponent component, @NotNull Quaterniond rotation) {
        Quaternionf quaternionf = new Quaternionf();
        rotation.get(quaternionf);
        return new Matrix4f()
                .rotate(quaternionf)
//                .translate(PLAYER_HEAD_OFFSET)
//                .rotateY((float) rotation.y)
//                .rotateZ((float) rotation.z)
//                .rotateX((float) rotation.x)
                .mul(component.getMatrix());
    }

    public Vector3d rotate(@NotNull Vector3d vector, @NotNull Quaterniond rotation) {
        return new Vector3d(vector).rotate(rotation);
    }

    public Vector3d rotateBackwards(@NotNull Vector3d vector, @NotNull Quaterniond rotation) {
        return new Vector3d(vector).rotate(rotation);
    }
}
