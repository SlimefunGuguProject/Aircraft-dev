package org.metamechanists.aircraft.vehicle.component.hud.bottompanel;

import lombok.Setter;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
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
        private final boolean foreground ;
        private final Material material;
        private final double width;
        private final double height;
        private final double offset;

        public QuantityBarSchema(
                @NotNull String id,
                @NotNull String prefix,
                boolean foreground,
                @NotNull HudSection.HudSectionSchema sectionSchema,
                @NotNull YamlTraverser traverser
        ) {
            super(id + "_bottom_panel_" + prefix, sectionSchema, traverser, QuantityBar.class, ItemDisplay.class);
            this.foreground = foreground;
            material = Material.getMaterial(traverser.get(prefix + "Material"));
            assert material != null;
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
        Bukkit.getServer().getLogger().info(schema().material.toString());
        Bukkit.getServer().getLogger().info(String.valueOf(schema().width));
        Bukkit.getServer().getLogger().info(String.valueOf(schema().height));
        Bukkit.getServer().getLogger().info(String.valueOf(schema().offset));
        Bukkit.getServer().getLogger().info(String.valueOf(fraction));
        double offsetX = -0.5F * schema().width + 0.5F * (1.0 - fraction) * schema().width;
        return schema().getSectionSchema().rollCuboid(vehicleEntity)
                .material(schema().material)
                .translate(offsetX, schema().offset, 0.5F + (schema().foreground ? 0.02F : 0.0F))
                .scale(new Vector3f((float) schema().width * fraction, (float) schema().height, 0.001F))
                .translate(0.5F, 0.35F, 0.0F);
    }
}
