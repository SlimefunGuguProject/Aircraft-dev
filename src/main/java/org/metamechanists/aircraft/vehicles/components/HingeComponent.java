package org.metamechanists.aircraft.vehicles.components;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.metamechanists.aircraft.vehicles.surfaces.ControlSurfaceOrientation;
import org.metamechanists.aircraft.vehicles.surfaces.VehicleSurface;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedCuboid;

import java.util.Map;
import java.util.Set;

import static java.lang.Math.sin;


public class HingeComponent {
    private final FixedComponent fixedComponent;
    private final Vector3d rotationAxis;
    private final double rotationRate;
    private final double rotationMax;
    private final char keyUp;
    private final char keyDown;

    public HingeComponent(FixedComponent fixedComponent, Vector3d rotationAxis, double rotationRate, double rotationMax, char keyUp, char keyDown) {
        this.fixedComponent = fixedComponent;
        this.rotationAxis = rotationAxis;
        this.rotationRate = rotationRate;
        this.rotationMax = rotationMax;
        this.keyUp = keyUp;
        this.keyDown = keyDown;
    }

    public void useKey(Map<String, ControlSurfaceOrientation> orientations, char key) {
        if (key == keyUp) {
            orientations.get(fixedComponent.getName())
                    .adjust(rotationRate, rotationMax);
        } else if (key == keyDown) {
            orientations.get(fixedComponent.getName())
                    .adjust(-rotationRate, rotationMax);
        }
    }

    public void moveTowardsCenter(@NotNull Map<String, ControlSurfaceOrientation> orientations) {
        orientations.get(fixedComponent.getName()).moveTowardsCenter(rotationRate);
    }

    public String getName() {
        return fixedComponent.getName();
    }

    private Vector3d getRotation(@NotNull Map<String, ControlSurfaceOrientation> orientations) {
        return new Quaterniond()
                .fromAxisAngleRad(new Vector3d(rotationAxis), orientations.get(fixedComponent.getName()).getAngle())
                .getEulerAnglesXYZ(new Vector3d());
    }

    private Vector3d getTranslation(@NotNull Map<String, ControlSurfaceOrientation> orientations) {
        return new Vector3d(0.0, -fixedComponent.getSize().x / 2.0, 0.0)
                .mul(sin(orientations.get(fixedComponent.getName()).getAngle()));
    }

    public Set<VehicleSurface> getSurfaces(Map<String, ControlSurfaceOrientation> orientations) {
        return fixedComponent.getSurfaces(getRotation(orientations)); // Effect of translation is very small so we don't model it
    }

    public ModelAdvancedCuboid getCuboid(Map<String, ControlSurfaceOrientation> orientations) {
        return fixedComponent.getCuboid(getRotation(orientations), getTranslation(orientations));
    }
}
