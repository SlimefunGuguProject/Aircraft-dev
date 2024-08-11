package org.metamechanists.aircraft.vehicles.hud;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedText;
import org.metamechanists.displaymodellib.models.components.ModelComponent;

import java.util.Map;

import static java.lang.Math.PI;


public final class Horizon {
    private static final TextColor HORIZON_ALTITUDE_INDICATOR_COLOR = TextColor.color(0, 255, 0);
    private static final TextColor HORIZON_INDICATOR_COLOR = TextColor.color(100, 255, 100);
    private static final TextColor HORIZON_MAJOR_COLOR = TextColor.color(0, 255, 255);
    private static final TextColor HORIZON_MINOR_COLOR = TextColor.color(0, 180, 255);
    private static final TextColor HORIZON_DETAIL_COLOR = TextColor.color(0, 150, 180);
    private static final String HORIZON_INDICATOR_TEXT = "= = [     ] = =";
    private static final String HORIZON_MAJOR_TEXT = "--------";
    private static final String HORIZON_MINOR_TEXT = "-------";
    private static final String HORIZON_DETAIL_TEXT = "------";

    private Horizon() {}

    private static ModelAdvancedText getAltitudeIndicator(@NotNull VehicleState state, Vector3f hudCenter) {
        return new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(state.rotation)
                .translate(hudCenter)
                .rotateBackwards(state.rotation)
                .rotate(new Vector3d(0, Util.getYaw(state), Util.getPitch(state)))
                .facing(BlockFace.WEST)
                .scale(new Vector3f(0.15F, 0.15F, 0.001F))
                .translate(0.5F, 0.35F, 0.03F);
    }

    private static ModelAdvancedText getArtificialHorizonCenter(@NotNull VehicleState state, Vector3f hudCenter, Vector3f horizonOffset, boolean shouldRender) {
        return new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .text(Component.text(HORIZON_MAJOR_TEXT).color(HORIZON_MAJOR_COLOR))
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(state.rotation)
                .translate(horizonOffset)
                .translate(hudCenter)
                .facing(BlockFace.WEST)
                .scale(shouldRender ? new Vector3f(0.15F, 0.15F, 0.001F) : new Vector3f())
                .translate(0.5F, 0.35F, 0);
    }

    private static ModelAdvancedText getArtificialHorizonBar(
            @NotNull VehicleState state, Vector3f hudCenter, Vector3f horizonOffset, Component component, Vector3f barOffset, boolean shouldRender) {
        return new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .text(component)
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(state.rotation)
                .translate(horizonOffset)
                .translate(hudCenter)
                .translate(barOffset)
                .facing(BlockFace.WEST)
                .scale(shouldRender ? new Vector3f(0.1F, 0.1F, 0.001F) : new Vector3f())
                .translate(0.5F, 0.35F, 0);
    }

    private static ModelAdvancedText getHorizonIndicator(@NotNull VehicleState state, Vector3f hudCenter) {
        return Util.rollIndependentComponent(state, hudCenter)
                .text(Component.text(HORIZON_INDICATOR_TEXT).color(HORIZON_INDICATOR_COLOR))
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .scale(new Vector3f(0.15F, 0.15F, 0.001F))
                .translate(0.5F, 0.35F, 0.03F);
    }

    private static ModelAdvancedText getArtificialHorizonDegree(
            @NotNull VehicleState state, Vector3f hudCenter, Component component, Vector3f totalAdjustment, boolean shouldRender) {
        return new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .text(component)
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(state.rotation)
                .translate(totalAdjustment)
                .translate(hudCenter)
                .translate(new Vector3f(0, 0, 0.19F))
                .facing(BlockFace.WEST)
                .scale(shouldRender ? new Vector3f(0.1F, 0.1F, 0.001F) : new Vector3f())
                .translate(0.5F, 0.35F, 0);
    }

    public static void update(VehicleState state, @NotNull Map<String, ModelComponent> hudComponents, Vector3f hudCenter, Location location) {
        Vector3f horizonOffset = new Vector3f(0, (float) (-Util.getPitch(state)), 0);
        final float horizonRadius = 0.2F;
        boolean shouldRenderCenter = Math.abs(horizonOffset.y) < horizonRadius;

        hudComponents.put("altitude", getAltitudeIndicator(state, hudCenter));
        hudComponents.put("horizon", getHorizonIndicator(state, hudCenter));
        hudComponents.put("horizon_center", getArtificialHorizonCenter(state, hudCenter, horizonOffset, shouldRenderCenter));

        final int bars = 30;
        final float verticalSpacing = 0.5F * (float) ((PI / 1.14) / (bars));
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

        TextDisplay altitudeText = (TextDisplay) hudComponents.get("altitude");
        altitudeText.text(Component.text(location.getBlockY()).color(HORIZON_ALTITUDE_INDICATOR_COLOR));
    }
}
