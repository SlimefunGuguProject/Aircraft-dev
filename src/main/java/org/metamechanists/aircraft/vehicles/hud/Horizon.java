package org.metamechanists.aircraft.vehicles.hud;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedText;
import org.metamechanists.displaymodellib.models.components.ModelComponent;

import java.util.Map;

import static java.lang.Math.PI;


public final class Horizon {
    private static final TextColor HORIZON_ALTITUDE_COLOR = TextColor.color(200, 255, 255);
    private static final TextColor HORIZON_INDICATOR_COLOR = TextColor.color(0, 0, 255);
    private static final TextColor HORIZON_VELOCITY_COLOR = TextColor.color(130, 0, 255);
    private static final TextColor HORIZON_MAJOR_COLOR = TextColor.color(0, 255, 255);
    private static final TextColor HORIZON_MINOR_COLOR = TextColor.color(0, 180, 255);
    private static final TextColor HORIZON_DETAIL_COLOR = TextColor.color(0, 150, 180);
    private static final String HORIZON_ROTATION_TEXT = "[     ]";
    private static final String HORIZON_VELOCITY_TEXT = "<--         -->";
    private static final String HORIZON_MAJOR_TEXT = "--------";
    private static final String HORIZON_MINOR_TEXT = "-------";
    private static final String HORIZON_DETAIL_TEXT = "------";

    private Horizon() {}

    private static ModelAdvancedText getAltitudeIndicator(@NotNull VehicleState state, Vector3f hudCenter) {
        return Util.rollIndependentText(state, hudCenter)
                .scale(new Vector3f(0.15F, 0.15F, 0.001F))
                .translate(0.5F, 0.35F, 0.05F);
    }

    private static ModelAdvancedText getArtificialHorizonMajor(@NotNull VehicleState state, Vector3f hudCenter, Vector3f horizonOffset, boolean shouldRender) {
        return Util.rollText(state, hudCenter, horizonOffset)
                .text(Component.text(HORIZON_MAJOR_TEXT).color(HORIZON_MAJOR_COLOR))
                .scale(shouldRender ? new Vector3f(0.15F, 0.15F, 0.001F) : new Vector3f())
                .translate(0.5F, 0.35F, 0);
    }

    private static ModelAdvancedText getArtificialHorizonBar(
            @NotNull VehicleState state, Vector3f hudCenter, Vector3f horizonOffset, Component component, Vector3f barOffset, boolean shouldRender) {
        return Util.rollText(state, hudCenter, new Vector3f(horizonOffset).add(barOffset))
                .text(component)
                .scale(shouldRender ? new Vector3f(0.1F, 0.1F, 0.001F) : new Vector3f())
                .translate(0.5F, 0.35F, 0);
    }

    private static ModelAdvancedText getRotationIndicator(@NotNull VehicleState state, Vector3f hudCenter) {
        return Util.rollIndependentText(state, hudCenter)
                .text(Component.text(HORIZON_ROTATION_TEXT).color(HORIZON_INDICATOR_COLOR))
                .scale(new Vector3f(0.15F, 0.15F, 0.001F))
                .translate(0.5F, 0.35F, 0.05F);
    }

    private static ModelAdvancedText getVelocityIndicator(@NotNull VehicleState state, Vector3f hudCenter, Vector3f velocityOffset) {
        return Util.rollIndependentText(state, hudCenter)
                .text(Component.text(HORIZON_VELOCITY_TEXT).color(HORIZON_VELOCITY_COLOR))
                .scale(new Vector3f(0.15F, 0.15F, 0.001F))
                .translate(velocityOffset)
                .translate(0.5F, 0.35F, 0.05F);
    }

    private static ModelAdvancedText getArtificialHorizonDegree(
            @NotNull VehicleState state, Vector3f hudCenter, Component component, Vector3f totalAdjustment, boolean shouldRender) {
        return Util.rollText(state, hudCenter, new Vector3f(totalAdjustment).add(new Vector3f(0, 0, 0.08F)))
                .text(component)
                .scale(shouldRender ? new Vector3f(0.1F, 0.1F, 0.001F) : new Vector3f())
                .translate(0.5F, 0.35F, 0);
    }

    public static void build(VehicleState state, @NotNull Map<String, ModelComponent> hudComponents, Vector3f hudCenter) {
        Vector3f horizonOffset = new Vector3f(0, (float) (0.5 * -Util.getPitch(state)), 0);
        final float horizonRadius = 0.15F;
        boolean shouldRenderCenter = Math.abs(horizonOffset.y) < horizonRadius;

        hudComponents.put("altitude", getAltitudeIndicator(state, hudCenter));
        hudComponents.put("horizon", getRotationIndicator(state, hudCenter));
        hudComponents.put("horizon_center", getArtificialHorizonMajor(state, hudCenter, horizonOffset, shouldRenderCenter));

        Vector3f velocityOffset = new Vector3f(0, (float) (0.5 * -Util.getPitch(state)), 0);
        hudComponents.put("velocity", getVelocityIndicator(state, hudCenter, velocityOffset));

        final int bars = 30;
        final float verticalSpacing = 0.25F * (float) ((PI / 1.14) / (bars));
        for (int i = -bars; i <= bars; i++) {
            if (i == 0) {
                continue;
            }

            Vector3f barOffset = new Vector3f(0, verticalSpacing * i, 0);
            Vector3f totalAdjustment = new Vector3f(barOffset).add(horizonOffset);
            boolean longBar = i % 5 == 0;
            String text = longBar ? HORIZON_MINOR_TEXT : HORIZON_DETAIL_TEXT;
            TextColor color = longBar ? HORIZON_MINOR_COLOR : HORIZON_DETAIL_COLOR;
            Component component = Component.text(text).color(color);
            boolean shouldRender = Math.abs(totalAdjustment.length()) < horizonRadius;

            hudComponents.put("horizon_bar_" + i, getArtificialHorizonBar(state, hudCenter, horizonOffset, component, barOffset, shouldRender));

            if (longBar) {
                Component degreeComponent = Component.text(i * (90 / (bars-1))).color(HORIZON_MINOR_COLOR);
                hudComponents.put("horizon_degree_" + i, getArtificialHorizonDegree(state, hudCenter, degreeComponent, totalAdjustment, shouldRender));
            }
        }
    }

    public static void update(@NotNull Map<String, Display> displays, @NotNull Location location) {
        TextDisplay altitudeText = (TextDisplay) displays.get("altitude");
        altitudeText.text(Component.text(location.getBlockY()).color(HORIZON_ALTITUDE_COLOR));
    }
}
