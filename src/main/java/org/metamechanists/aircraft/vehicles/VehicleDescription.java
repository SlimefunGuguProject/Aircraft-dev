package org.metamechanists.aircraft.vehicles;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.models.ModelCuboid;
import org.metamechanists.aircraft.vehicles.components.FixedComponent;
import org.metamechanists.aircraft.vehicles.components.HingeComponent;
import org.metamechanists.aircraft.vehicles.components.VehicleComponent;
import org.metamechanists.metalib.yaml.YamlTraverser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class VehicleDescription {
    private record ComponentGroup(double density, double dragCoefficient, double liftCoefficient) {}

    private final double mass;
    private final double momentOfInertia;
    private final double velocityDampening;
    private final Map<String, VehicleComponent> components = new HashMap<>();

    private static @NotNull Vector3d getVector3d(@NotNull final YamlTraverser traverser, final String name) {
        final List<Double> rotationList = traverser.get(name);
        return new Vector3d(rotationList.get(0), rotationList.get(1), rotationList.get(2));
    }
    private static @NotNull Vector3f getVector3f(@NotNull final YamlTraverser traverser, final String name) {
        final Vector3d as3d = getVector3d(traverser, name);
        return new Vector3f((float) as3d.x, (float) as3d.y, (float) as3d.z);
    }

    @SuppressWarnings("DataFlowIssue")
    private static @NotNull VehicleComponent getComponentFromTraverser(final @NotNull YamlTraverser component, final @NotNull Map<String, ComponentGroup> groups) {
        final ComponentGroup group = groups.get(component.get("group"));
        final Material material = Material.valueOf(component.get("material"));
        final Vector3f size = getVector3f(component, "size");
        final Vector3f location = getVector3f(component, "location");
        final Vector3d rotation = getVector3d(component, "rotation");
        final FixedComponent fixedComponent = new FixedComponent(component.name(), group.density, group.dragCoefficient, group.liftCoefficient, material, size, location, rotation);

        if (component.get("control_surface", false)) {
            return new HingeComponent(fixedComponent, component.get("rotationAxis"), component.get("rotationRate"), component.get("rotationMax"));
        }

        return fixedComponent;
    }

    @SuppressWarnings("DataFlowIssue")
    public VehicleDescription(final @NotNull YamlTraverser traverser) {
        mass = traverser.get("mass");
        momentOfInertia = traverser.get("momentOfInertia");
        velocityDampening = traverser.get("velocityDampening");
        double angularVelocityDampening = traverser.get("angularVelocityDampening");

        final Map<String, ComponentGroup> groups = new HashMap<>();
        for (final YamlTraverser group : traverser.getSection("groups").getSections()) {
            groups.put(group.name(), new ComponentGroup(group.get("density"), group.get("dragCoefficient"), group.get("liftCoefficient")));
        }

        for (final YamlTraverser component : traverser.getSection("components").getSections()) {
            components.put(component.name(), getComponentFromTraverser(component, groups));
        }
    }

    public Set<VehicleSurface> getSurfaces(final ControlSurfaces controlSurfaces) {
        return components.values().stream().flatMap(component -> component.getSurfaces().stream()).collect(Collectors.toSet());
    }

    public Map<String, ModelCuboid> getCuboids(final ControlSurfaces controlSurfaces) {
        return components.values().stream().collect(Collectors.toMap(VehicleComponent::getName, VehicleComponent::getCuboid, (a, b) -> b));
    }
}
