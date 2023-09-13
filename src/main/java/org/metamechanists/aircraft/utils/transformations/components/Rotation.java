package org.metamechanists.aircraft.utils.transformations.components;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;


public class Rotation implements TransformationMatrixComponent {
    private final Vector3d rotation;

    public Rotation(@NotNull final Vector3d rotation) {
        this.rotation = rotation;
    }

    @Override
    public void apply(@NotNull final Matrix4f matrix) {
        matrix.rotateZ((float) rotation.z);
        matrix.rotateY((float) rotation.y);
        matrix.rotateX((float) rotation.x);
    }
}
