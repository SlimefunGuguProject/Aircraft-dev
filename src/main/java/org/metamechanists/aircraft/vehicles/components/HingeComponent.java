package org.metamechanists.aircraft.vehicles.components;

import org.joml.Vector3d;
import org.metamechanists.aircraft.utils.models.ModelCuboid;
import org.metamechanists.aircraft.vehicles.VehicleSurface;

import java.util.Set;


public class HingeComponent implements VehicleComponent {
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

    @Override
    public String getName() {
        return fixedComponent.getName();
    }

    @Override
    public Set<VehicleSurface> getSurfaces() {
        return fixedComponent.getSurfaces();
    }

    @Override
    public ModelCuboid getCuboid() {
        return fixedComponent.getCuboid();
    }
}
