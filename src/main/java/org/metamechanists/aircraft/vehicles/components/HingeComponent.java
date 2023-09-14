package org.metamechanists.aircraft.vehicles.components;

import org.joml.Vector3d;
import org.metamechanists.aircraft.vehicles.VehicleSurface;

import java.util.Set;


public class HingeComponent implements VehicleComponent {
    private final FixedComponent fixedComponent;
    private final Vector3d rotationAxis;
    private final double rotationRate;
    private final double rotationMax;

    public HingeComponent(final FixedComponent fixedComponent, final Vector3d rotationAxis, final double rotationRate, final double rotationMax) {
        this.fixedComponent = fixedComponent;
        this.rotationAxis = rotationAxis;
        this.rotationRate = rotationRate;
        this.rotationMax = rotationMax;
    }

    @Override
    public Set<VehicleSurface> getSurfaces() {
        return fixedComponent.getSurfaces();
    }
}
