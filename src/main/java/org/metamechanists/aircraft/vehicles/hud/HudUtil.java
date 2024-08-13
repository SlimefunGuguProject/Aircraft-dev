package org.metamechanists.aircraft.vehicles.hud;

import org.bukkit.Color;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedCuboid;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedText;


public final class HudUtil {
    private HudUtil() {}

    public static double getPitch(@NotNull Vector3d lookingAtForward) {
        double pitch = lookingAtForward.angle(new Vector3d(lookingAtForward.x, 0, lookingAtForward.z));
        return lookingAtForward.y > 0 ? pitch : -pitch;
    }

    private static ModelAdvancedText defaultText() {
        return new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON);
    }

    private static ModelAdvancedCuboid defaultCuboid() {
        return new ModelAdvancedCuboid()
                .brightness(Utils.BRIGHTNESS_ON);
    }

    public static ModelAdvancedText rollText(@NotNull VehicleState state, @NotNull Vector3f hudCenter, @NotNull Vector3f offset) {
        return defaultText()
                .rotate(state.rotation)
                .translate(hudCenter)
                .translate(offset)
                .facing(BlockFace.WEST);
    }

    public static ModelAdvancedCuboid rollCuboid(@NotNull VehicleState state, @NotNull Vector3f hudCenter) {
        return defaultCuboid()
                .rotate(state.rotation)
                .translate(hudCenter)
                .facing(BlockFace.WEST);
    }

    public static ModelAdvancedText rollText(@NotNull VehicleState state, @NotNull Vector3f hudCenter) {
        return rollText(state, hudCenter, new Vector3f());
    }

    public static ModelAdvancedText rollIndependentText(@NotNull VehicleState state, @NotNull Vector3f hudCenter) {
        return defaultText()
                .rotate(state.rotation)
                .translate(hudCenter)
                .rotateBackwards(state.rotation)
                .rotate(new Vector3d(0, state.getYaw(), state.getPitch()))
                .facing(BlockFace.WEST);
    }

    public static ModelAdvancedCuboid rollIndependentCuboid(@NotNull VehicleState state, @NotNull Vector3f hudCenter) {
        return defaultCuboid()
                .rotate(state.rotation)
                .translate(hudCenter)
                .rotateBackwards(state.rotation)
                .rotate(new Vector3d(0, state.getYaw(), state.getPitch()))
                .facing(BlockFace.WEST);
    }
}
