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
        private final List<ResourceBar.ResourceBarSchema> resourceBarSchemas = new ArrayList<>();
        private final List<ResourceText.ResourceTextSchema> resourceTextSchemas = new ArrayList<>();

        public BottomPanelSchema(@NotNull String id, @NotNull YamlTraverser traverser) {
            super(id, EntityType.INTERACTION, traverser, BottomPanel.class);

            // Throttle
            YamlTraverser throttleTraverser = traverser.getSection("throttle");
            throttleBarSchema = new ThrottleBar.ThrottleBarSchema(id, this, traverser);
            throttleTextSchema = new ThrottleText.ThrottleTextSchema(id, this, traverser);

            // Resources
            YamlTraverser resourcesTraverser = traverser.getSection("resources");
            for (YamlTraverser resourceTraverser : resourcesTraverser.getSections()) {
                resourceBarSchemas.add(new ResourceBar.ResourceBarSchema(id, resourceTraverser.name(), this, resourceTraverser));
                resourceTextSchemas.add(new ResourceText.ResourceTextSchema(id, resourceTraverser.name(), this, resourceTraverser));
            }
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
        for (ResourceBar.ResourceBarSchema resourceBarSchema : schema().resourceBarSchemas) {
            components.add(new ResourceBar(resourceBarSchema, vehicleEntity).uuid());
        }
        for (ResourceText.ResourceTextSchema resourceTextSchema : schema().resourceTextSchemas) {
            components.add(new ResourceText(resourceTextSchema, vehicleEntity).uuid());
        }
        return components;
    }
}
