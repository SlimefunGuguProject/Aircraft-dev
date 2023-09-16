package org.metamechanists.aircraft.utils.models;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.transformations.TransformationMatrixBuilder;


/**
 * Represents a single component of a model, composed of one or multiple Displays.
 */
public interface ModelComponent {
    Display build(@NotNull final Location origin, @NotNull final Vector3d modelRotation);
    @SuppressWarnings("unused")
    Display build(@NotNull final Block block, @NotNull final Vector3d modelRotation);
    Matrix4f getMatrix(final Vector3d modelRotation);
    Matrix4f getMatrix(final Quaterniond modelRotation);
}