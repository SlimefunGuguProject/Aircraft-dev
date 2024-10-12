package org.metamechanists.aircraft.utils;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.displaymodellib.models.components.ModelItem;
import org.metamechanists.displaymodellib.models.components.ModelText;


public final class Utils {
    public static final Vector3f PLAYER_HEAD_OFFSET = new Vector3f(0, 1.2F, 0);

    private Utils() {}

    public static double clampToRange(double value, double min, double max) {
        return Math.max(Math.min(value, max), min);
    }

    public static Vector3d rotateBackwards(@NotNull Vector3d vector, @NotNull Quaterniond rotation) {
        return new Vector3d(vector).rotate(new Quaterniond(rotation).invert());
    }

    public static ModelItem defaultModelItem(@NotNull Quaterniond rotation) {
        Quaternionf quaternionf = new Quaternionf();
        rotation.get(quaternionf);
        return new ModelItem()
                .translate(PLAYER_HEAD_OFFSET)
                .rotate(quaternionf);
    }

    public static ModelText defaultModelText(@NotNull Quaterniond rotation) {
        Quaternionf quaternionf = new Quaternionf();
        rotation.get(quaternionf);
        return new ModelText()
                .translate(PLAYER_HEAD_OFFSET)
                .rotate(quaternionf);
    }
}
