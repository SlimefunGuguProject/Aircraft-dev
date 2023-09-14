package org.metamechanists.aircraft.utils.models;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.metamechanists.aircraft.utils.models.components.ModelComponent;
import org.metamechanists.metalib.sefilib.entity.display.DisplayGroup;

import java.util.HashMap;
import java.util.Map;


/**
 * Builder class that allows you to construct a model using components
 */
public class ModelBuilder {
    private final Map<String, ModelComponent> components = new HashMap<>();

    public ModelBuilder add(@NotNull final String name, @NotNull final ModelComponent component) {
        components.put(name, component);
        return this;
    }

    /**
     * Creates all the components and adds them to a displaygroup
     * @param center The center location of the model
     * @return The display group containing all the components
     */
    public DisplayGroup buildAtLocation(@NotNull final Location center) {
        final DisplayGroup group = new DisplayGroup(center.clone(), 0, 0);
        components.forEach((name, component) -> group.addDisplay(name, component.build(center.clone(), new Quaterniond())));
        return group;
    }
    /**
     * Creates all the components and adds them to a displaygroup
     * @param blockLocation The block location of the model (this will be converted to the center of the block and used to build the model)
     * @return The display group containing all the components
     */
    public DisplayGroup buildAtBlockCenter(@NotNull final Location blockLocation) {
        final DisplayGroup group = new DisplayGroup(blockLocation.clone(), 0, 0);
        components.forEach((name, component) -> group.addDisplay(name, component.build(blockLocation.clone().toCenterLocation(), new Quaterniond())));
        return group;
    }
}
