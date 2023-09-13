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
    private final Vector3d relativeLocation;

    public SpatialForce(final String name, final ForceType type, final Vector3d force, final Vector3d relativeLocation) {
        this.name = name;
        this.type = type;
        this.force = force;
        this.relativeLocation = relativeLocation;
    }
    
    public Vector3d getTorqueVector() {
        return new Vector3d(force).cross(relativeLocation);
    }

    public ModelLine visualise() {
        final Vector3f from = new Vector3f((float) relativeLocation.x, (float) relativeLocation.y, (float) relativeLocation.z);
        final Vector3f to = new Vector3f(from).add(new Vector3f((float) force.x, (float) force.y, (float) force.z).mul(25));
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
