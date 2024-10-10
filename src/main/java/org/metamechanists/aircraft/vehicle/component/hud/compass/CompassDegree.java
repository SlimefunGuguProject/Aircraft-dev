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


public class CompassDegree extends HudTextComponent<CompassDegree.CompassDegreeSchema> {
    public static class CompassDegreeSchema extends HudTextComponentSchema {
        private final TextColor color;
        private final float size;

        public CompassDegreeSchema(@NotNull String id, @NotNull HudSection.HudSectionSchema sectionSchema, @NotNull YamlTraverser traverser) {
            super(id + "_compass_degree", sectionSchema, traverser, CompassDegree.class, TextDisplay.class);
            color = traverser.getTextColor("degreeColor");
            size = traverser.get("degreeSize ");
        }
    }

    private final int index;

    public CompassDegree(@NotNull CompassDegree.CompassDegreeSchema schema, @NotNull VehicleEntity vehicleEntity, int index) {
        super(schema, vehicleEntity);
        this.index = index;
    }

    @SuppressWarnings("DataFlowIssue")
    public CompassDegree(@NotNull StateReader reader) {
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

        int degrees = (index + 60) * 3 - 90;
        if (degrees < 0) {
            degrees += 360;
        }

        setVisible(Math.abs(totalAdjustment.length()) < Horizon.RADIUS);

        return schema().getSectionSchema().rollText(vehicleEntity)
                .text(Component.text(degrees).color(schema().color))
                .translate(totalAdjustment)
                .scale(new Vector3f(schema().size, schema().size, 0.001F))
                .translate(0.5F, 0.0F, -0.01F);
    }
}

