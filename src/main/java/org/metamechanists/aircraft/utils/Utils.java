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

    public Matrix4f getComponentMatrix(@NotNull ModelComponent component, @NotNull Vector3d rotation) {
        Matrix3f roll = new Matrix3f(
                (float) Math.cos(rotation.y), 0, (float) -Math.sin(rotation.y),
                0, 1, 0,
                (float) Math.sin(rotation.y), 0, (float) Math.cos(rotation.y)
        );
        Matrix3f pitch = new Matrix3f(
                0, 1, 0,
                (float) Math.cos(rotation.z), 0, (float) -Math.sin(rotation.z),
                (float) Math.sin(rotation.z), 0, (float) Math.cos(rotation.z)
        );
        Matrix3f yaw = new Matrix3f(
                (float) Math.cos(rotation.x), 0, (float) -Math.sin(rotation.x),
                (float) Math.sin(rotation.x), 0, (float) Math.cos(rotation.x),
                0, 1, 0
        );
        Matrix3f rotationMatrix = new Matrix3f(roll).mul(pitch).mul(yaw);
        // https://msl.cs.uiuc.edu/planning/node102.html
        return new Matrix4f(rotationMatrix)
//                .translate(PLAYER_HEAD_OFFSET)
//                .rotateY((float) rotation.y)
//                .rotateZ((float) rotation.z)
//                .rotateX((float) rotation.x)
                .mul(component.getMatrix());
    }

    public Vector3d rotate(@NotNull Vector3d vector, @NotNull Vector3d rotation) {
        return new Vector3d(vector)
                .rotateX((float) rotation.x)
                .rotateY((float) rotation.y)
                .rotateZ((float) rotation.z);
    }

    public Vector3d rotateBackwards(@NotNull Vector3d vector, @NotNull Vector3d rotation) {
        return new Vector3d(vector)
                .rotateZ((float) -rotation.z)
                .rotateY((float) -rotation.y)
                .rotateX((float) -rotation.x);
    }
}
