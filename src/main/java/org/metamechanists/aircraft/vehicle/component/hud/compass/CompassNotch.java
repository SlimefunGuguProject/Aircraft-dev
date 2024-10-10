package org.metamechanists.aircraft.vehicle.component.hud.compass;

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


public class CompassNotch extends HudTextComponent<CompassNotch.CompassNotchSchema> {
    public static class CompassNotchSchema extends HudTextComponentSchema {
        private final TextColor color;
        private final String text;

        public CompassNotchSchema(@NotNull String id, @NotNull HudSection.HudSectionSchema sectionSchema, @NotNull YamlTraverser traverser) {
            super(id + "_compass_notch", sectionSchema, traverser, CompassNotch.class, TextDisplay.class);
            color = traverser.getTextColor("notchColor");
            text = traverser.get("notchText");
        }
    }

    public CompassNotch(@NotNull CompassNotch.CompassNotchSchema schema, @NotNull VehicleEntity vehicleEntity) {
        super(schema, vehicleEntity);
    }

    public CompassNotch(@NotNull StateReader reader) {
        super(reader);
    }

    @Override
    protected @NotNull ModelText modelText(@NotNull VehicleEntity vehicleEntity) {
        return schema().getSectionSchema().rollIndependentText(vehicleEntity)
                .text(Component.text(schema().text).color(schema().color))
                .translate(0.0F, 0.05F, 0.05F)
                .scale(new Vector3f(0.15F, 0.15F, 0.001F))
                .translate(0.5F, 0.0F, 0.0F);
    }
}
