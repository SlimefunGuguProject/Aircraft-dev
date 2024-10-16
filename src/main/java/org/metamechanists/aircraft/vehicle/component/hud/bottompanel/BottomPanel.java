package org.metamechanists.aircraft.vehicle.component.hud.bottompanel;

import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.aircraft.vehicle.component.base.HudSection;
import org.metamechanists.kinematiccore.api.state.StateReader;
import org.metamechanists.metalib.yaml.YamlTraverser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class BottomPanel extends HudSection<BottomPanel.BottomPanelSchema> {
    public static class BottomPanelSchema extends HudSectionSchema {
        private final ThrottleBar.ThrottleBarSchema throttleBarSchema;
        private final ThrottleText.ThrottleTextSchema throttleTextSchema;

        public BottomPanelSchema(@NotNull String id, @NotNull YamlTraverser traverser) {
            super(id, EntityType.INTERACTION, traverser, BottomPanel.class);
            throttleBarSchema = new ThrottleBar.ThrottleBarSchema(id, this, traverser);
            throttleTextSchema = new ThrottleText.ThrottleTextSchema(id, this, traverser);
        }
    }

    public BottomPanel(@NotNull BottomPanel.BottomPanelSchema schema, @NotNull VehicleEntity vehicleEntity) {
        super(schema, vehicleEntity);
    }

    public BottomPanel(@NotNull StateReader reader) {
        super(reader);
    }

    @Override
    protected List<UUID> buildComponents(@NotNull VehicleEntity vehicleEntity) {
        List<UUID> components = new ArrayList<>();
        components.add(new ThrottleBar(schema().throttleBarSchema, vehicleEntity).uuid());
        components.add(new ThrottleText(schema().throttleTextSchema, vehicleEntity).uuid());
        return components;
    }
}
