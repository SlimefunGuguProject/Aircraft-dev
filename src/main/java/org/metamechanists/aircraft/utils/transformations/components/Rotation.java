package org.metamechanists.aircraft.utils.transformations.components;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;


public class Rotation implements TransformationMatrixComponent {
    private final Vector3f rotation;

    public Rotation(@NotNull final Vector3d rotation) {
        this.rotation = new Vector3f((float) rotation.x, (float) rotation.y, (float) rotation.z);
    }

    @Override
    public void apply(@NotNull final Matrix4f matrix) {
        matrix.rotateXYZ(rotation);
    }
}
