package org.metamechanists.aircraft.vehicle.component.hud.horizon;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.aircraft.vehicle.component.base.HudSection;
import org.metamechanists.aircraft.vehicle.component.base.HudTextComponent;
import org.metamechanists.displaymodellib.models.components.ModelText;
import org.metamechanists.kinematiccore.api.storage.StateReader;
import org.metamechanists.metalib.yaml.YamlTraverser;


public class AltitudeText extends HudTextComponent<AltitudeText.AltitudeTextSchema> {
    public static class AltitudeTextSchema extends HudTextComponent.HudTextComponentSchema {
        private final TextColor color;

        public AltitudeTextSchema(@NotNull HudSection.HudSectionSchema sectionSchema, @NotNull YamlTraverser traverser) {
            super("altitude_text", sectionSchema, traverser, AltitudeText.class, TextDisplay.class);
            color = traverser.getTextColor("altitudeTextColor");
        }
    }

    public AltitudeText(@NotNull AltitudeTextSchema schema, @NotNull VehicleEntity vehicleEntity) {
        super(schema, vehicleEntity);
    }

    public AltitudeText(@NotNull StateReader reader) {
        super(reader);
    }

    @Override
    protected @NotNull ModelText modelText(@NotNull VehicleEntity vehicleEntity) {
        return schema().getSectionSchema().rollIndependentText(vehicleEntity)
                .text(Component.text(vehicleEntity.altitude()).color(schema().color))
                .scale(new Vector3f(0.15F, 0.15F, 0.001F))
                .translate(0.5F, 0.35F, 0.05F);
    }
}
