package org.metamechanists.aircraft.vehicles;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.models.ModelBuilder;
import org.metamechanists.aircraft.utils.models.components.ModelLine;


public record SpatialForce(ForceType type, Vector3d force, Vector3d relativeLocation) {
    public Vector3d getTorqueVector() {
        return new Vector3d(force).cross(relativeLocation);
    }
    public void visualise(final @NotNull ModelBuilder builder) {
        final Vector3f from = new Vector3f((float) relativeLocation.x, (float) relativeLocation.y, (float) relativeLocation.z);
        final Vector3f to = new Vector3f(from).add(new Vector3f((float) force.x, (float) force.y, (float) force.z));
        builder.add(hash(), new ModelLine()
                .from(from)
                .to(to)
                .thickness(0.2F)
                .material(type.getMaterial()));
    }

    private @NotNull String hash() {
        return relativeLocation.toString() + type.toString();
    }
}
