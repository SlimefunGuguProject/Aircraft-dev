package org.metamechanists.aircraft.vehicles;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.models.components.ModelLine;


public class SpatialForce {
    private final String name;
    private final ForceType type;
    @Getter
    private final Vector3d force;
    private final Vector3d absoluteLocation;

    public SpatialForce(final String name, final ForceType type, final Vector3d force, final Vector3d absoluteLocation) {
        this.name = name;
        this.type = type;
        this.force = force;
        this.absoluteLocation = absoluteLocation;
    }
    
    public Vector3d getTorqueVector(final @NotNull Vector3d rotation) {
        return new Vector3d(absoluteLocation).cross(force);
    }

    public ModelLine visualise() {
        final Vector3f from = new Vector3f((float) absoluteLocation.x, (float) absoluteLocation.y, (float) absoluteLocation.z);
//                .rotateX((float) rotation.x)
//                .rotateY((float) rotation.y)
//                .rotateZ((float) rotation.z);
        final Vector3f to = new Vector3f(from).add(new Vector3f((float) force.x, (float) force.y, (float) force.z).mul(8));
        return new ModelLine()
                .from(from)
                .to(to)
                .thickness(0.04F)
                .material(type.getMaterial());
    }

    public String getId() {
        return name + type.toString();
    }
}
