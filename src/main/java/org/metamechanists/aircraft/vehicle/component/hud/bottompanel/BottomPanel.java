package org.metamechanists.aircraft.vehicle.component.hud.bottompanel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Interaction;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.aircraft.vehicle.component.base.HudSection;
import org.metamechanists.aircraft.vehicle.component.hud.compass.CompassBar;
import org.metamechanists.aircraft.vehicle.component.hud.compass.CompassDegree;
import org.metamechanists.aircraft.vehicle.component.hud.compass.CompassDirection;
import org.metamechanists.aircraft.vehicle.component.hud.compass.CompassNotch;
import org.metamechanists.kinematiccore.api.storage.StateReader;
import org.metamechanists.metalib.yaml.YamlTraverser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class BottomPanel extends HudSection<BottomPanel.BottomPanelSchema> {
    public static class BottomPanelSchema extends HudSectionSchema {
        private final QuantityBar.QuantityBarSchema throttleBarBackgroundSchema;
        private final QuantityBar.QuantityBarSchema throttleBarForegroundSchema;

        public BottomPanelSchema(@NotNull String id, @NotNull YamlTraverser traverser) {
            super(id, traverser, BottomPanel.class, Interaction.class);
            throttleBarBackgroundSchema = new QuantityBar.QuantityBarSchema(id, "throttleBackground", this, traverser);
            throttleBarForegroundSchema = new QuantityBar.QuantityBarSchema(id, "throttleForeground", this, traverser);
        }

        @Override
        public void unregister() {
            super.unregister();
            throttleBarBackgroundSchema.unregister();
            throttleBarForegroundSchema.unregister();
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
        components.add(new QuantityBar(schema().throttleBarBackgroundSchema, vehicleEntity, 1.0F).uuid());
        components.add(new QuantityBar(schema().throttleBarForegroundSchema, vehicleEntity, 0.2F).uuid());
        return components;
    }
}
