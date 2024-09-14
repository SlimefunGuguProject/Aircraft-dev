package org.metamechanists.aircraft.vehicle.component.hud.horizon;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.aircraft.vehicle.component.base.HudTextComponent;
import org.metamechanists.displaymodellib.models.components.ModelText;
import org.metamechanists.kinematiccore.api.storage.StateReader;
import org.metamechanists.metalib.yaml.YamlTraverser;


public class VelocityIndicator extends HudTextComponent<VelocityIndicator.VelocityIndicatorSchema> {
    public static class VelocityIndicatorSchema extends HudTextComponentSchema {
        private final TextColor color;
        private final String text;

        public VelocityIndicatorSchema(@NotNull Horizon.HorizonSchema sectionSchema, @NotNull YamlTraverser traverser) {
            super("velocity_indicator", sectionSchema, traverser, VelocityIndicator.class, TextDisplay.class);
            color = traverser.getTextColor("velocityIndicatorColor");
            text = traverser.get("velocityIndicatorText");
        }
    }

    protected VelocityIndicator(@NotNull VelocityIndicator.VelocityIndicatorSchema schema, @NotNull VehicleEntity vehicleEntity) {
        super(schema, vehicleEntity);
    }

    protected VelocityIndicator(@NotNull StateReader reader) {
        super(reader);
    }

    @Override
    protected @NotNull ModelText modelText(@NotNull VehicleEntity vehicleEntity) {
        float horizonOffset = (float) (0.5 * vehicleEntity.getVelocityPitch()) + Horizon.HorizonSchema.offset(vehicleEntity);
        return schema().getSectionSchema().rollIndependentText(vehicleEntity)
                .text(Component.text(schema().text).color(schema().color))
                .translate(new Vector3f(0, horizonOffset, 0))
                .scale(new Vector3f(0.15F, 0.15F, 0.001F))
                .translate(0.5F, 0.35F, 0.05F);
    }
}
