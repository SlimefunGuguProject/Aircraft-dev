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
import org.metamechanists.kinematiccore.api.storage.StateWriter;
import org.metamechanists.metalib.yaml.YamlTraverser;


public class HorizonBar extends HudTextComponent<HorizonBar.HorizonBarSchema> {
    public static class HorizonBarSchema extends HudTextComponentSchema {
        private final int bars;
        private final TextColor majorColor;
        private final TextColor minorColor;
        private final TextColor detailColor;
        private final String majorText;
        private final String minorText;
        private final String detailText;

        @SuppressWarnings("DataFlowIssue")
        public HorizonBarSchema(@NotNull HudSection.HudSectionSchema sectionSchema, @NotNull YamlTraverser traverser) {
            super("altitude_brackets", sectionSchema, traverser, HorizonBar.class, TextDisplay.class);
            bars = traverser.get("bars");
            majorColor = traverser.getTextColor("altitudeBracketsColor");
            minorColor = traverser.getTextColor("minorColor");
            detailColor = traverser.getTextColor("detailColor");
            majorText = traverser.get("altitudeBracketsText");
            minorText = traverser.get("minorText");
            detailText = traverser.get("detailText");
        }
    }

    private final int index;

    public HorizonBar(@NotNull HorizonBar.HorizonBarSchema schema, @NotNull VehicleEntity vehicleEntity, int index) {
        super(schema, vehicleEntity);
        this.index = index;
    }

    @SuppressWarnings("DataFlowIssue")
    public HorizonBar(@NotNull StateReader reader) {
        super(reader);
        index = reader.get("index", int.class);
    }

    @Override
    public void write(@NotNull StateWriter writer) {
        writer.set("index", index);
    }

    @Override
    protected @NotNull ModelText modelText(@NotNull VehicleEntity vehicleEntity) {
        return schema().getSectionSchema().rollIndependentText(vehicleEntity)
                .text(Component.text(schema().majorText).color(schema().majorColor))
                .scale(new Vector3f(0.15F, 0.15F, 0.001F))
                .translate(0.5F, 0.35F, 0.05F);
    }
}
