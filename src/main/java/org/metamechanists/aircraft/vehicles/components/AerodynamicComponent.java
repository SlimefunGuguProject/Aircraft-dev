package org.metamechanists.aircraft.vehicles.components;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.aircraft.vehicles.surfaces.VehicleSurface;
import org.metamechanists.displaymodellib.transformations.TransformationMatrixBuilder;
import org.metamechanists.metalib.yaml.YamlTraverser;
import org.metamechanists.metalib.yaml.YamlTraverser.ErrorSetting;

import java.util.HashSet;
import java.util.Set;


public interface AerodynamicComponent extends Component {
    double MIN_AREA = 0.01;
    @NotNull Set<VehicleSurface> getSurfaces(VehicleState state);
    double getDragCoefficient();
    double getLiftCoefficient();

    static @NotNull AerodynamicComponent fromTraverser(@NotNull YamlTraverser traverser, Vector3f translation) {
        YamlTraverser hingedTraverser = traverser.getSection("hinged", ErrorSetting.NO_BEHAVIOUR);
        if (hingedTraverser != null) {
            return new AerodynamicHingeComponent(traverser, hingedTraverser, translation);
        }
        return new AerodynamicFixedComponent(traverser, translation);
    }

    private @Nullable VehicleSurface getSurface(@NotNull Vector3d startingLocation, double surfaceWidth, double surfaceHeight, @NotNull Vector3d extraRotation) {
        double area = surfaceWidth * surfaceHeight;
        if (area < MIN_AREA) {
            return null;
        }
        Matrix4f rotationMatrix = new TransformationMatrixBuilder().rotate(getRotation()).rotate(extraRotation).buildForItemDisplay();
        Vector4d relativeLocation4 = new Vector4d(startingLocation, 1.0).mul(rotationMatrix);
        Vector3d relativeLocation = new Vector3d(relativeLocation4.x, relativeLocation4.y, relativeLocation4.z);
        Vector3d normal = new Vector3d(relativeLocation).normalize();
        return new VehicleSurface(getDragCoefficient(), getLiftCoefficient(), area, normal, new Vector3d(getLocation()).add(relativeLocation));
    }

    @SuppressWarnings("SuspiciousNameCombination")
    default @NotNull Set<VehicleSurface> getSurfaces(Vector3d extraRotation) {
        Set<VehicleSurface> surfaces = new HashSet<>();

        VehicleSurface surface = getSurface(new Vector3d(0, 0, getSize().z / 2), getSize().x, getSize().y, extraRotation);
        if (surface != null) { surfaces.add(surface); }
        surface = getSurface(new Vector3d(0, 0, -getSize().z / 2), getSize().x, getSize().y, extraRotation);
        if (surface != null) { surfaces.add(surface); }

        surface = getSurface(new Vector3d(0, getSize().y / 2, 0), getSize().x, getSize().z, extraRotation);
        if (surface != null) { surfaces.add(surface); }
        surface = getSurface(new Vector3d(0, -getSize().y / 2, 0), getSize().x, getSize().z, extraRotation);
        if (surface != null) { surfaces.add(surface); }

        surface = getSurface(new Vector3d(getSize().x / 2, 0, 0), getSize().y, getSize().z, extraRotation);
        if (surface != null) { surfaces.add(surface); }
        surface = getSurface(new Vector3d(-getSize().x / 2, 0, 0), getSize().y, getSize().z, extraRotation);
        if (surface != null) { surfaces.add(surface); }

        return surfaces;
    }
}
