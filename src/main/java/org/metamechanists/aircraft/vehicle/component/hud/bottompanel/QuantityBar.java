package org.metamechanists.aircraft.vehicle.component.hud.bottompanel;

import lombok.Setter;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.aircraft.vehicle.component.base.HudItemComponent;
import org.metamechanists.aircraft.vehicle.component.base.HudSection;
import org.metamechanists.displaymodellib.models.components.ModelItem;
import org.metamechanists.kinematiccore.api.storage.StateReader;
import org.metamechanists.metalib.yaml.YamlTraverser;


public class QuantityBar extends HudItemComponent<QuantityBar.QuantityBarSchema> {
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
            super(id + "_bottom_panel_" + prefix, sectionSchema, traverser, QuantityBar.class, TextDisplay.class);
            material = Material.getMaterial(traverser.get(prefix + "Material"));
            height = traverser.get(prefix + "Height");
            width = traverser.get(prefix + "Width");
            offset = traverser.get(prefix + "Offset");
        }
    }

    @Setter
    private float fraction;

    public QuantityBar(@NotNull QuantityBar.QuantityBarSchema schema, @NotNull VehicleEntity vehicleEntity, float fraction) {
        super(schema, vehicleEntity);
        this.fraction = fraction;
    }

    public QuantityBar(@NotNull StateReader reader) {
        super(reader);
    }

    @Override
    protected @NotNull ModelItem modelItem(@NotNull VehicleEntity vehicleEntity) {
        return schema().getSectionSchema().rollCuboid(vehicleEntity)
                .material(schema().material)
                .translate(-0.5F * fraction * schema().width, schema().offset, 0.05F)
                .scale(new Vector3f((float) schema().width * fraction, (float) schema().height, 0.001F))
                .translate(0.5F, 0.35F, 0.0F);
    }
}
