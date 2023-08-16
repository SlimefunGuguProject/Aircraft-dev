package org.metamechanists.aircraft.vehicles;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.models.components.ModelLine;


public record SpatialForce(Vector3d force, Vector3d relativeLocation, ForceType type) {
    public Vector3d getTorqueVector() {
        return new Vector3d(force).cross(relativeLocation);
    }

    public ModelLine getForceLine(final Vector3d rotation) {
        final Vector3d adjustedForce = new Vector3d(force).mul(100);
        final Vector3d adjustedLocation = getLocation(rotation);
        final Vector3f originFloat = new Vector3f((float)adjustedLocation.x, (float)adjustedLocation.y, (float)adjustedLocation.z);
        final Vector3f destinationFloat = new Vector3f(originFloat).add((float)adjustedForce.x, (float)adjustedForce.y, (float)adjustedForce.z);
        return new ModelLine()
                .material(type.getMaterial())
                .brightness(15)
                .from(originFloat)
                .to(destinationFloat)
                .thickness(0.12F);
    }

    public Vector3d getLocation(final @NotNull Vector3d rotation) {
        return relativeLocation.rotateX(rotation.x).rotateY(rotation.y).rotateZ(rotation.z);
    }

    public @NotNull String stringHash() {
        return relativeLocation.toString() + type.toString();
    }
}
