package org.metamechanists.aircraft.vehicle.component.hud.horizon;

import org.bukkit.entity.Interaction;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.aircraft.vehicle.component.base.HudSection;
import org.metamechanists.kinematiccore.api.storage.StateReader;
import org.metamechanists.metalib.yaml.YamlTraverser;

import java.util.List;
import java.util.UUID;


public class Horizon extends HudSection<Horizon.HorizonSchema> {
    public static class HorizonSchema extends HudSection.HudSectionSchema {
        private final AltitudeText.AltitudeTextSchema altitudeTextSchema;
        private final AltitudeBrackets.AltitudeBracketsSchema altitudeBracketsSchema;
        private final VelocityIndicator.VelocityIndicatorSchema velocityIndicatorSchema;

        public HorizonSchema(@NotNull String id, @NotNull YamlTraverser traverser) {
            super(id, traverser, Horizon.class, Interaction.class);
            altitudeTextSchema = new AltitudeText.AltitudeTextSchema(this, traverser);
            altitudeBracketsSchema = new AltitudeBrackets.AltitudeBracketsSchema(this, traverser);
            velocityIndicatorSchema = new VelocityIndicator.VelocityIndicatorSchema(this, traverser);
        }

        @Override
        public void unregister() {
            altitudeTextSchema.unregister();
            altitudeBracketsSchema.unregister();
            velocityIndicatorSchema.unregister();
        }

        public static float offset(@NotNull VehicleEntity vehicleEntity) {
            return (float) (0.5F * -vehicleEntity.pitch());
        }
    }

    public Horizon(@NotNull HorizonSchema schema, @NotNull VehicleEntity vehicleEntity) {
        super(schema, vehicleEntity);
    }

    public Horizon(@NotNull StateReader reader) {
        super(reader);
    }

    @Override
    protected List<UUID> buildComponents(@NotNull VehicleEntity vehicleEntity) {
        return List.of(
                new AltitudeText(schema().altitudeTextSchema, vehicleEntity).uuid(),
                new AltitudeBrackets(schema().altitudeBracketsSchema, vehicleEntity).uuid(),
                new VelocityIndicator(schema().velocityIndicatorSchema, vehicleEntity).uuid()
        );
    }
}
