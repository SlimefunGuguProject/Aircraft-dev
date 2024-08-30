package org.metamechanists.aircraft.vehicles.hud;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelComponent;
import org.metamechanists.displaymodellib.models.components.ModelItem;
import org.metamechanists.displaymodellib.models.components.ModelText;

import java.util.Map;


public final class BottomPanel {
    private static final TextColor THROTTLE_VALUE_COLOR = TextColor.color(0, 255, 0);
    private static final float THROTTLE_SIZE = 0.2F;

    private BottomPanel() {}

    private static ModelText getThrotteValue(Vector3f center) {
        return HudUtil.rollText(center)
                .translate(-0.09F, 0.034F, 0.0F)
                .scale(new Vector3f(0.1F, 0.1F, 0.001F));
    }

    private static ModelItem getThrottleBackground(Vector3f center) {
        return HudUtil.rollCuboid(center)
                .material(Material.BLACK_TERRACOTTA)
                .translate(0.0F, 0.0F , 0.0F)
                .scale(new Vector3f(THROTTLE_SIZE, 0.01F, 0.001F));
    }

    private static ModelItem getThrottleForeground(@NotNull VehicleState state, Vector3f center) {
        float fraction = (float) (state.throttle / 100.0);
        return HudUtil.rollCuboid(center)
                .material(Material.LIGHT_BLUE_CONCRETE)
                .translate(-0.1F + 0.5F * THROTTLE_SIZE * fraction, 0.0F, 0.001F)
                .scale(new Vector3f(THROTTLE_SIZE * fraction, 0.01F, 0.001F));
    }

    public static void build(VehicleState state, @NotNull Map<String, ModelComponent> hudComponents, Vector3f hudCenter) {
        Vector3f center = new Vector3f(0.0F, -0.36F, 0.0F).add(hudCenter);

        hudComponents.put("bottom-panel.throttle.value", getThrotteValue(center));
        hudComponents.put("bottom-panel.throttle.background", getThrottleBackground(center));
        hudComponents.put("bottom-panel.throttle.foreground", getThrottleForeground(state, center));
    }

    public static void update(@NotNull VehicleState state, @NotNull Map<String, Display> displays) {
        TextDisplay throttleText = (TextDisplay) displays.get("bottom-panel.throttle.value");
        throttleText.text(Component.text(state.throttle).color(THROTTLE_VALUE_COLOR));
    }
}
