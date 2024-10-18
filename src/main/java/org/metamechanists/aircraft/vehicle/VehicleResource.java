package org.metamechanists.aircraft.vehicle;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.metalib.yaml.YamlTraverser;


@Accessors(fluent = true)
@Getter
public class VehicleResource {
    private final double capacity;
    private final double passiveEngineDrain;
    private final double throttleDrain;

    @SuppressWarnings("DataFlowIssue")
    public VehicleResource(@NotNull YamlTraverser traverser) {
        capacity = traverser.get("capacity");
        passiveEngineDrain = traverser.get("passiveEngineDrain ");
        throttleDrain = traverser.get("throttleDrain ");
    }

    public double drainedThisTick(@NotNull VehicleEntity vehicleEntity) {
        return passiveEngineDrain + vehicleEntity.getThrottle() * throttleDrain;
    }
}
