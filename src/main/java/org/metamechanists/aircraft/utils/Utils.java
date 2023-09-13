package org.metamechanists.aircraft.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;


@UtilityClass
@SuppressWarnings({"unused", "WeakerAccess"})
public class Utils {
    public final int BRIGHTNESS_ON = 15;
    public final int BRIGHTNESS_OFF = 3;
    public final int BRIGHTNESS_PANEL = 12;
    public final int VIEW_RANGE_ON = 1;
    public final int VIEW_RANGE_OFF = 0;
    private final double FLOATING_POINT_THRESHOLD = 0.00001;
    public double roundTo2dp(final double value) {
        return Math.round(value*Math.pow(10, 2)) / Math.pow(10, 2);
    }

    public void clampToRange(final @NotNull Vector vector, final float min, final float max) {
        vector.setX(Math.min(vector.getX(), max));
        vector.setY(Math.min(vector.getY(), max));
        vector.setZ(Math.min(vector.getZ(), max));

        vector.setX(Math.max(vector.getX(), min));
        vector.setY(Math.max(vector.getY(), min));
        vector.setZ(Math.max(vector.getZ(), min));
    }

    public double clampToRange(final double value, final double min, final double max) {
        return Math.max(Math.min(value, max), min);
    }

    public float clampToRange(final float value, final float min, final float max) {
        return Math.max(Math.min(value, max), min);
    }

    public int clampToRange(final int value, final int min, final int max) {
        return Math.max(Math.min(value, max), min);
    }

    public boolean equal(final double a, final double b) {
        return Math.abs(a - b) < FLOATING_POINT_THRESHOLD;
    }

    public boolean equal(final float a, final float b) {
        return Math.abs(a - b) < FLOATING_POINT_THRESHOLD;
    }

    public Vector3d rotate(final @NotNull Vector3d vector, final @NotNull Vector3d rotation) {
        return new Vector3d(vector).rotateX((float) rotation.x).rotateY((float) rotation.y).rotateZ((float) rotation.z);  // new Vector3d(vector).rotateAxis(rotation.length(), rotation.x, rotation.y, rotation.z);
    }

    public Vector3f rotate(final @NotNull Vector3f vector, final @NotNull Vector3d rotation) {
        return new Vector3f(vector).rotateX((float) rotation.x).rotateY((float) rotation.y).rotateZ((float) rotation.z); //new Vector3f(vector).rotateAxis((float) rotation.length(), (float) rotation.x, (float) rotation.y, (float) rotation.z);
    }

    public Matrix4f rotate(final @NotNull Vector3d rotation) {
        return new Matrix4f().rotateXYZ(new Vector3f((float) rotation.x, (float) rotation.y, (float) rotation.z)); // new Matrix4f().rotate((float) rotation.length(), (float) rotation.x, (float) rotation.y, (float) rotation.z);
    }
}
