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


public class CompassDirection extends HudTextComponent<CompassDirection.CompassDirectionSchema> {
    public static class CompassDirectionSchema extends HudTextComponentSchema {
        private final TextColor color;
        private final double size;
        private final String textN;
        private final String textE;
        private final String textS;
        private final String textW;

        public CompassDirectionSchema(@NotNull String id, @NotNull HudSection.HudSectionSchema sectionSchema, @NotNull YamlTraverser traverser) {
            super(id + "_compass_direction", sectionSchema, traverser, CompassDirection.class, TextDisplay.class);
            color = traverser.getTextColor("degreeColor");
            size = traverser.get("directionSize");
            textN = traverser.get("directionTextN");
            textE = traverser.get("directionTextE");
            textS = traverser.get("directionTextS");
            textW = traverser.get("directionTextW");
        }
    }

    private final int index;

    public CompassDirection(@NotNull CompassDirection.CompassDirectionSchema schema, @NotNull VehicleEntity vehicleEntity, int index) {
        super(schema, vehicleEntity);
        this.index = index;
    }

    @SuppressWarnings("DataFlowIssue")
    public CompassDirection(@NotNull StateReader reader) {
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

        String text = switch (index) {
            case -60, 60 -> schema().textW;
            case -30 -> schema().textN;
            case 0 -> schema().textE;
            case 30 -> schema().textS;
            default -> "ERROR";
        };

        setVisible(Math.abs(totalAdjustment.length()) < Horizon.RADIUS);

        return schema().getSectionSchema().rollText(vehicleEntity)
                .text(Component.text(text).color(schema().color))
                .translate(totalAdjustment)
                .scale(new Vector3f((float) schema().size, (float) schema().size, 0.001F))
                .translate(0.5F, 0.0F, -0.01F);
    }
}

