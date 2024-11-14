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
    private final ResourceType type;

    public enum ResourceType {
        COMBUSTIBLE,
        WATER
    }

    @SuppressWarnings("DataFlowIssue")
    public VehicleResource(@NotNull YamlTraverser traverser) {
        type = ResourceType.valueOf(traverser.get("type"));
        capacity = traverser.get("capacity");
        passiveEngineDrain = traverser.get("passiveEngineDrain");
        throttleDrain = traverser.get("throttleDrain");
    }

    public double drainedThisTick(@NotNull VehicleEntity vehicleEntity) {
        double total = vehicleEntity.getThrottle() * throttleDrain / 100.0;
        if (vehicleEntity.isEngineOn()) {
            total += passiveEngineDrain;
        }
        return total;
    }
}
