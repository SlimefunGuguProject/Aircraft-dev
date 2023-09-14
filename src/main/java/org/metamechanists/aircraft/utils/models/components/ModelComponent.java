package org.metamechanists.aircraft.utils.models.components;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;


/**
 * Represents a single component of a model, composed of one or multiple Displays.
 */
public interface ModelComponent {
    Display build(@NotNull final Location origin, @NotNull final Quaterniond modelRotation);
    @SuppressWarnings("unused")
    Display build(@NotNull final Block block, @NotNull final Quaterniond modelRotation);
}
