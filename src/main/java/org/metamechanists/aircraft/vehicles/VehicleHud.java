package org.metamechanists.aircraft.vehicles;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedText;
import org.metamechanists.displaymodellib.models.components.ModelComponent;
import org.metamechanists.displaymodellib.sefilib.entity.display.DisplayGroup;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.PI;


public final class VehicleHud {
    private VehicleHud() {}

    private static final TextColor HORIZON_ALTITUDE_INDICATOR_COLOR = TextColor.color(0, 255, 0);
    private static final TextColor HORIZON_INDICATOR_COLOR = TextColor.color(100, 255, 100);
    private static final TextColor HORIZON_MAJOR_COLOR = TextColor.color(0, 255, 255);
    private static final TextColor HORIZON_MINOR_COLOR = TextColor.color(0, 180, 255);
    private static final TextColor HORIZON_DETAIL_COLOR = TextColor.color(0, 150, 180);
    private static final String HORIZON_INDICATOR_TEXT = "= = [     ] = =";
    private static final String HORIZON_MAJOR_TEXT = "----------";
    private static final String HORIZON_MINOR_TEXT = "--------";
    private static final String HORIZON_DETAIL_TEXT = "--------";

    private static final TextColor COMPASS_MAJOR_COLOR = TextColor.color(0, 255, 255);
    private static final TextColor COMPASS_MINOR_COLOR = TextColor.color(0, 180, 255);
    private static final TextColor COMPASS_DETAIL_COLOR = TextColor.color(0, 150, 180);
    private static final TextColor COMPASS_DIRECTION_COLOR = TextColor.color(180, 255, 255);
    private static final TextColor COMPASS_NOTCH_COLOR = TextColor.color(100, 255, 100);

    private static double getPitch(@NotNull Vector3d rotation) {
        Vector3d lookingAtForward = Utils.rotateByEulerAngles(new Vector3d(1, 0, 0), rotation);
        double pitch = lookingAtForward.angle(new Vector3d(lookingAtForward.x, 0, lookingAtForward.z));
        return lookingAtForward.y > 0 ? pitch : -pitch;
    }
    private static double getYaw(@NotNull Vector3d rotation) {
        Vector3d lookingAtForward = Utils.rotateByEulerAngles(new Vector3d(1, 0, 0), rotation);
        double yaw = new Vector3d(lookingAtForward.x, 0, lookingAtForward.z).angle(new Vector3d(1, 0, 0));
        return lookingAtForward.z > 0 ? -yaw : yaw;
    }
    private static ModelAdvancedText rollIndependentComponent(@NotNull Vector3f hudCenter, @NotNull Vector3d rotation) {
        return new ModelAdvancedText()
                .rotate(rotation)
                .translate(hudCenter)
                .rotateBackwards(rotation)
                .rotate(new Vector3d(0, getYaw(rotation), getPitch(rotation)))
                .facing(BlockFace.WEST);
    }

    private static ModelAdvancedText getAltitudeIndicator(@NotNull Vector3f hudCenter, @NotNull Vector3d rotation) {
        return new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(rotation)
                .translate(hudCenter)
                .rotateBackwards(rotation)
                .rotate(new Vector3d(0, getYaw(rotation), getPitch(rotation)))
                .facing(BlockFace.WEST)
                .scale(new Vector3f(0.2F, 0.2F, 0.001F))
                .translate(0.25F, 0.175F, 0.01F);
    }
    private static ModelAdvancedText getHorizonIndicator(@NotNull Vector3f hudCenter, @NotNull Vector3d rotation) {
        return rollIndependentComponent(hudCenter, rotation)
                .text(Component.text(HORIZON_INDICATOR_TEXT).color(HORIZON_INDICATOR_COLOR))
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .scale(new Vector3f(0.2F, 0.2F, 0.001F))
                .translate(0.25F, 0.175F, 0.01F);
    }
    private static ModelAdvancedText getArtificialHorizonCenter(
            @NotNull Vector3f hudCenter, @NotNull Vector3d rotation, Vector3f horizonOffset, boolean shouldRender) {
        return new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .text(Component.text(HORIZON_MAJOR_TEXT).color(HORIZON_MAJOR_COLOR))
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(rotation)
                .translate(horizonOffset)
                .translate(hudCenter)
                .facing(BlockFace.WEST)
                .scale(shouldRender ? new Vector3f(0.15F, 0.15F, 0.001F) : new Vector3f())
                .translate(0.25F, 0.155F, 0);
    }
    private static ModelAdvancedText getArtificialHorizonBar(
            Component component,
            @NotNull Vector3f hudCenter, @NotNull Vector3d rotation, Vector3f horizonOffset,
            Vector3f barOffset, boolean shouldRender) {
        return new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .text(component)
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(rotation)
                .translate(horizonOffset)
                .translate(hudCenter)
                .translate(barOffset)
                .facing(BlockFace.WEST)
                .scale(shouldRender ? new Vector3f(0.1F, 0.1F, 0.001F) : new Vector3f())
                .translate(0.25F, 0.175F, 0);
    }
    private static ModelAdvancedText getArtificialHorizonDegree(
            Component component,
            @NotNull Vector3f hudCenter, @NotNull Vector3d rotation, Vector3f totalAdjustment, boolean shouldRender) {
        return new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .text(component)
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(rotation)
                .translate(totalAdjustment)
                .translate(hudCenter)
                .translate(new Vector3f(0, 0, 0.19F))
                .facing(BlockFace.WEST)
                .scale(shouldRender ? new Vector3f(0.1F, 0.1F, 0.001F) : new Vector3f())
                .translate(0.25F, 0.175F, 0);
    }

    private static void addArtificialHorizon(@NotNull Map<String, ModelComponent> hudComponents, @NotNull Vector3f hudCenter, @NotNull Vector3d rotation) {
        hudComponents.put("altitude", getAltitudeIndicator(hudCenter, rotation));
        hudComponents.put("horizon", getHorizonIndicator(hudCenter, rotation));

        Vector3f horizonOffset = new Vector3f(0, (float) (2 * -getPitch(rotation)), 0);
        final float horizonRadius = 0.2F;
        boolean shouldRenderCenter = Math.abs(horizonOffset.y) < horizonRadius;

        hudComponents.put("horizon_center", getArtificialHorizonCenter(hudCenter, rotation, horizonOffset, shouldRenderCenter));

        final int bars = 30;
        final float verticalSpacing = (float) ((PI / 1.14) / (bars));
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

            hudComponents.put("horizon_bar_" + i, getArtificialHorizonBar(component, hudCenter, rotation, horizonOffset, barOffset, shouldRender));

            if (longBar) {
                Component degreeComponent = Component.text(i * (90 / (bars-1))).color(HORIZON_MINOR_COLOR);
                hudComponents.put("horizon_degree_" + i, getArtificialHorizonDegree(degreeComponent, hudCenter, rotation, totalAdjustment, shouldRender));
            }
        }
    }
    private static ModelComponent getCompassBar(
            @NotNull Vector3f hudCenter,
            @NotNull Vector3d rotation, @NotNull Vector3f totalAdjustment, float compassRadius, int i) {
        boolean shouldRender = Math.abs(totalAdjustment.x) < compassRadius;
        TextColor color;
        float size;
        if (i % 30 == 0) {
            color = COMPASS_MAJOR_COLOR;
            size = 0.6F;
        } else if (i % 10 == 0) {
            color = COMPASS_MINOR_COLOR;
            size = 0.3F;
        } else {
            color = COMPASS_DETAIL_COLOR;
            size = 0.2F;
        }

        return rollIndependentComponent(hudCenter, rotation)
                .text(Component.text("|").color(color))
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .translate(totalAdjustment)
                .scale(shouldRender ? new Vector3f(size, size, 0.001F) : new Vector3f())
                .translate(0.5F, 0.35F, -0.01F);
    }
    private static ModelComponent getCompassDirection(
            @NotNull Vector3f hudCenter,
            @NotNull Vector3d rotation, @NotNull Vector3f totalAdjustment, float compassRadius, String text) {
        boolean shouldRender = Math.abs(totalAdjustment.x) < compassRadius;
        return rollIndependentComponent(hudCenter, rotation)
                .text(Component.text(text).color(COMPASS_DIRECTION_COLOR))
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .translate(totalAdjustment)
                .scale(shouldRender ? new Vector3f(0.3F, 0.3F, 0.001F) : new Vector3f())
                .translate(0.5F, -0.15F, -0.01F);
    }
    private static ModelComponent getCompassDegree(
            @NotNull Vector3f hudCenter,
            @NotNull Vector3d rotation, @NotNull Vector3f totalAdjustment, float compassRadius, int degrees) {
        boolean shouldRender = Math.abs(totalAdjustment.x) < compassRadius;
        return rollIndependentComponent(hudCenter, rotation)
                .text(Component.text(degrees).color(COMPASS_MAJOR_COLOR))
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .translate(totalAdjustment)
                .scale(shouldRender ? new Vector3f(0.2F, 0.2F, 0.001F) : new Vector3f())
                .translate(0.5F, -0.04F, -0.01F);
    }
    private static ModelComponent getCompassNotch(@NotNull Vector3f hudCenter, @NotNull Vector3d rotation) {
        return rollIndependentComponent(hudCenter, rotation)
                .text(Component.text("▼").color(COMPASS_NOTCH_COLOR))
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .scale(new Vector3f(0.2F, 0.2F, 0.001F))
                .translate(0.5F, -2.2F, -0.01F);
    }

    private static void addCompass(@NotNull Map<String, ModelComponent> hudComponents, @NotNull Vector3f hudCenter, @NotNull Vector3d rotation) {
        hudComponents.put("compass_notch", getCompassNotch(hudCenter, rotation));

        final int bars = 60;
        final int extraBars = 8;
        Vector3f compassOffset = new Vector3f((float) (getYaw(rotation)), -0.6F, 0);
        final float compassRadius = 0.4F;
        final float horizontalSpacing = (float) (PI / (bars));

        for (int i = -bars-extraBars; i <= bars+extraBars; i++) {
            Vector3f barOffset = new Vector3f(horizontalSpacing * i, 0, 0);
            Vector3f totalAdjustment = new Vector3f(barOffset).add(compassOffset);
            hudComponents.put("compass_bar_" + i, getCompassBar(hudCenter, rotation, totalAdjustment, compassRadius, i));

            if ((i+420) % 30 == 0) {
                String text = switch (i) {
                    case -60, 60 -> "W";
                    case -30 -> "N";
                    case 0 -> "E";
                    case 30 -> "S";
                    default -> "ERROR";
                };
                hudComponents.put("compass_direction_" + i, getCompassDirection(hudCenter, rotation, totalAdjustment, compassRadius, text));
            }

            if ((i+420) % 10 == 0) {
                int degrees = (i + 60) * 3 - 90;
                if (degrees < 0) {
                    degrees += 360;
                }
                hudComponents.put("compass_degree_" + i, getCompassDegree(hudCenter, rotation, totalAdjustment, compassRadius, degrees));
            }
        }
    }

    public static @NotNull Map<String, ModelComponent> getHud(@NotNull Vector3d rotation) {
        Map<String, ModelComponent> hudComponents = new HashMap<>();
        Vector3f hudCenter = new Vector3f(0.25F, -0.1F, 0.0F);

        addArtificialHorizon(hudComponents, hudCenter, rotation);
        addCompass(hudComponents, hudCenter, rotation);

        return hudComponents;
    }

    public static void updateHud(Vector3d rotation, int altitude, @NotNull DisplayGroup hudGroup) {
        Map<String, ModelComponent> hudComponents = getHud(rotation);
        Map<String, Display> displays = hudGroup.getDisplays();
        hudComponents.forEach((name, component) -> displays.get(name).setTransformationMatrix(Utils.getHudMatrix(hudComponents.get(name))));

        TextDisplay altitudeText = (TextDisplay) displays.get("altitude");
        altitudeText.text(Component.text(altitude).color(HORIZON_ALTITUDE_INDICATOR_COLOR));
    }
}
