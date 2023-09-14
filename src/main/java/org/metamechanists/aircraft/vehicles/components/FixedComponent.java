package org.metamechanists.aircraft.vehicles.components;

import org.bukkit.Material;
import org.joml.Vector3d;


public class FixedComponent implements VehicleComponent {
    private final double density;
    private final double dragCoefficient;
    private final double liftCoefficient;
    private final Material material;
    private final Vector3d size;
    private final Vector3d location;

    public FixedComponent(final double density, final double dragCoefficient, final double liftCoefficient, final Material material, final Vector3d size, final Vector3d location) {
        this.density = density;
        this.dragCoefficient = dragCoefficient;
        this.liftCoefficient = liftCoefficient;
        this.material = material;
        this.size = size;
        this.location = location;
    }
}
