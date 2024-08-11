package org.metamechanists.aircraft.vehicles.hud;

import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedText;


public final class Util {
    private Util() {}

    public static double getPitch(@NotNull VehicleState state) {
        Vector3d lookingAtForward = Utils.rotateByEulerAngles(new Vector3d(1, 0, 0), state.rotation);
        double pitch = lookingAtForward.angle(new Vector3d(lookingAtForward.x, 0, lookingAtForward.z));
        return lookingAtForward.y > 0 ? pitch : -pitch;
    }

    public static double getYaw(@NotNull VehicleState state) {
        Vector3d lookingAtForward = Utils.rotateByEulerAngles(new Vector3d(1, 0, 0), state.rotation);
        double yaw = new Vector3d(lookingAtForward.x, 0, lookingAtForward.z).angle(new Vector3d(1, 0, 0));
        return lookingAtForward.z > 0 ? -yaw : yaw;
    }

    public static ModelAdvancedText rollIndependentComponent(@NotNull VehicleState state, @NotNull Vector3f hudCenter) {
        return new ModelAdvancedText()
                .rotate(state.rotation)
                .translate(hudCenter)
                .rotateBackwards(state.rotation)
                .rotate(new Vector3d(0, getYaw(state), getPitch(state)))
                .facing(BlockFace.WEST);
    }
}
