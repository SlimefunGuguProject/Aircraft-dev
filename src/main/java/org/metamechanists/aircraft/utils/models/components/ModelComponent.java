package org.metamechanists.aircraft.utils.models.components;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;


/**
 * Represents a single component of a model, composed of one or multiple Displays.
 */
public interface ModelComponent {
    Display build(@NotNull final Location origin, final Vector3d rotation);
    @SuppressWarnings("unused")
    Display build(@NotNull final Block block, final Vector3d rotation);
}
