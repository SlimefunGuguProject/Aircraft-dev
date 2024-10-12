package org.metamechanists.aircraft.vehicle.component.vehicle;

import lombok.Getter;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Pig;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.aircraft.vehicle.VehicleSurface;
import org.metamechanists.aircraft.vehicle.component.base.ItemComponent;
import org.metamechanists.aircraft.vehicle.component.base.VehicleComponent;
import org.metamechanists.aircraft.vehicle.forces.SpatialForce;
import org.metamechanists.aircraft.vehicle.forces.SpatialForceType;
import org.metamechanists.displaymodellib.models.components.ModelItem;
import org.metamechanists.kinematiccore.api.state.StateReader;
import org.metamechanists.kinematiccore.api.state.StateWriter;
import org.metamechanists.metalib.yaml.YamlTraverser;

import java.util.HashSet;
import java.util.Set;


public class ThrusterComponent extends VehicleComponent<ThrusterComponent.ThrusterComponentSchema> {
    @Getter
    public static class ThrusterComponentSchema extends VehicleComponentSchema {
        private final String signal;
        private final double thrust;

        @SuppressWarnings("DataFlowIssue")
        public ThrusterComponentSchema(
                @NotNull String id,
                @NotNull YamlTraverser traverser,
                @NotNull YamlTraverser thrusterTraverser,
                @NotNull Vector3f translation,
                boolean mirror
        ) {
            super(id, ThrusterComponent.class, traverser, translation, mirror);
            signal = thrusterTraverser.get("signal", YamlTraverser.ErrorSetting.LOG_MISSING_KEY);
            thrust = thrusterTraverser.get("thrust", YamlTraverser.ErrorSetting.LOG_MISSING_KEY);
        }

        @Override
        public ItemComponent<?> build(@NotNull VehicleEntity vehicleEntity) {
            return new ThrusterComponent(this, vehicleEntity);
        }
    }

    private boolean active;

    @SuppressWarnings("WeakerAccess")
    public ThrusterComponent(@NotNull ThrusterComponent.ThrusterComponentSchema schema, @NotNull VehicleEntity vehicleEntity) {
        super(schema, () -> {
            Pig pig = vehicleEntity.entity();
            assert pig != null;
            ItemDisplay item = schema.modelItem(vehicleEntity, new Vector3d(), new Vector3d()).build(pig.getLocation());
            pig.addPassenger(item);
            return item;
        });
    }

    public SpatialForce force() {
        double thrust = active ? schema().thrust : 0;
        Vector3d rotation = super.schema().getRotation();
        Vector3d force = new Vector3d(0, 0, thrust)
                .rotateX(rotation.x)
                .rotateY(rotation.y)
                .rotateZ(rotation.z);
        Vector3d location = new Vector3d(super.schema().getLocation());
        return new SpatialForce(SpatialForceType.THRUSTER, force, location);
    }

    @SuppressWarnings("DataFlowIssue")
    public ThrusterComponent(@NotNull StateReader reader) {
        super(reader);
        active = reader.get("active", Boolean.class);
    }

    @Override
    public void write(@NotNull StateWriter writer) {
        writer.set("active", active);
    }

    @Override
    public void update(@NotNull VehicleEntity vehicleEntity) {
        super.update(vehicleEntity);
        active = false;
    }

    @Override
    public @NotNull ModelItem modelItem(@NotNull VehicleEntity vehicleEntity) {
        return schema().modelItem(vehicleEntity, new Vector3d(), new Vector3d());
    }

    @Override
    public @NotNull Set<VehicleSurface> getSurfaces() {
        return new HashSet<>();
    }
}
