package org.metamechanists.aircraft.vehicles;

import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.metamechanists.aircraft.vehicles.components.FixedComponent;
import org.metamechanists.aircraft.vehicles.components.HingeComponent;
import org.metamechanists.aircraft.vehicles.components.VehicleComponent;
import org.metamechanists.metalib.yaml.YamlTraverser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VehicleDescription {
    private record ComponentGroup(double density, double dragCoefficient, double liftCoefficient) {}

    private final double mass;
    private final double momentOfInertia;
    private final double velocityDampening;
    private final double angularVelocityDampening;
    private final Map<String, VehicleComponent> components = new HashMap<>();

    public VehicleDescription(final @NotNull YamlTraverser traverser) {
        mass = traverser.get("mass");
        momentOfInertia = traverser.get("momentOfInertia");
        velocityDampening = traverser.get("velocityDampening");
        angularVelocityDampening = traverser.get("angularVelocityDampening");

        final Map<String, ComponentGroup> groups = new HashMap<>();
        final YamlTraverser groupTraverser = traverser.getSection("groups");
        for (final YamlTraverser group : groupTraverser.getSections()) {
            groups.put(group.name(), new ComponentGroup(group.get("density"), group.get("dragCoefficient"), group.get("liftCoefficient")));
        }

        final YamlTraverser componentTraverser = traverser.getSection("components");
        for (final YamlTraverser component : componentTraverser.getSections()) {
            final String name = component.name();
            final ComponentGroup group = groups.get(component.get("group"));
            final Material material = Material.valueOf(component.get("material"));
            final List<Double> sizeList = component.get("size");
            final Vector3d size = new Vector3d(sizeList.get(0), sizeList.get(1), sizeList.get(2));
            final List<Double> locationList = component.get("location");
            final Vector3d location = new Vector3d(locationList.get(0), locationList.get(1), locationList.get(2));
            final FixedComponent fixedComponent = new FixedComponent(group.density, group.dragCoefficient, group.liftCoefficient, material, size, location);

            if (component.get("control_surface", false)) {
                components.put(name, fixedComponent);
            } else {
                components.put(name, new HingeComponent(fixedComponent, component.get("rotationAxis"), component.get("rotationRate"), component.get("rotationMax")));
            }
        }
    }
}
