package org.metamechanists.aircraft.utils.transformations.components;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;


public class Rotation implements TransformationMatrixComponent {
    private final Quaterniond rotation;

    public Rotation(@NotNull final Vector3d rotation) {
        if (rotation.length() < 0.0001) {
            this.rotation = new Quaterniond();
            return;
        }
        this.rotation = new Quaterniond().fromAxisAngleRad(new Vector3d(rotation).normalize(), rotation.length());
    }

    @Override
    public void apply(@NotNull final Matrix4f matrix) {
        matrix.rotate(new Quaternionf(rotation.x, rotation.y, rotation.z, rotation.w));
//        matrix.rotateZ((float) rotation.z);
//        matrix.rotateY((float) rotation.y);
//        matrix.rotateX((float) rotation.x);
    }
}
