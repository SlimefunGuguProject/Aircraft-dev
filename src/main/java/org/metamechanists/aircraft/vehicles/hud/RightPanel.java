package org.metamechanists.aircraft.vehicles.hud;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedText;
import org.metamechanists.displaymodellib.models.components.ModelComponent;

import java.util.Map;


public final class RightPanel {
    private static final TextColor KEY_COLOR = TextColor.color(170, 255, 170);
    private static final TextColor VALUE_COLOR = TextColor.color(0, 255, 0);

    private RightPanel() {}

    public static ModelAdvancedText build(VehicleState state, @NotNull Map<String, ModelComponent> hudComponents, Vector3f hudCenter) {
        return Util.rollIndependentText(state, hudCenter)
                .text(Component.text("VEL").color(KEY_COLOR))
                .scale(new Vector3f(0.1F, 0.1F, 0.001F))
                .translate(0.5F, -1.57F, -0.01F);
    }
}
