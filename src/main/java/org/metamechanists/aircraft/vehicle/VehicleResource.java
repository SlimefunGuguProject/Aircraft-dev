package org.metamechanists.aircraft.vehicle;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.metalib.yaml.YamlTraverser;

import java.util.ArrayList;
import java.util.List;


@Accessors(fluent = true)
public class VehicleResource {
    @Getter
    private final double capacity;
    private final List<ResourceDrain> drains = new ArrayList<>();

    @SuppressWarnings("DataFlowIssue")
    public VehicleResource(@NotNull YamlTraverser traverser) {
        capacity = traverser.get("capacity");
        YamlTraverser drainsTraverser = traverser.getSection("drains");
        for (YamlTraverser drainTraverser : drainsTraverser.getSections()) {
            drains.add(ResourceDrain.fromTraverser(drainTraverser));
        }
    }

    private interface ResourceDrain {
        double drainedThisTick(@NotNull VehicleEntity vehicleEntity);

        @SuppressWarnings("DataFlowIssue")
        static @NotNull ResourceDrain fromTraverser(@NotNull YamlTraverser traverser) {
            double rate = traverser.get("rate");
            List<String> signals = traverser.get("signals", YamlTraverser.ErrorSetting.NO_BEHAVIOUR);
            if (signals == null) {
                return new EngineResourceDrain(rate);
            }
            return new SignalResourceDrain(rate, signals);
        }
    }

    private record EngineResourceDrain(double rate) implements ResourceDrain {
        @Override
        public double drainedThisTick(@NotNull VehicleEntity vehicleEntity) {
            return rate * vehicleEntity.getThrottle();
        }
    }

    private record SignalResourceDrain(double rate, List<String> signals) implements ResourceDrain {
        @SuppressWarnings("Convert2streamapi")
        @Override
        public double drainedThisTick(@NotNull VehicleEntity vehicleEntity) {
            double total = 0.0;
            for (String signal : signals) {
                total += rate * vehicleEntity.signalCountThisTick(signal);
            }
            return total;
        }
    }

    public double drainedThisTick(@NotNull VehicleEntity vehicleEntity) {
        return drains.stream()
                .mapToDouble(drain -> drain.drainedThisTick(vehicleEntity))
                .sum();
    }
}
