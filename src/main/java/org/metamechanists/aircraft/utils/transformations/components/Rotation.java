package org.metamechanists.aircraft.utils.transformations.components;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;


public class Rotation implements TransformationMatrixComponent {
    private final Vector3d rotation;

    public Rotation(@NotNull final Vector3d rotation) {
        this.rotation = rotation;
    }

    @Override
    public void apply(@NotNull final Matrix4f matrix) {
        //matrix.rotate((float) rotation.length(), new Vector3f((float) rotation.x, (float) rotation.y, (float) rotation.z).normalize());
    }
}
