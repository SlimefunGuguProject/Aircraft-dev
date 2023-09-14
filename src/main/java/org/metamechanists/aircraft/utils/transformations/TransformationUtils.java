package org.metamechanists.aircraft.utils.transformations;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.Utils;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class TransformationUtils {

    public @NotNull Vector3f getDisplacement(final Location from, @NotNull final Location to) {
        return to.clone().subtract(from).toVector().toVector3f();
    }
    public @NotNull Vector3f getDisplacement(final Vector3f from, @NotNull final Vector3f to) {
        return new Vector3f(to).sub(from);
    }

    public @NotNull Vector3f getDirection(@NotNull final Location from, @NotNull final Location to) {
        return getDisplacement(from, to).normalize();
    }
    public @NotNull Vector3f getDirection(@NotNull final Vector3f from, @NotNull final Vector3f to) {
        return getDisplacement(from, to).normalize();
    }

    public @NotNull Location getMidpoint(@NotNull final Location from, @NotNull final Location to) {
        return from.clone().add(to).multiply(0.5);
    }
    public @NotNull Vector3f getMidpoint(@NotNull final Vector3f from, @NotNull final Vector3f to) {
        return new Vector3f(from).add(to).mul(0.5F);
    }
}
