package org.metamechanists.aircraft.vehicle.component.hud.compass;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.aircraft.vehicle.component.base.HudSection;
import org.metamechanists.aircraft.vehicle.component.base.HudTextComponent;
import org.metamechanists.aircraft.vehicle.component.hud.horizon.Horizon;
import org.metamechanists.displaymodellib.models.components.ModelText;
import org.metamechanists.kinematiccore.api.storage.StateReader;
import org.metamechanists.kinematiccore.api.storage.StateWriter;
import org.metamechanists.metalib.yaml.YamlTraverser;

import static java.lang.Math.PI;


public class CompassBar extends HudTextComponent<CompassBar.CompassBarSchema> {
    public static class CompassBarSchema extends HudTextComponentSchema {
        private final TextColor majorColor;
        private final TextColor minorColor;
        private final TextColor detailColor;
        private final String majorText;
        private final String minorText;
        private final String detailText;
        private final float majorSize;
        private final float minorSize;
        private final float detailSize;

        public CompassBarSchema(@NotNull String id, @NotNull HudSection.HudSectionSchema sectionSchema, @NotNull YamlTraverser traverser) {
            super(id + "_compass_bar", sectionSchema, traverser, CompassBar.class, TextDisplay.class);
            majorColor = traverser.getTextColor("majorColor");
            minorColor = traverser.getTextColor("minorColor");
            detailColor = traverser.getTextColor("detailColor");
            majorText = traverser.get("majorText");
            minorText = traverser.get("minorText");
            detailText = traverser.get("detailText");
            majorSize = traverser.get("majorSize");
            minorSize = traverser.get("minorSize ");
            detailSize = traverser.get("detailSize ");
        }
    }

    private final int index;

    public CompassBar(@NotNull CompassBar.CompassBarSchema schema, @NotNull VehicleEntity vehicleEntity, int index) {
        super(schema, vehicleEntity);
        this.index = index;
    }

    @SuppressWarnings("DataFlowIssue")
    public CompassBar(@NotNull StateReader reader) {
        super(reader);
        index = reader.get("index", int.class);
    }

    @Override
    public void write(@NotNull StateWriter writer) {
        writer.set("index", index);
    }

    @Override
    protected @NotNull ModelText modelText(@NotNull VehicleEntity vehicleEntity) {
        Vector3f compassOffset = new Vector3f((float) (0.5 * vehicleEntity.yaw()), -0.27F, 0);
        final float horizontalSpacing = 0.5F * (float) (PI / (Compass.BARS));
        Vector3f barOffset = new Vector3f(horizontalSpacing * index, 0, 0);
        Vector3f totalAdjustment = new Vector3f(barOffset).add(compassOffset);

        String text;
        TextColor color;
        float size;

        if (index % 30 == 0) {
            text = schema().majorText;
            color = schema().majorColor;
            size = schema().majorSize;
        } else if (index % 10 == 0) {
            text = schema().minorText;
            color = schema().minorColor;
            size = schema().minorSize;
        } else {
            text = schema().detailText;
            color = schema().detailColor;
            size = schema().detailSize;
        }

        setVisible(Math.abs(totalAdjustment.length()) < Horizon.RADIUS);

        return schema().getSectionSchema().rollText(vehicleEntity)
                .text(Component.text(text).color(color))
                .translate(totalAdjustment)
                .scale(new Vector3f(size, size, 0.001F))
                .translate(0.5F, 0.35F, -0.01F);
    }
}

