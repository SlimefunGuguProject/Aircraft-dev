package org.metamechanists.aircraft.utils.models;

import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.builders.TextDisplayBuilder;
import org.metamechanists.aircraft.utils.transformations.TransformationMatrixBuilder;


@SuppressWarnings("unused")
@Getter
public class ModelText implements ModelComponent {
    private final TextDisplayBuilder main = new TextDisplayBuilder();

    private Vector3f location = new Vector3f();
    private Vector3f facing = new Vector3f(0, 0, 1);
    private Vector3f secondLocation = new Vector3f();
    private Vector3f size = new Vector3f();
    private Vector3d rotation = new Vector3d();
    private Vector3d secondRotation = new Vector3d();

    /**
     * @param location The center of the cuboid
     */
    public ModelText location(@NotNull final Vector3f location) {
        this.location = location;
        return this;
    }
    /**
     * @param location The center of the cuboid
     */
    public ModelText location(@NotNull final Vector3d location) {
        this.location = new Vector3f((float) location.x, (float) location.y, (float) location.z);
        return this;
    }
    /**
     * Sets the center of the cuboid
     */
    public ModelText location(final float x, final float y, final float z) {
        return location(new Vector3f(x, y, z));
    }

    /**
     * Sets the orientation of the text (default is south AKA positive Z)
     * The player will only see the text when looking at it from this orientation
     */
    public ModelText facing(final @NotNull Vector3f facing) {
        this.facing = facing;
        return this;
    }
    /**
     * Sets the orientation of the text (default is south AKA positive Z)
     * The player will only see the text when looking at it from this orientation
     */
    public ModelText facing(final @NotNull BlockFace face) {
        return facing(face.getDirection().toVector3f());
    }

    public ModelText secondLocation(@NotNull final Vector3d location) {
        this.secondLocation = new Vector3f((float) location.x, (float) location.y, (float) location.z);
        return this;
    }

    /**
     * @param size The size of the cuboid (ie: the distance from one side to the other) on each axis
     */
    public ModelText size(@NotNull final Vector3f size) {
        this.size = size;
        return this;
    }
    /**
     * @param size The size of the cuboid (ie: the distance from one side to the other) on each axis
     */
    public ModelText size(@NotNull final Vector3d size) {
        this.size = new Vector3f((float) size.x, (float) size.y, (float) size.z);
        return this;
    }
    /**
     * Sets the size of the cuboid (ie: the distance from one side to the other) on each axis
     */
    public ModelText size(final float x, final float y, final float z) {
        return size(new Vector3f(x, y, z));
    }
    /**
     * @param size The size of the cuboid (ie: the distance from one side to the other) on all three axes - this forms a cube
     */
    public ModelText size(final float size) {
        return size(new Vector3f(size));
    }

    /**
     * @param rotation The rotation of the cuboid in radians
     */
    public ModelText secondRotation(@NotNull final Vector3d secondRotation) {
        this.secondRotation = secondRotation;
        return this;
    }
    /**
     * @param rotation The rotation of the cuboid in radians
     */
    public ModelText rotation(@NotNull final Vector3d rotation) {
        this.rotation = rotation;
        return this;
    }
    /**
     * Sets the rotation of the cuboid in radians
     */
    public ModelText rotation(final double x, final double y, final double z) {
        return rotation(new Vector3d(x, y, z));
    }
    /**
     * @param rotationY The rotation of the cuboid around the Y axis in radians
     */
    public ModelText rotation(final double rotationY) {
        return rotation(new Vector3d(0, rotationY, 0));
    }

    /**
     * @param color The background of the text (default is transparent)
     */
    public ModelText background(@NotNull final Color color) {
        main.backgroundColor(color);
        return this;
    }
    public ModelText text(@NotNull final String text) {
        main.text(text);
        return this;
    }
    public ModelText brightness(final int blockBrightness) {
        main.brightness(blockBrightness);
        return this;
    }
    public ModelText glow(@NotNull final Color color) {
        main.glow(color);
        return this;
    }

    @Override
    public Matrix4f getMatrix(final Vector3d modelRotation) {
        return new TransformationMatrixBuilder()
                .rotate(modelRotation)
                .lookAlong(facing)
                .translate(location)
                .rotate(rotation)
                .rotate(secondRotation)
                .translate(secondLocation)
                .scale(new Vector3f(size))
                .buildForBlockDisplay();
    }
    @Override
    public Matrix4f getMatrix(final Quaterniond modelRotation) {
        return new TransformationMatrixBuilder()
                .rotate(modelRotation)
                .lookAlong(facing)
                .translate(location)
                .rotate(rotation)
                .rotate(secondRotation)
                .translate(secondLocation)
                .scale(new Vector3f(size))
                .buildForBlockDisplay();
    }

    @Override
    public TextDisplay build(@NotNull final Location origin, @NotNull final Vector3d modelRotation) {
        return main.transformation(getMatrix(modelRotation)).build(origin);
    }
    @Override
    public TextDisplay build(@NotNull final Block block, @NotNull final Vector3d modelRotation) {
        return build(block.getLocation(), modelRotation);
    }
}
