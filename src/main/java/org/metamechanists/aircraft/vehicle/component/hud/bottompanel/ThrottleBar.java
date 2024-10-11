package org.metamechanists.aircraft.vehicle.component.hud.bottompanel;

import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.aircraft.vehicle.component.base.HudItemComponent;
import org.metamechanists.aircraft.vehicle.component.base.HudSection;
import org.metamechanists.displaymodellib.models.components.ModelItem;
import org.metamechanists.kinematiccore.api.storage.StateReader;
import org.metamechanists.metalib.yaml.YamlTraverser;


public class ThrottleBar extends HudItemComponent<ThrottleBar.QuantityBarSchema> {
    public static class QuantityBarSchema extends HudItemComponentSchema {
        private final Material material;
        private final double width;
        private final double height;
        private final double offset;

        public QuantityBarSchema(
                @NotNull String id,
                @NotNull String prefix,
                @NotNull HudSection.HudSectionSchema sectionSchema,
                @NotNull YamlTraverser traverser
        ) {
            super(id + "_bottom_panel_throttle", sectionSchema, traverser, ThrottleBar.class, ItemDisplay.class);
            material = Material.getMaterial(traverser.get("throttleMaterial"));
            assert material != null;
            height = traverser.get("throttleHeight");
            width = traverser.get("throttleWidth");
            offset = traverser.get("throttleOffset");
        }
    }

    public ThrottleBar(@NotNull ThrottleBar.QuantityBarSchema schema, @NotNull VehicleEntity vehicleEntity) {
        super(schema, vehicleEntity);
    }

    public ThrottleBar(@NotNull StateReader reader) {
        super(reader);
    }

    @Override
    protected @NotNull ModelItem modelItem(@NotNull VehicleEntity vehicleEntity) {
        float fraction = vehicleEntity.getThrottle() / 100.0F;
        double offsetX = -0.5F * schema().width;
        return schema().getSectionSchema().rollCuboid(vehicleEntity)
                .material(schema().material)
                .translate(offsetX, schema().offset, 0.04F)
                .scale(new Vector3f((float) schema().width * fraction, (float) schema().height, 0.001F))
                .translate(0.5F, 0.35F, 0.0F);
    }
}
