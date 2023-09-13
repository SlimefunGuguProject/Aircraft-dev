package org.metamechanists.aircraft.utils.transformations.components;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;


public class Rotation implements TransformationMatrixComponent {
    private final Vector3d rotation;

    public Rotation(@NotNull final Vector3d rotation) {
        this.rotation = rotation;
    }

    @Override
    public void apply(@NotNull final Matrix4f matrix) {
        if (rotation.length() < 0.0001) {
            return;
        }
        matrix.rotate(new Quaternionf().fromAxisAngleRad(new Vector3f((float) rotation.x, (float) rotation.y, (float) rotation.z), (float) rotation.length()));
//        matrix.rotateZ((float) rotation.z);
//        matrix.rotateY((float) rotation.y);
//        matrix.rotateX((float) rotation.x);
    }
}
