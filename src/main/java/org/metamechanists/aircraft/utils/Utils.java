package org.metamechanists.aircraft.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.displaymodellib.models.components.ModelComponent;


@UtilityClass
@SuppressWarnings({"unused", "WeakerAccess"})
public class Utils {
    public final int BRIGHTNESS_ON = 15;
    public final int BRIGHTNESS_OFF = 3;
    public final int BRIGHTNESS_PANEL = 12;
    public final int VIEW_RANGE_ON = 1;
    public final int VIEW_RANGE_OFF = 0;
    private final double FLOATING_POINT_THRESHOLD = 0.01;
    private final Vector3f PLAYER_HEAD_OFFSET = new Vector3f(0, 1.2F, 0);
    public double roundTo2dp(double value) {
        return Math.round(value*Math.pow(10, 2)) / Math.pow(10, 2);
    }

    public void clampToRange(@NotNull Vector vector, float min, float max) {
        vector.setX(Math.min(vector.getX(), max));
        vector.setY(Math.min(vector.getY(), max));
        vector.setZ(Math.min(vector.getZ(), max));

        vector.setX(Math.max(vector.getX(), min));
        vector.setY(Math.max(vector.getY(), min));
        vector.setZ(Math.max(vector.getZ(), min));
    }

    public double clampToRange(double value, double min, double max) {
        return Math.max(Math.min(value, max), min);
    }

    public float clampToRange(float value, float min, float max) {
        return Math.max(Math.min(value, max), min);
    }

    public int clampToRange(int value, int min, int max) {
        return Math.max(Math.min(value, max), min);
    }

    public boolean equal(double a, double b) {
        return Math.abs(a - b) < FLOATING_POINT_THRESHOLD;
    }

    public boolean equal(float a, float b) {
        return Math.abs(a - b) < FLOATING_POINT_THRESHOLD;
    }

    public Quaterniond getRotationAngleAxis(@NotNull Vector3d rotation) {
        if (rotation.length() < 0.0001) {
            return new Quaterniond();
        }
        return new Quaterniond().fromAxisAngleRad(rotation.x, rotation.y, rotation.z, (float) rotation.length());
    }

    public Quaterniond getRotationEulerAngles(@NotNull Vector3d rotation) {
        if (rotation.length() < 0.0001) {
            return new Quaterniond();
        }
        return new Quaterniond().identity().rotateXYZ(rotation.x, rotation.y, rotation.z);
    }

    public Matrix4f getComponentMatrix(@NotNull ModelComponent component, @NotNull Vector3d rotation) {
        return new Matrix4f()
                .translate(PLAYER_HEAD_OFFSET)
                .rotateXYZ(new Vector3f((float) rotation.x, (float) rotation.y, (float) rotation.z))
                .mul(component.getMatrix());
    }

    public Matrix4f getHudMatrix(@NotNull ModelComponent component) {
        return new Matrix4f()
                .translate(PLAYER_HEAD_OFFSET)
                .mul(component.getMatrix());
    }

    public Vector3d rotateByEulerAngles(@NotNull Vector3d vector, @NotNull Vector3d rotation) {
        return new Vector3d(vector).rotate(getRotationEulerAngles(rotation));
    }

    public Vector3f rotateByEulerAngles(@NotNull Vector3f vector, @NotNull Vector3d rotation) {
        return new Vector3f(vector).rotate(new Quaternionf(getRotationEulerAngles(rotation)));
    }
}
