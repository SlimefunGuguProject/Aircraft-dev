package org.metamechanists.aircraft.vehicles.components;

import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.aircraft.vehicles.surfaces.VehicleSurface;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedCuboid;
import org.metamechanists.displaymodellib.transformations.TransformationMatrixBuilder;
import org.metamechanists.metalib.yaml.YamlTraverser;
import org.metamechanists.metalib.yaml.YamlTraverser.ErrorSetting;

import java.util.HashSet;
import java.util.Set;


public abstract class Component {
    @Getter
    protected final String name;
    private final Material material;
    protected final Vector3f size;
    private final Vector3f location;
    private final Vector3d rotation;
    private final double dragCoefficient;
    private final double liftCoefficient;

    protected Component(String name, double dragCoefficient, double liftCoefficient, Material material, Vector3f size, Vector3f location, Vector3d rotation) {
        this.name = name;
        this.dragCoefficient = dragCoefficient;
        this.liftCoefficient = liftCoefficient;
        this.material = material;
        this.size = size;
        this.location = location;
        this.rotation = rotation;
    }

    public static @NotNull Component fromTraverser(@NotNull YamlTraverser traverser, Vector3f translation) {
        YamlTraverser hingedTraverser = traverser.getSection("hinged", ErrorSetting.NO_BEHAVIOUR);
        if (hingedTraverser != null) {
            return HingeComponent.newFromTraverser(traverser, hingedTraverser, translation);
        }
        return FixedComponent.newFromTraverser(traverser, translation);
    }

    protected abstract Set<VehicleSurface> getSurfaces(@NotNull VehicleState state);
    public abstract ModelAdvancedCuboid getCuboid(@NotNull VehicleState state);
    public abstract void onKey(@NotNull VehicleState state, char key);
    public abstract void update(@NotNull VehicleState state);

    private @NotNull VehicleSurface getSurface(@NotNull Vector3d startingLocation, double surfaceWidth, double surfaceHeight, @NotNull Vector3d rotation) {
        double area = surfaceWidth * surfaceHeight;
        Matrix4f rotationMatrix = new TransformationMatrixBuilder().rotate(this.rotation).rotate(rotation).buildForItemDisplay();
        Vector4d relativeLocation4 = new Vector4d(startingLocation, 1.0).mul(rotationMatrix);
        Vector3d relativeLocation = new Vector3d(relativeLocation4.x, relativeLocation4.y, relativeLocation4.z);
        Vector3d normal = new Vector3d(relativeLocation).normalize();
        return new VehicleSurface(dragCoefficient, liftCoefficient, area, normal, new Vector3d(location).add(relativeLocation));
    }

    protected Set<VehicleSurface> getSurfaces(Vector3d rotation) {
        Set<VehicleSurface> surfaces = new HashSet<>();

        surfaces.add(getSurface(new Vector3d(0, 0, size.z / 2), size.x, size.y, rotation));
        surfaces.add(getSurface(new Vector3d(0, 0, -size.z / 2), size.x, size.y, rotation));

        surfaces.add(getSurface(new Vector3d(0, size.y / 2, 0), size.x, size.z, rotation));
        surfaces.add(getSurface(new Vector3d(0, -size.y / 2, 0), size.x, size.z, rotation));

        surfaces.add(getSurface(new Vector3d(size.x / 2, 0, 0), size.y, size.z, rotation));
        surfaces.add(getSurface(new Vector3d(-size.x / 2, 0, 0), size.y, size.z, rotation));

        return surfaces;
    }

    protected ModelAdvancedCuboid getCuboid(Vector3d rotation, @NotNull Vector3d translation) {
        return new ModelAdvancedCuboid()
                .material(material)
                .translate(location)
                .rotate(this.rotation)
                .rotate(rotation)
                .translate(new Vector3f((float) translation.x, (float) translation.y, (float) translation.z))
                .scale(size);
    }
}
