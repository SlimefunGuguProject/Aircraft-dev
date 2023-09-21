package org.metamechanists.aircraft.vehicles;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
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


public class VehicleHud {
    private static double getPitch(final @NotNull Vector3d rotation) {
        final Vector3d lookingAtForward = Utils.rotateByEulerAngles(new Vector3d(1, 0, 0), rotation);
        final double pitch = lookingAtForward.angle(new Vector3d(lookingAtForward.x, 0, lookingAtForward.z));
        return lookingAtForward.y > 0 ? pitch : -pitch;
    }
    private static double getYaw(final @NotNull Vector3d rotation) {
        final Vector3d lookingAtForward = Utils.rotateByEulerAngles(new Vector3d(1, 0, 0), rotation);
        final double yaw = new Vector3d(lookingAtForward.x, 0, lookingAtForward.z).angle(new Vector3d(1, 0, 0));
        return lookingAtForward.z > 0 ? -yaw : yaw;
    }
    private static ModelAdvancedText rollIndependentComponent(final @NotNull Vector3f hudCenter, final @NotNull Vector3d rotation) {
        return new ModelAdvancedText()
                .rotate(rotation)
                .translate(hudCenter)
                .rotateBackwards(rotation)
                .rotate(new Vector3d(0, getYaw(rotation), getPitch(rotation)))
                .facing(BlockFace.WEST);
    }

    private static ModelAdvancedText getAltitudeIndicator(final @NotNull Vector3f hudCenter, final @NotNull Vector3d rotation) {
        return new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(rotation)
                .translate(hudCenter)
                .rotateBackwards(rotation)
                .rotate(new Vector3d(0, getYaw(rotation), getPitch(rotation)))
                .facing(BlockFace.WEST)
                .scale(new Vector3f(0.4F, 0.4F, 0.001F))
                .translate(0.5F, 0.35F, -0.01F);
    }
    private static ModelAdvancedText getHorizonIndicator(final @NotNull Vector3f hudCenter, final @NotNull Vector3d rotation) {
        return rollIndependentComponent(hudCenter, rotation)
                .text(Component.text("[ = <     > = ]").color(TextColor.color(255, 255, 255)))
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .scale(new Vector3f(0.4F, 0.4F, 0.001F))
                .translate(0.5F, 0.35F, -0.01F);
    }
    private static ModelAdvancedText getArtificialHorizonCenter(final @NotNull Vector3f hudCenter, final @NotNull Vector3d rotation, final Vector3f horizonOffset, final boolean shouldRender) {
        return new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .text(Component.text("------------------").color(TextColor.color(0, 255, 255)).decorate(TextDecoration.BOLD))
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(rotation)
                .translate(horizonOffset)
                .translate(hudCenter)
                .facing(BlockFace.WEST)
                .scale(shouldRender ? new Vector3f(0.3F, 0.3F, 0.001F) : new Vector3f())
                .translate(0.5F, 0.35F, 0);
    }
    private static ModelAdvancedText getArtificialHorizonBar(final Component component,
                                                             final @NotNull Vector3f hudCenter, final @NotNull Vector3d rotation, final Vector3f horizonOffset,
                                                             final Vector3f barOffset, final boolean shouldRender) {
        return new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .text(component)
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(rotation)
                .translate(horizonOffset)
                .translate(hudCenter)
                .translate(barOffset)
                .facing(BlockFace.WEST)
                .scale(shouldRender ? new Vector3f(0.2F, 0.2F, 0.001F) : new Vector3f())
                .translate(0.5F, 0.35F, 0);
    }
    private static ModelAdvancedText getArtificialHorizonDegree(final Component component,
                                                                final @NotNull Vector3f hudCenter, final @NotNull Vector3d rotation, final Vector3f totalAdjustment, final boolean shouldRender) {
        return new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .text(component)
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(rotation)
                .translate(totalAdjustment)
                .translate(hudCenter)
                .translate(new Vector3f(0, 0, 0.38F))
                .facing(BlockFace.WEST)
                .scale(shouldRender ? new Vector3f(0.2F, 0.2F, 0.001F) : new Vector3f())
                .translate(0.5F, 0.35F, 0);
    }

    private static void addArtificialHorizon(final @NotNull Map<String, ModelComponent> hudComponents, final @NotNull Vector3f hudCenter, final @NotNull Vector3d rotation) {
        hudComponents.put("altitude", getAltitudeIndicator(hudCenter, rotation));
        hudComponents.put("horizon", getHorizonIndicator(hudCenter, rotation));

        final Vector3f horizonOffset = new Vector3f(0, (float) (2 * -getPitch(rotation)), 0);
        final float horizonRadius = 0.4F;
        final boolean shouldRenderCenter = Math.abs(horizonOffset.y) < horizonRadius;

        hudComponents.put("horizon_center", getArtificialHorizonCenter(hudCenter, rotation, horizonOffset, shouldRenderCenter));

        final int bars = 30;
        final float verticalSpacing = (float) ((PI / 1.14) / (bars));
        for (int i = -bars; i <= bars; i++) {
            if (i == 0) {
                continue;
            }

            final Vector3f barOffset = new Vector3f(0, verticalSpacing * i, 0);
            final Vector3f totalAdjustment = new Vector3f(barOffset).add(horizonOffset);
            final boolean longBar = i % 5 == 0;
            final String text = "--------------" + (longBar ? "------" : "");
            final TextColor color = longBar ? TextColor.color(0, 180, 255) : TextColor.color(180, 180, 180);
            final Component component = Component.text(text).color(color);
            final boolean shouldRender = Math.abs(totalAdjustment.length()) < horizonRadius;

            hudComponents.put("horizon_bar_" + i, getArtificialHorizonBar(component, hudCenter, rotation, horizonOffset, barOffset, shouldRender));

            if (longBar) {
                final Component degreeComponent = Component.text(i * (90 / (bars-1)));
                hudComponents.put("horizon_degree_" + i, getArtificialHorizonDegree(degreeComponent, hudCenter, rotation, totalAdjustment, shouldRender));
            }
        }
    }
    private static ModelComponent getCompassBar(final @NotNull Vector3f hudCenter,
                                                final @NotNull Vector3d rotation, final @NotNull Vector3f totalAdjustment, final float compassRadius, final int i) {
        final boolean shouldRender = Math.abs(totalAdjustment.x) < compassRadius;
        final TextColor color;
        final float size;
        if (i % 30 == 0) {
            color = TextColor.color(0, 255, 0);
            size = 0.6F;
        } else if (i % 10 == 0) {
            color = TextColor.color(100, 140, 100);
            size = 0.3F;
        } else {
            color = TextColor.color(100, 100, 100);
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
    private static ModelComponent getCompassDirection(final @NotNull Vector3f hudCenter,
                                                final @NotNull Vector3d rotation, final @NotNull Vector3f totalAdjustment, final float compassRadius, final String text) {
        final boolean shouldRender = Math.abs(totalAdjustment.x) < compassRadius;
        return rollIndependentComponent(hudCenter, rotation)
                .text(Component.text(text).color(TextColor.color(255, 0, 0)))
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .translate(totalAdjustment)
                .scale(shouldRender ? new Vector3f(0.4F, 0.4F, 0.001F) : new Vector3f())
                .translate(0.5F, 0.0F, -0.01F);
    }
    private static ModelComponent getCompassDegree(final @NotNull Vector3f hudCenter,
                                                      final @NotNull Vector3d rotation, final @NotNull Vector3f totalAdjustment, final float compassRadius, final int degrees) {
        final boolean shouldRender = Math.abs(totalAdjustment.x) < compassRadius;
        return rollIndependentComponent(hudCenter, rotation)
                .text(Component.text(degrees).color(TextColor.color(255, 255, 255)))
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .translate(totalAdjustment)
                .scale(shouldRender ? new Vector3f(0.2F, 0.2F, 0.001F) : new Vector3f())
                .translate(0.5F, 0.02F, -0.01F);
    }
    private static ModelComponent getCompassNotch(final @NotNull Vector3f hudCenter, final @NotNull Vector3d rotation) {
        return rollIndependentComponent(hudCenter, rotation)
                .text(Component.text("v").color(TextColor.color(255, 255, 255)))
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .scale(new Vector3f(0.2F, 0.2F, 0.001F))
                .translate(0.5F, 0.3F, -0.01F);
    }

    private static void addCompass(final @NotNull Map<String, ModelComponent> hudComponents, final @NotNull Vector3f hudCenter, final @NotNull Vector3d rotation) {
        final int bars = 60;
        final int extraBars = 8;
        final Vector3f compassOffset = new Vector3f((float) (getYaw(rotation)), -0.6F, 0);
        final float compassRadius = 0.4F;
        final float horizontalSpacing = (float) (PI / (bars));

        hudComponents.put("compass_notch", getCompassNotch(hudCenter, rotation));

        for (int i = -bars-extraBars; i <= bars+extraBars; i++) {
            final Vector3f barOffset = new Vector3f(horizontalSpacing * i, 0, 0);
            final Vector3f totalAdjustment = new Vector3f(barOffset).add(compassOffset);
            hudComponents.put("compass_bar_" + i, getCompassBar(hudCenter, rotation, totalAdjustment, compassRadius, i));

            if ((i+420) % 30 == 0) {
                final String text = switch (i) {
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
                } else if (degrees > 360) {
                    degrees -= 360;
                }
                hudComponents.put("compass_degree_" + i, getCompassDegree(hudCenter, rotation, totalAdjustment, compassRadius, degrees));
            }
        }
    }

    public static @NotNull Map<String, ModelComponent> getHud(final @NotNull Vector3d rotation) {
        final Map<String, ModelComponent> hudComponents = new HashMap<>();
        final Vector3f hudCenter = new Vector3f(1, 0, 0);

        addArtificialHorizon(hudComponents, hudCenter, rotation);
        addCompass(hudComponents, hudCenter, rotation);

        return hudComponents;
    }

    public static void updateHud(final Vector3d rotation, final int altitude, final @NotNull DisplayGroup hudGroup) {
        final Map<String, ModelComponent> hudComponents = getHud(rotation);
        final Map<String, Display> displays = hudGroup.getDisplays();
        hudComponents.forEach((name, component) -> displays.get(name).setTransformationMatrix(Utils.getHudMatrix(hudComponents.get(name))));

        final TextDisplay altitudeText = (TextDisplay) displays.get("altitude");
        altitudeText.text(Component.text(altitude).color(TextColor.color(0, 255, 0)));
    }
}
