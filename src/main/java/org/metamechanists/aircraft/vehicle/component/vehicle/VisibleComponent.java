package org.metamechanists.aircraft.vehicle.component.vehicle;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Pig;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.aircraft.vehicle.VehicleSurface;
import org.metamechanists.aircraft.vehicle.component.base.ItemComponent;
import org.metamechanists.aircraft.vehicle.component.base.VehicleComponent;
import org.metamechanists.displaymodellib.models.components.ModelItem;
import org.metamechanists.kinematiccore.api.state.StateReader;
import org.metamechanists.kinematiccore.api.state.StateWriter;
import org.metamechanists.metalib.yaml.YamlTraverser;

import java.util.List;
import java.util.Set;


public class VisibleComponent extends VehicleComponent<VisibleComponent.VisibleComponentSchema> {
    @Getter
    @Accessors(fluent = true)
    public static class VisibleComponentSchema extends VehicleComponentSchema {
        private final List<String> signals;

        public VisibleComponentSchema(
                @NotNull String id,
                @NotNull YamlTraverser traverser,
                @NotNull YamlTraverser visibleTraverser,
                @NotNull Vector3f translation,
                boolean mirror) {
            super(id, VisibleComponent.class, traverser, translation, mirror);
            signals = visibleTraverser.get("signal", YamlTraverser.ErrorSetting.LOG_MISSING_KEY);
        }

        @Override
        public ItemComponent<?> build(@NotNull VehicleEntity vehicleEntity) {
            return new VisibleComponent(this, vehicleEntity);
        }
    }

    private boolean visible;

    @SuppressWarnings("WeakerAccess")
    public VisibleComponent(@NotNull VisibleComponent.VisibleComponentSchema schema, @NotNull VehicleEntity vehicleEntity) {
        super(schema, () -> {
            Pig pig = vehicleEntity.entity();
            assert pig != null;
            ItemDisplay item = schema.modelItem(vehicleEntity, new Vector3d(), new Vector3d()).build(pig.getLocation());
            pig.addPassenger(item);
            return item;
        });
    }

    @SuppressWarnings("DataFlowIssue")
    public VisibleComponent(@NotNull StateReader reader) {
        super(reader);
        visible = reader.get("visible", Boolean.class);
    }

    @Override
    public void write(@NotNull StateWriter writer) {
        writer.set("visible", visible);
    }

    @Override
    protected @NotNull ModelItem modelItem(@NotNull VehicleEntity vehicleEntity) {
        return schema().modelItem(vehicleEntity, new Vector3d(), new Vector3d()).viewRange(visible ? 1 : 0);
    }

    @Override
    public void update(@NotNull VehicleEntity vehicleEntity) {
        super.update(vehicleEntity);
        visible = false;
    }

    @Override
    public void onSignal(String signal) {
        if (schema().signals.contains(signal)) {
            visible = true;
        }
    }

    @Override
    public @NotNull Set<VehicleSurface> getSurfaces() {
        return schema().getSurfaces(new Vector3d());
    }
}
