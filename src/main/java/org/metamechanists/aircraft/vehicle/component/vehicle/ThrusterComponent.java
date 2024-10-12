package org.metamechanists.aircraft.vehicle.component.vehicle;

import com.destroystokyo.paper.ParticleBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Pig;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.Utils;
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
        private final Vector3d direction;
        private final Particle particle;
        private final double particleSpeed;

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
            direction = thrusterTraverser.getVector3d("direction", YamlTraverser.ErrorSetting.LOG_MISSING_KEY);
            particle = Particle.valueOf(thrusterTraverser.get("particle", YamlTraverser.ErrorSetting.LOG_MISSING_KEY));
            particleSpeed = thrusterTraverser.get("particleSpeed", YamlTraverser.ErrorSetting.LOG_MISSING_KEY);
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
        Vector3d force = new Vector3d(schema().direction).normalize().mul(thrust);
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
        if (!active) {
            return;
        }

        // Vector3d absoluteLocation = new Vector3d(schema().getLocation())
        //         .rotate(vehicleEntity.getRotation())
        //         .add(Utils.PLAYER_HEAD_OFFSET);
        Vector3d absoluteLocation = new Vector3d(Utils.PLAYER_HEAD_OFFSET).add(new Vector3d(0.0, 1.0, 0.0));
        Vector3d absoluteDirection = new Vector3d(schema().direction)
                .rotate(vehicleEntity.getRotation())
                .normalize()
                .mul(-1);

        Pig pig = vehicleEntity.entity();
        assert pig != null;
        Location location = pig.getLocation()
                .add(Vector.fromJOML(absoluteLocation));

        new ParticleBuilder(schema().particle)
                .location(location)
                .count(0)
                .extra(schema().particleSpeed)
                .offset(absoluteDirection.x, absoluteDirection.y, absoluteDirection.z)
                .spawn();

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

    @Override
    public void onSignal(String signal) {
        if (schema().signal.equals(signal)) {
            active = true;
        }
    }
}
