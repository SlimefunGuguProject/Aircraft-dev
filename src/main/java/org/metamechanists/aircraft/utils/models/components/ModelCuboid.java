package org.metamechanists.aircraft.utils.models.components;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.builders.BlockDisplayBuilder;
import org.metamechanists.aircraft.utils.transformations.TransformationMatrixBuilder;
import org.metamechanists.aircraft.vehicles.AircraftSurface;

import java.util.HashSet;
import java.util.Set;


@SuppressWarnings("unused")
public class ModelCuboid implements ModelComponent {
    private final BlockDisplayBuilder main = new BlockDisplayBuilder();

    private Vector3f location = new Vector3f();
    private Vector3f size = new Vector3f();
    private Vector3d rotation = new Vector3d();

    /**
     * @param location The center of the cuboid
     */
    public ModelCuboid location(@NotNull final Vector3f location) {
        this.location = location;
        return this;
    }
    /**
     * Sets the center of the cuboid
     */
    public ModelCuboid location(final float x, final float y, final float z) {
        return location(new Vector3f(x, y, z));
    }

    /**
     * @param size The size of the cuboid (ie: the distance from one side to the other) on each axis
     */
    public ModelCuboid size(@NotNull final Vector3f size) {
        this.size = size;
        return this;
    }
    /**
     * Sets the size of the cuboid (ie: the distance from one side to the other) on each axis
     */
    public ModelCuboid size(final float x, final float y, final float z) {
        return size(new Vector3f(x, y, z));
    }
    /**
     * @param size The size of the cuboid (ie: the distance from one side to the other) on all three axes - this forms a cube
     */
    public ModelCuboid size(final float size) {
        return size(new Vector3f(size));
    }

    /**
     * @param rotation The rotation of the cuboid in radians
     */
    public ModelCuboid rotation(@NotNull final Vector3d rotation) {
        this.rotation = rotation;
        return this;
    }
    /**
     * Sets the rotation of the cuboid in radians
     */
    public ModelCuboid rotation(final double x, final double y, final double z) {
        return rotation(new Vector3d(x, y, z));
    }
    /**
     * @param rotationY The rotation of the cuboid around the Y axis in radians
     */
    public ModelCuboid rotation(final double rotationY) {
        return rotation(new Vector3d(0, rotationY, 0));
    }

    public ModelCuboid material(@NotNull final Material material) {
        main.material(material);
        return this;
    }
    /**
     * Overrides material
     */
    public ModelCuboid block(@NotNull final BlockData block) {
        main.blockData(block);
        return this;
    }
    public ModelCuboid brightness(final int blockBrightness) {
        main.brightness(blockBrightness);
        return this;
    }
    public ModelCuboid glow(@NotNull final Color color) {
        main.glow(color);
        return this;
    }

    public Matrix4f getMatrix(final Vector3d modelRotation) {
        return new TransformationMatrixBuilder()
                .rotate(modelRotation)
                .translate(location)
                .rotate(rotation)
                .scale(new Vector3f(size))
                .buildForBlockDisplay();
    }
    public BlockDisplay build(@NotNull final Location origin, @NotNull final Vector3d modelRotation) {
        return main.transformation(getMatrix(modelRotation)).build(origin);
    }
    public BlockDisplay build(@NotNull final Block block, @NotNull final Vector3d modelRotation) {
        return build(block.getLocation(), modelRotation);
    }
    private @NotNull AircraftSurface getSurface(
            final double dragCoefficient, final double liftCoefficient,
            final @NotNull Vector3d startingLocation, final double surfaceWidth, final double surfaceHeight) {
        final double area = surfaceWidth * surfaceHeight;
        final Vector3d relativeLocation = startingLocation.rotateX(rotation.x).rotateY(rotation.y).rotateZ(rotation.z);
        final Vector3d normal = new Vector3d(relativeLocation).normalize();
        return new AircraftSurface(dragCoefficient, liftCoefficient, area, normal, new Vector3d(location).add(relativeLocation));
    }
    public Set<AircraftSurface> getSurfaces(final double dragCoefficient, final double liftCoefficient) {
        final Set<AircraftSurface> surfaces = new HashSet<>();

        surfaces.add(getSurface(dragCoefficient, liftCoefficient, new Vector3d(0, 0, size.z / 2), size.x, size.y));
        surfaces.add(getSurface(dragCoefficient, liftCoefficient, new Vector3d(0, 0, -size.z / 2), size.x, size.y));

        surfaces.add(getSurface(dragCoefficient, liftCoefficient, new Vector3d(0, size.y / 2, 0), size.x, size.z));
        surfaces.add(getSurface(dragCoefficient, liftCoefficient, new Vector3d(0, -size.y / 2, 0), size.x, size.z));

        surfaces.add(getSurface(dragCoefficient, liftCoefficient, new Vector3d(size.x, 0, 0), size.y, size.z));
        surfaces.add(getSurface(dragCoefficient, liftCoefficient, new Vector3d(-size.x, 0, 0), size.y, size.z));

        return surfaces;
    }
}
