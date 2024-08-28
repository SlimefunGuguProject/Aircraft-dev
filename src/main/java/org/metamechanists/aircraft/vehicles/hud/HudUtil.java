package org.metamechanists.aircraft.vehicles.hud;

import org.bukkit.Color;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelItem;
import org.metamechanists.displaymodellib.models.components.ModelText;


public final class HudUtil {
    private HudUtil() {}

    public static double getVelocityPitch(@NotNull VehicleState state) {
        Vector3d absoluteVelocity = state.absoluteVelocity();
        double pitch = absoluteVelocity.angle(new Vector3d(absoluteVelocity.x, 0, absoluteVelocity.z));
        return absoluteVelocity.y > 0 ? pitch : -pitch;
    }

    private static ModelText defaultText() {
        return new ModelText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON);
    }

    private static ModelItem defaultCuboid() {
        return new ModelItem()
                .brightness(Utils.BRIGHTNESS_ON);
    }

    public static ModelText rollText(@NotNull Vector3f hudCenter, @NotNull Vector3f offset) {
        return defaultText()
                .translate(hudCenter)
                .translate(offset)
                .lookAlong(BlockFace.WEST);
    }

    public static ModelItem rollCuboid(@NotNull Vector3f hudCenter) {
        return defaultCuboid()
                .translate(hudCenter)
                .lookAlong(BlockFace.WEST);
    }

    public static ModelText rollText(@NotNull Vector3f hudCenter) {
        return rollText(hudCenter, new Vector3f());
    }

    public static ModelText rollIndependentText(@NotNull VehicleState state, @NotNull Vector3f hudCenter) {
        return defaultText()
                .translate(hudCenter)
                .undoRotate(state.rotation)
                .rotate(new Vector3d(0, state.yaw(), state.pitch()))
                .lookAlong(BlockFace.WEST);
    }

    public static ModelItem rollIndependentCuboid(@NotNull VehicleState state, @NotNull Vector3f hudCenter) {
        return defaultCuboid()
                .translate(hudCenter)
                .undoRotate(state.rotation)
                .rotate(new Vector3d(0, state.yaw(), state.pitch()))
                .lookAlong(BlockFace.WEST);
    }
}
