package org.metamechanists.aircraft.vehicles.hud;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedCuboid;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedText;
import org.metamechanists.displaymodellib.models.components.ModelComponent;

import java.util.Map;


public final class BottomPanel {
    private static final TextColor THROTTLE_VALUE_COLOR = TextColor.color(0, 255, 0);
    private static final float THROTTLE_SIZE = 0.2F;

    private BottomPanel() {}

    private static ModelAdvancedText getThrotteValue(VehicleState state, Vector3f hudCenter, Vector3f panelCenter) {
        return HudUtil.rollText(state, hudCenter)
                .translate(panelCenter)
                .translate(-0.15F, -0.2F, 0.0F)
                .scale(new Vector3f(0.1F, 0.1F, 0.001F));
    }

    private static ModelAdvancedCuboid getThrottleBackground(VehicleState state, Vector3f hudCenter, Vector3f panelCenter) {
        return HudUtil.rollCuboid(state, hudCenter)
                .material(Material.BLACK_CONCRETE)
                .translate(panelCenter)
                .translate(-0.05F, -0.2F , 0.0F)
                .scale(new Vector3f(THROTTLE_SIZE, 0.01F, 0.001F));
    }

    private static ModelAdvancedCuboid getThrottleForeground(@NotNull VehicleState state, Vector3f hudCenter, Vector3f panelCenter) {
        float fraction = (float) (state.throttle / 100.0);
        return HudUtil.rollCuboid(state, hudCenter)
                .material(Material.LIGHT_BLUE_CONCRETE)
                .translate(panelCenter)
                .translate(-0.05F + 0.5F * THROTTLE_SIZE * fraction, -0.2F, 0.001F)
                .scale(new Vector3f(THROTTLE_SIZE * fraction, 0.01F, 0.001F));
    }

    public static void build(VehicleState state, @NotNull Map<String, ModelComponent> hudComponents, Vector3f hudCenter) {
        Vector3f panelCenter = new Vector3f(0.0F, -0.3F, 0.0F);

        hudComponents.put("throttle_value", getThrotteValue(state, hudCenter, panelCenter));
        hudComponents.put("throttle_background", getThrottleBackground(state, hudCenter, panelCenter));
        hudComponents.put("throttle_foreground", getThrottleForeground(state, hudCenter, panelCenter));
    }

    public static void update(@NotNull VehicleState state, @NotNull Map<String, Display> displays) {
        TextDisplay throttleText = (TextDisplay) displays.get("throttle_value");
        throttleText.text(Component.text(state.throttle).color(THROTTLE_VALUE_COLOR));
    }
}
