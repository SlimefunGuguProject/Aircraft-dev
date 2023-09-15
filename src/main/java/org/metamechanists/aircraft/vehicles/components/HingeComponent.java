package org.metamechanists.aircraft.vehicles.components;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.metamechanists.aircraft.utils.models.ModelCuboid;
import org.metamechanists.aircraft.vehicles.ControlSurfaceOrientation;
import org.metamechanists.aircraft.vehicles.VehicleSurface;

import java.util.Map;
import java.util.Set;


public class HingeComponent {
    private final FixedComponent fixedComponent;
    private final Vector3d rotationAxis;
    private final double rotationRate;
    private final double rotationMax;
    private final char keyUp;
    private final char keyDown;

    public HingeComponent(final FixedComponent fixedComponent, final Vector3d rotationAxis, final double rotationRate, final double rotationMax, final char keyUp, final char keyDown) {
        this.fixedComponent = fixedComponent;
        this.rotationAxis = rotationAxis;
        this.rotationRate = rotationRate;
        this.rotationMax = rotationMax;
        this.keyUp = keyUp;
        this.keyDown = keyDown;
    }

    public void useKey(final Map<String, ControlSurfaceOrientation> orientations, final char key) {
        if (key == keyUp) {
            orientations.get(fixedComponent.getName()).adjust(rotationRate);
        } else if (key == keyDown) {
            orientations.get(fixedComponent.getName()).adjust(-rotationRate);
        }
    }

    public String getName() {
        return fixedComponent.getName();
    }

    private Vector3d getRotation(final @NotNull Map<String, ControlSurfaceOrientation> orientations) {
        final Vector3d relativeRotationAxis = new Vector3d(rotationAxis).rotateX(fixedComponent.getRotation().x).rotateY(fixedComponent.getRotation().y).rotateZ(fixedComponent.getRotation().z);
        return new Quaterniond().fromAxisAngleRad(relativeRotationAxis, orientations.get(fixedComponent.getName()).getAngle()).getEulerAnglesXYZ(new Vector3d());
    }

    public Set<VehicleSurface> getSurfaces(final @NotNull Map<String, ControlSurfaceOrientation> orientations) {
        return fixedComponent.getSurfaces(getRotation(orientations));
    }

    public ModelCuboid getCuboid(final Map<String, ControlSurfaceOrientation> orientations) {
        return fixedComponent.getCuboid(getRotation(orientations));
    }
}
