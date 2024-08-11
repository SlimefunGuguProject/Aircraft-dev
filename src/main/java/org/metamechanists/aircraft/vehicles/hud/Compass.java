package org.metamechanists.aircraft.vehicles.hud;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelComponent;

import java.util.Map;

import static java.lang.Math.PI;


public final class Compass {
    private static final TextColor COMPASS_MAJOR_COLOR = TextColor.color(0, 255, 255);
    private static final TextColor COMPASS_MINOR_COLOR = TextColor.color(0, 180, 255);
    private static final TextColor COMPASS_DETAIL_COLOR = TextColor.color(0, 150, 180);
    private static final TextColor COMPASS_DIRECTION_COLOR = TextColor.color(180, 255, 255);
    private static final TextColor COMPASS_NOTCH_COLOR = TextColor.color(0, 0, 255);

    private Compass() {}

    private static ModelComponent getCompassBar(@NotNull VehicleState state, @NotNull Vector3f hudCenter, @NotNull Vector3f totalAdjustment, float compassRadius, int i) {
        boolean shouldRender = Math.abs(totalAdjustment.x) < compassRadius;
        TextColor color;
        float size;
        if (i % 30 == 0) {
            color = COMPASS_MAJOR_COLOR;
            size = 0.2F;
        } else if (i % 10 == 0) {
            color = COMPASS_MINOR_COLOR;
            size = 0.15F;
        } else {
            color = COMPASS_DETAIL_COLOR;
            size = 0.1F;
        }

        return Util.rollIndependentText(state, hudCenter)
                .text(Component.text("|").color(color))
                .translate(totalAdjustment)
                .scale(shouldRender ? new Vector3f(size, size, 0.001F) : new Vector3f())
                .translate(0.5F, 0.35F, -0.01F);
    }

    private static ModelComponent getCompassDirection(
            @NotNull VehicleState state, @NotNull Vector3f hudCenter, @NotNull Vector3f totalAdjustment, float compassRadius, String text) {
        boolean shouldRender = Math.abs(totalAdjustment.x) < compassRadius;
        return Util.rollIndependentText(state, hudCenter)
                .text(Component.text(text).color(COMPASS_DIRECTION_COLOR))
                .translate(totalAdjustment)
                .scale(shouldRender ? new Vector3f(0.15F, 0.15F, 0.001F) : new Vector3f())
                .translate(0.5F, -0.15F, -0.01F);
    }

    private static ModelComponent getCompassDegree(
            @NotNull VehicleState state, @NotNull Vector3f hudCenter, @NotNull Vector3f totalAdjustment, float compassRadius, int degrees) {
        boolean shouldRender = Math.abs(totalAdjustment.x) < compassRadius;
        return Util.rollIndependentText(state, hudCenter)
                .text(Component.text(degrees).color(COMPASS_MAJOR_COLOR))
                .translate(totalAdjustment)
                .scale(shouldRender ? new Vector3f(0.1F, 0.1F, 0.001F) : new Vector3f())
                .translate(0.5F, -0.04F, -0.01F);
    }

    private static ModelComponent getCompassNotch(@NotNull VehicleState state, @NotNull Vector3f hudCenter) {
        return Util.rollIndependentText(state, hudCenter)
                .text(Component.text("▼").color(COMPASS_NOTCH_COLOR))
                .scale(new Vector3f(0.1F, 0.1F, 0.001F))
                .translate(0.5F, -1.57F, -0.01F);
    }

    public static void build(@NotNull VehicleState state, @NotNull Map<String, ModelComponent> hudComponents, @NotNull Vector3f hudCenter) {
        hudComponents.put("compass_notch", getCompassNotch(state, hudCenter));

        final int bars = 60;
        final int extraBars = 8;
        Vector3f compassOffset = new Vector3f((float) (0.5 * Util.getYaw(state)), -0.27F, 0);
        final float compassRadius = 0.2F;
        final float horizontalSpacing = 0.5F * (float) (PI / (bars));

        for (int i = -bars-extraBars; i <= bars+extraBars; i++) {
            Vector3f barOffset = new Vector3f(horizontalSpacing * i, 0, 0);
            Vector3f totalAdjustment = new Vector3f(barOffset).add(compassOffset);
            hudComponents.put("compass_bar_" + i, getCompassBar(state, hudCenter, totalAdjustment, compassRadius, i));

            if ((i+420) % 30 == 0) {
                String text = switch (i) {
                    case -60, 60 -> "W";
                    case -30 -> "N";
                    case 0 -> "E";
                    case 30 -> "S";
                    default -> "ERROR";
                };
                hudComponents.put("compass_direction_" + i, getCompassDirection(state, hudCenter, totalAdjustment, compassRadius, text));
            }

            if ((i+420) % 10 == 0) {
                int degrees = (i + 60) * 3 - 90;
                if (degrees < 0) {
                    degrees += 360;
                }
                hudComponents.put("compass_degree_" + i, getCompassDegree(state, hudCenter, totalAdjustment, compassRadius, degrees));
            }
        }
    }
}
