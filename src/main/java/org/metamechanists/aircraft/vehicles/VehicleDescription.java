package org.metamechanists.aircraft.vehicles;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.models.ModelCuboid;
import org.metamechanists.aircraft.vehicles.components.FixedComponent;
import org.metamechanists.aircraft.vehicles.components.HingeComponent;
import org.metamechanists.metalib.yaml.YamlTraverser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class VehicleDescription {
    private record ComponentGroup(double density, double dragCoefficient, double liftCoefficient) {}

    private final double mass;
    private final double momentOfInertia;
    private final double velocityDampening;
    private final Set<FixedComponent> fixedComponents = new HashSet<>();
    private final Set<HingeComponent> hingeComponents = new HashSet<>();

    private static @NotNull Vector3d getVector3d(@NotNull final YamlTraverser traverser, final String name) {
        final List<Double> rotationList = traverser.get(name);
        return new Vector3d(rotationList.get(0), rotationList.get(1), rotationList.get(2));
    }
    private static @NotNull Vector3f getVector3f(@NotNull final YamlTraverser traverser, final String name) {
        final Vector3d as3d = getVector3d(traverser, name);
        return new Vector3f((float) as3d.x, (float) as3d.y, (float) as3d.z);
    }

    @SuppressWarnings("DataFlowIssue")
    private void processComponentFromTraverser(final @NotNull YamlTraverser componentTraverser, final @NotNull Map<String, ComponentGroup> groups) {
        final ComponentGroup group = groups.get(componentTraverser.get("group"));
        final Material material = Material.valueOf(componentTraverser.get("material"));
        final Vector3f size = getVector3f(componentTraverser, "size");
        final Vector3f location = getVector3f(componentTraverser, "location");
        final Vector3d rotation = getVector3d(componentTraverser, "rotation");
        final FixedComponent fixedComponent = new FixedComponent(componentTraverser.name(), group.density, group.dragCoefficient, group.liftCoefficient, material, size, location, rotation);

        if (componentTraverser.get("controlSurface", false)) {
            hingeComponents.add(new HingeComponent(fixedComponent,
                    componentTraverser.get("rotationAxis"), componentTraverser.get("rotationRate"), componentTraverser.get("rotationMax"),
                    componentTraverser.get("keyUp"), componentTraverser.get("keyDown")));
        }

        fixedComponents.add(fixedComponent);
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

        for (final YamlTraverser componentTraverser : traverser.getSection("components").getSections()) {
            processComponentFromTraverser(componentTraverser, groups);
        }
    }

    @SuppressWarnings("SimplifyForEach")
    public Set<VehicleSurface> getSurfaces(final Map<String, ControlSurfaceOrientation> orientations) {
        final Set<VehicleSurface> surfaces = new HashSet<>();
        fixedComponents.forEach(component -> surfaces.addAll(component.getSurfaces()));
        hingeComponents.forEach(component -> surfaces.addAll(component.getSurfaces(orientations)));
        return surfaces;
    }

    @SuppressWarnings("SimplifyForEach")
    public Map<String, ModelCuboid> getCuboids(final Map<String, ControlSurfaceOrientation> orientations) {
        final Map<String, ModelCuboid> surfaces = new HashMap<>();
        fixedComponents.forEach(component -> surfaces.put(component.getName(), component.getCuboid()));
        hingeComponents.forEach(component -> surfaces.put(component.getName(), component.getCuboid(orientations)));
        return surfaces;
    }

    public void adjustHingeComponents(final Map<String, ControlSurfaceOrientation> orientations, final char key) {
        hingeComponents.forEach(component -> component.useKey(orientations, key));
    }

    public Map<String, ControlSurfaceOrientation> initializeOrientations() {
        return hingeComponents.stream().collect(Collectors.toMap(HingeComponent::getName, component -> new ControlSurfaceOrientation(), (name, orientation) -> orientation));
    }
}
