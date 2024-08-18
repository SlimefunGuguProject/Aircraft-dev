package org.metamechanists.aircraft.vehicles.components;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.aircraft.vehicles.surfaces.VehicleSurface;
import org.metamechanists.metalib.yaml.YamlTraverser;
import org.metamechanists.metalib.yaml.YamlTraverser.ErrorSetting;

import java.util.Set;


@Getter
public class AerodynamicHingeComponent extends HingeComponent implements AerodynamicComponent {
    private final double dragCoefficient;
    private final double liftCoefficient;

    @SuppressWarnings("DataFlowIssue")
    public AerodynamicHingeComponent(@NotNull YamlTraverser traverser, @NotNull YamlTraverser hingedTraverser, String name, Vector3f location, Vector3d rotation) {
        super(traverser, hingedTraverser, name, location, rotation);
        dragCoefficient = traverser.get("dragCoefficient", ErrorSetting.LOG_MISSING_KEY);
        liftCoefficient = traverser.get("liftCoefficient", ErrorSetting.LOG_MISSING_KEY);
    }

    @Override
    public @NotNull Set<VehicleSurface> getSurfaces(VehicleState state) {
        return getSurfaces(getRotation(state));
    }
}
