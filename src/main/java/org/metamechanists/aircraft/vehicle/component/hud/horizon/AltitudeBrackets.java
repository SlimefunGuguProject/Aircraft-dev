package org.metamechanists.aircraft.vehicle.component.hud.horizon;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.aircraft.vehicle.component.base.HudSection;
import org.metamechanists.aircraft.vehicle.component.base.HudTextComponent;
import org.metamechanists.displaymodellib.models.components.ModelText;
import org.metamechanists.kinematiccore.api.storage.StateReader;
import org.metamechanists.metalib.yaml.YamlTraverser;


public class AltitudeBrackets extends HudTextComponent<AltitudeBrackets.AltitudeBracketsSchema> {
    public static class AltitudeBracketsSchema extends HudTextComponentSchema {
        private final TextColor color;
        private final String text;

        public AltitudeBracketsSchema(@NotNull HudSection.HudSectionSchema sectionSchema, @NotNull YamlTraverser traverser) {
            super("altitude_brackets", sectionSchema, traverser, AltitudeBrackets.class, TextDisplay.class);
            color = traverser.getTextColor("altitudeBracketsColor");
            text = traverser.get("altitudeBracketsText");
        }
    }

    public AltitudeBrackets(@NotNull AltitudeBrackets.AltitudeBracketsSchema schema, @NotNull VehicleEntity vehicleEntity) {
        super(schema, vehicleEntity);
    }

    public AltitudeBrackets(@NotNull StateReader reader) {
        super(reader);
    }

    @Override
    protected @NotNull ModelText modelText(@NotNull VehicleEntity vehicleEntity) {
        Aircraft.getInstance().getLogger().severe(schema().color.asHexString());
        return schema().getSectionSchema().rollIndependentText(vehicleEntity)
                .text(Component.text(schema().text).color(schema().color))
                .scale(new Vector3f(20.15F, 5.15F, 5.001F))
                .translate(0.5F, 0.35F, 0.05F);
    }
}
