package org.metamechanists.aircraft.vehicles.components;

import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
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
    @Getter
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

    private @NotNull VehicleSurface getSurface(final @NotNull Vector3d startingLocation, final double surfaceWidth, final double surfaceHeight, final @NotNull Vector3d rotation, final @NotNull Vector3d translation) {
        final double area = surfaceWidth * surfaceHeight;
        final Matrix4d rotationMatrix = new Matrix4d().rotateXYZ(rotation.x, rotation.y, rotation.z).rotateXYZ(this.rotation.x, this.rotation.y, this.rotation.z);
        final Vector4d relativeLocation4 = new Vector4d(startingLocation, 1.0).mul(rotationMatrix);
        final Vector3d relativeLocation = new Vector3d(relativeLocation4.x, relativeLocation4.y, relativeLocation4.z);
        final Vector3d normal = new Vector3d(relativeLocation).normalize();
        return new VehicleSurface(dragCoefficient, liftCoefficient, area, normal, new Vector3d(location).add(relativeLocation));
    }

    public Set<VehicleSurface> getSurfaces(final Vector3d rotation, final Vector3d translation) {
        final Set<VehicleSurface> surfaces = new HashSet<>();

        surfaces.add(getSurface(new Vector3d(0, 0, size.z / 2), size.x, size.y, rotation, translation));
        surfaces.add(getSurface(new Vector3d(0, 0, -size.z / 2), size.x, size.y, rotation, translation));

        surfaces.add(getSurface(new Vector3d(0, size.y / 2, 0), size.x, size.z, rotation, translation));
        surfaces.add(getSurface(new Vector3d(0, -size.y / 2, 0), size.x, size.z, rotation, translation));

        surfaces.add(getSurface(new Vector3d(size.x / 2, 0, 0), size.y, size.z, rotation, translation));
        surfaces.add(getSurface(new Vector3d(-size.x / 2, 0, 0), size.y, size.z, rotation, translation));

        return surfaces;
    }

    public Set<VehicleSurface> getSurfaces() {
        return getSurfaces(new Vector3d(), new Vector3d());
    }

    public ModelCuboid getCuboid(final Vector3d rotation, final Vector3d translation) {
        return new ModelCuboid().material(material).size(size).location(location).rotation(this.rotation).secondRotation(rotation).secondLocation(translation);
    }

    public ModelCuboid getCuboid() {
        return getCuboid(new Vector3d(), new Vector3d());
    }
}
