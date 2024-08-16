package org.metamechanists.aircraft.vehicles.config;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.metalib.yaml.YamlTraverser;
import org.metamechanists.metalib.yaml.YamlTraverser.ErrorSetting;

import java.util.List;


public final class Util {
    private Util() {}

    public static @NotNull Vector3d getVector3d(@NotNull YamlTraverser traverser, String name) {
        List<Double> rotationList = traverser.get(name, ErrorSetting.LOG_MISSING_KEY);
        return new Vector3d(rotationList.get(0), rotationList.get(1), rotationList.get(2));
    }

    public static @NotNull Vector3f getVector3f(@NotNull YamlTraverser traverser, String name) {
        Vector3d asVector3d = getVector3d(traverser, name);
        return new Vector3f((float) asVector3d.x, (float) asVector3d.y, (float) asVector3d.z);
    }

    public static char getChar(@NotNull YamlTraverser traverser, String name) {
        return ((CharSequence) traverser.get(name, ErrorSetting.LOG_MISSING_KEY)).charAt(0);
    }
}
