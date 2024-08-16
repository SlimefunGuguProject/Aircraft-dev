package org.metamechanists.aircraft.vehicles.components;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.aircraft.vehicles.surfaces.VehicleSurface;
import org.metamechanists.metalib.yaml.YamlTraverser;
import org.metamechanists.metalib.yaml.YamlTraverser.ErrorSetting;

import java.util.Set;


public final class AerodynamicComponent {
    @Getter
    private final Component component;
    private final double dragCoefficient;
    private final double liftCoefficient;

    private AerodynamicComponent(Component component, double dragCoefficient, double liftCoefficient) {
        this.component = component;
        this.dragCoefficient = dragCoefficient;
        this.liftCoefficient = liftCoefficient;
    }

    @SuppressWarnings("DataFlowIssue")
    public static @NotNull AerodynamicComponent fromTraverser(YamlTraverser traverser, Vector3f translation) {
        Component component = Component.fromTraverser(traverser, translation);
        double dragCoefficient = traverser.get("dragCoefficient", ErrorSetting.LOG_MISSING_KEY);
        double liftCoefficient = traverser.get("liftCoefficient", ErrorSetting.LOG_MISSING_KEY);
        return new AerodynamicComponent(component, dragCoefficient, liftCoefficient);
    }

    public Set<VehicleSurface> getSurfaces(@NotNull VehicleState state) {
        return component.getSurfaces(state);
    }
}
