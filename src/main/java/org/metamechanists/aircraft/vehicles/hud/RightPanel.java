package org.metamechanists.aircraft.vehicles.hud;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedCuboid;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedText;
import org.metamechanists.displaymodellib.models.components.ModelComponent;

import java.util.Map;


public final class RightPanel {
    private static final TextColor KEY_COLOR = TextColor.color(170, 255, 170);
    private static final TextColor VALUE_COLOR = TextColor.color(0, 255, 0);

    private RightPanel() {}

    private static ModelAdvancedText getAccelerationKey(VehicleState state, Vector3f hudCenter, Vector3f panelCenter) {
        return Util.rollIndependentText(state, hudCenter)
                .alignment(TextAlignment.LEFT)
                .text(Component.text("ACC").color(KEY_COLOR))
                .translate(panelCenter)
                .translate(0.0F, 0.25F, 0.0F)
                .scale(new Vector3f(0.15F, 0.15F, 0.001F));
    }

    private static ModelAdvancedText getAccelerationValue(VehicleState state, Vector3f hudCenter, Vector3f panelCenter) {
        return Util.rollIndependentText(state, hudCenter)
                .alignment(TextAlignment.LEFT)
                .translate(panelCenter)
                .translate(0.09F, 0.25F, 0.0F)
                .scale(new Vector3f(0.15F, 0.15F, 0.001F));
    }

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
                .translate(0.09F, 0.2F, 0.0F)
                .scale(new Vector3f(0.15F, 0.15F, 0.001F));
    }

    private static ModelAdvancedText getAngularAccelerationKey(VehicleState state, Vector3f hudCenter, Vector3f panelCenter) {
        return Util.rollIndependentText(state, hudCenter)
                .alignment(TextAlignment.LEFT)
                .text(Component.text("AAC").color(KEY_COLOR))
                .translate(panelCenter)
                .translate(0.0F, 0.15F, 0.0F)
                .scale(new Vector3f(0.15F, 0.15F, 0.001F));
    }

    private static ModelAdvancedText getAngularAccelerationValue(VehicleState state, Vector3f hudCenter, Vector3f panelCenter) {
        return Util.rollIndependentText(state, hudCenter)
                .alignment(TextAlignment.LEFT)
                .translate(panelCenter)
                .translate(0.09F, 0.15F, 0.0F)
                .scale(new Vector3f(0.15F, 0.15F, 0.001F));
    }

    private static ModelAdvancedText getAngularVelocityKey(VehicleState state, Vector3f hudCenter, Vector3f panelCenter) {
        return Util.rollIndependentText(state, hudCenter)
                .alignment(TextAlignment.LEFT)
                .text(Component.text("AVL").color(KEY_COLOR))
                .translate(panelCenter)
                .translate(0.0F, 0.1F, 0.0F)
                .scale(new Vector3f(0.15F, 0.15F, 0.001F));
    }

    private static ModelAdvancedText getAngularVelocityValue(VehicleState state, Vector3f hudCenter, Vector3f panelCenter) {
        return Util.rollIndependentText(state, hudCenter)
                .alignment(TextAlignment.LEFT)
                .translate(panelCenter)
                .translate(0.09F, 0.1F, 0.0F)
                .scale(new Vector3f(0.15F, 0.15F, 0.001F));
    }

    private static ModelAdvancedCuboid getThrottleBackground(VehicleState state, Vector3f hudCenter, Vector3f panelCenter) {
        return Util.rollIndependentCuboid(state, hudCenter)
                .material(Material.GRAY_CONCRETE)
                .translate(panelCenter)
                .translate(-0.13F, 0.0F, 0.0F)
                .scale(new Vector3f(0.01F, 0.4F, 0.001F));
    }

    private static ModelAdvancedCuboid getThrottleForeground(@NotNull VehicleState state, Vector3f hudCenter, Vector3f panelCenter) {
        float fraction = (float) (state.throttle / 100.0);
        return Util.rollIndependentCuboid(state, hudCenter)
                .material(Material.LIGHT_BLUE_CONCRETE)
                .translate(panelCenter)
                .translate(-0.13F, -0.2F + 0.2F * fraction, 0.001F)
                .scale(new Vector3f(0.01F, 0.4F * fraction, 0.001F));
    }

    public static void build(VehicleState state, @NotNull Map<String, ModelComponent> hudComponents, Vector3f hudCenter) {
        Vector3f panelCenter = new Vector3f(0.3F, 0.0F, 0.0F);

        hudComponents.put("acceleration_key", getAccelerationKey(state, hudCenter, panelCenter));
        hudComponents.put("acceleration_value", getAccelerationValue(state, hudCenter, panelCenter));

        hudComponents.put("velocity_key", getVelocityKey(state, hudCenter, panelCenter));
        hudComponents.put("velocity_value", getVelocityValue(state, hudCenter, panelCenter));

        hudComponents.put("angular_acceleration_key", getAngularAccelerationKey(state, hudCenter, panelCenter));
        hudComponents.put("angular_acceleration_value", getAngularAccelerationValue(state, hudCenter, panelCenter));

        hudComponents.put("angular_velocity_key", getAngularVelocityKey(state, hudCenter, panelCenter));
        hudComponents.put("angular_velocity_value", getAngularVelocityValue(state, hudCenter, panelCenter));

        hudComponents.put("throttle_background", getThrottleBackground(state, hudCenter, panelCenter));
        hudComponents.put("throttle_foreground", getThrottleForeground(state, hudCenter, panelCenter));
    }

    public static void update(@NotNull VehicleState state, @NotNull Map<String, Display> displays, double acceleration, double angularAcceleration) {
        TextDisplay accelerationText = (TextDisplay) displays.get("acceleration_value");
        accelerationText.text(Component.text(Math.round(acceleration * 1000.0) / 1000.0).color(VALUE_COLOR));

        TextDisplay velocityText = (TextDisplay) displays.get("velocity_value");
        velocityText.text(Component.text(Math.round(state.velocity.length() * 1000.0) / 1000.0).color(VALUE_COLOR));

        TextDisplay angularVelocityText = (TextDisplay) displays.get("angular_velocity_value");
        angularVelocityText.text(Component.text(Math.round(Math.toDegrees(state.angularVelocity.length()) * 1000.0) / 1000.0).color(VALUE_COLOR));

        TextDisplay angularAccelerationText = (TextDisplay) displays.get("angular_acceleration_value");
        angularAccelerationText.text(Component.text(Math.round(Math.toDegrees(angularAcceleration) * 1000.0) / 1000.0).color(VALUE_COLOR));
    }
}
