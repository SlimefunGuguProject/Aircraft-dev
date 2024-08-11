package org.metamechanists.aircraft.vehicles.hud;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.TextDisplay.TextAlignment;
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

    private static ModelAdvancedText getVelocityKey(VehicleState state, Vector3f hudCenter, Vector3f panelCenter) {
        return Util.rollIndependentText(state, hudCenter)
                .alignment(TextAlignment.LEFT)
                .text(Component.text("VEL").color(KEY_COLOR))
                .translate(panelCenter)
                .translate(0.0F, 0.2F, 0.0F)
                .scale(new Vector3f(0.15F, 0.15F, 0.001F));
    }

    private static ModelAdvancedText getVelocityValue(VehicleState state, Vector3f hudCenter, Vector3f panelCenter) {
        return Util.rollIndependentText(state, hudCenter)
                .alignment(TextAlignment.LEFT)
                .translate(panelCenter)
                .translate(0.15F, 0.15F, 0.0F)
                .scale(new Vector3f(0.2F, 0.2F, 0.001F));
    }

    public static void build(VehicleState state, @NotNull Map<String, ModelComponent> hudComponents, Vector3f hudCenter) {
        Vector3f panelCenter = new Vector3f(0.4F, 0.0F, 0.0F);

        hudComponents.put("velocity_key", getVelocityKey(state, hudCenter, panelCenter));
        hudComponents.put("velocity_value", getVelocityValue(state, hudCenter, panelCenter));
    }

    public static void update(@NotNull VehicleState state, @NotNull Map<String, Display> displays) {
        TextDisplay velocityText = (TextDisplay) displays.get("velocity_value");
        velocityText.text(Component.text(Math.round(state.velocity.length() * 10.0) / 10.0).color(VALUE_COLOR));
    }
}
