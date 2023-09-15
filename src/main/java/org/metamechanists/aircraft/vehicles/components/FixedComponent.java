package org.metamechanists.aircraft.vehicles.components;

import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.utils.models.ModelCuboid;
import org.metamechanists.aircraft.vehicles.VehicleSurface;

import java.util.HashSet;
import java.util.Set;


public class FixedComponent {
    @Getter
    private final String name;
    private final double density;
    private final double dragCoefficient;
    private final double liftCoefficient;
    private final Material material;
    private final Vector3f size;
    private final Vector3f location;
    private final Vector3d rotation;

    public FixedComponent(final String name,
                          final double density, final double dragCoefficient, final double liftCoefficient,
                          final Material material, final Vector3f size, final Vector3f location, final Vector3d rotation) {
        this.name = name;
        this.density = density;
        this.dragCoefficient = dragCoefficient;
        this.liftCoefficient = liftCoefficient;
        this.material = material;
        this.size = size;
        this.location = location;
        this.rotation = rotation;
    }

    private @NotNull VehicleSurface getSurface(final @NotNull Vector3d startingLocation, final double surfaceWidth, final double surfaceHeight, final Vector3d rotation) {
        final double area = surfaceWidth * surfaceHeight;
        final Vector3d relativeLocation = Utils.rotate(startingLocation, this.rotation).rotateX(rotation.x).rotateY(rotation.y).rotateZ(rotation.z);
        final Vector3d normal = new Vector3d(relativeLocation).normalize();
        return new VehicleSurface(dragCoefficient, liftCoefficient, area, normal, new Vector3d(location).add(relativeLocation));
    }

    public Set<VehicleSurface> getSurfaces(final Vector3d rotation) {
        final Set<VehicleSurface> surfaces = new HashSet<>();

        surfaces.add(getSurface(new Vector3d(0, 0, size.z / 2), size.x, size.y, rotation));
        surfaces.add(getSurface(new Vector3d(0, 0, -size.z / 2), size.x, size.y, rotation));

        surfaces.add(getSurface(new Vector3d(0, size.y / 2, 0), size.x, size.z, rotation));
        surfaces.add(getSurface(new Vector3d(0, -size.y / 2, 0), size.x, size.z, rotation));

        surfaces.add(getSurface(new Vector3d(size.x / 2, 0, 0), size.y, size.z, rotation));
        surfaces.add(getSurface(new Vector3d(-size.x / 2, 0, 0), size.y, size.z, rotation));

        return surfaces;
    }

    public Set<VehicleSurface> getSurfaces() {
        return getSurfaces(new Vector3d());
    }

    public ModelCuboid getCuboid(final Vector3d rotation) {
        final Vector3d resultantRotation = new Vector3d(this.rotation).add(rotation);
        return new ModelCuboid().material(material).size(size).location(location).rotation(resultantRotation);
    }

    public ModelCuboid getCuboid() {
        return new ModelCuboid().material(material).size(size).location(location).rotation(new Vector3d());
    }
}
