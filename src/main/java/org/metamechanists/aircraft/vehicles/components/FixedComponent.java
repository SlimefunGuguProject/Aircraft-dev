package org.metamechanists.aircraft.vehicles.components;

import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.metamechanists.aircraft.vehicles.VehicleSurface;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedCuboid;
import org.metamechanists.displaymodellib.transformations.TransformationMatrixBuilder;

import java.util.HashSet;
import java.util.Set;


public class FixedComponent {
    @Getter
    private final String name;
    private final double dragCoefficient;
    private final double liftCoefficient;
    private final Material material;
    @Getter
    private final Vector3f size;
    private final Vector3f locationRelativeToModelCenter;
    private final Vector3f locationRelativeToCenterOfMass;
    @Getter
    private final Vector3d rotation;

    public FixedComponent(final String name, final double dragCoefficient, final double liftCoefficient,
                          final Material material, final Vector3f size,
                          final Vector3f locationRelativeToModelCenter, final Vector3f locationRelativeToCenterOfMass, final Vector3d rotation) {
        this.name = name;
        this.dragCoefficient = dragCoefficient;
        this.liftCoefficient = liftCoefficient;
        this.material = material;
        this.size = size;
        this.locationRelativeToModelCenter = locationRelativeToModelCenter;
        this.locationRelativeToCenterOfMass = locationRelativeToCenterOfMass;
        this.rotation = rotation;
    }

    private @NotNull VehicleSurface getSurface(final @NotNull Vector3d startingLocation,
                                               final double surfaceWidth, final double surfaceHeight, final @NotNull Vector3d rotation) {
        final double area = surfaceWidth * surfaceHeight;
        final Matrix4f rotationMatrix = new TransformationMatrixBuilder().rotate(this.rotation).rotate(rotation).buildForItemDisplay();
        final Vector4d relativeLocation4 = new Vector4d(startingLocation, 1.0).mul(rotationMatrix);
        final Vector3d relativeLocation = new Vector3d(relativeLocation4.x, relativeLocation4.y, relativeLocation4.z);
        final Vector3d normal = new Vector3d(relativeLocation).normalize();
        return new VehicleSurface(dragCoefficient, liftCoefficient, area, normal, new Vector3d(locationRelativeToCenterOfMass).add(relativeLocation));
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

    public ModelAdvancedCuboid getCuboid(final Vector3d rotation, final @NotNull Vector3d translation) {
        return new ModelAdvancedCuboid()
                .material(material)
                .translate(locationRelativeToCenterOfMass)
                .rotate(this.rotation)
                .rotate(rotation)
                .translate(new Vector3f((float) translation.x, (float) translation.y, (float) translation.z))
                .scale(size);
    }

    public ModelAdvancedCuboid getCuboid() {
        return getCuboid(new Vector3d(), new Vector3d());
    }
}
