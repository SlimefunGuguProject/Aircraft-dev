package org.metamechanists.aircraft.vehicles;

import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.vehicles.components.FixedComponent;
import org.metamechanists.aircraft.vehicles.components.HingeComponent;
import org.metamechanists.aircraft.vehicles.surfaces.ControlSurfaceOrientation;
import org.metamechanists.aircraft.vehicles.surfaces.VehicleSurface;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedCuboid;
import org.metamechanists.metalib.yaml.YamlTraverser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class VehicleDescription {
    private record ComponentGroup(double dragCoefficient, double liftCoefficient) {}

    @Getter
    private final String path;
    @Getter
    private final double mass;
    @Getter
    private final double momentOfInertia;
    private final double velocityDampening;
    private final double angularVelocityDampening;
    @Getter
    private final Vector3d thrustLocation;
    @Getter
    private final Vector3d weightLocation;
    @Getter
    private final double thrust;
    @Getter
    private final double frictionCoefficient;
    @Getter
    private final double gravityAcceleration;
    @Getter
    private final double airDensity;
    private final Set<FixedComponent> fixedComponents = new HashSet<>();
    @Getter
    private final Set<HingeComponent> hingeComponents = new HashSet<>();

    private static @NotNull Vector3d getVector3d(@NotNull YamlTraverser traverser, String name) {
        List<Double> rotationList = traverser.get(name);
        return new Vector3d(rotationList.get(0), rotationList.get(1), rotationList.get(2));
    }
    private static @NotNull Vector3f getVector3f(@NotNull YamlTraverser traverser, String name) {
        Vector3d as3d = getVector3d(traverser, name);
        return new Vector3f((float) as3d.x, (float) as3d.y, (float) as3d.z);
    }
    private static char getChar(@NotNull YamlTraverser traverser, String name) {
        return ((CharSequence) traverser.get(name)).charAt(0);
    }

    @SuppressWarnings("DataFlowIssue")
    private void processComponentFromTraverser(@NotNull YamlTraverser componentTraverser, @NotNull Map<String, ComponentGroup> groups, Vector3f translation) {
        ComponentGroup group = groups.get(componentTraverser.get("group").toString());
        Material material = Material.valueOf(componentTraverser.get("material"));
        Vector3f size = getVector3f(componentTraverser, "size");
        Vector3f location = getVector3f(componentTraverser, "location").sub(translation);
        Vector3d rotation = getVector3d(componentTraverser, "rotation");
        FixedComponent fixedComponent = new FixedComponent(componentTraverser.name(),
                group.dragCoefficient, group.liftCoefficient, material, size, location, rotation);

        if (componentTraverser.get("controlSurface", false)) {
            hingeComponents.add(new HingeComponent(fixedComponent,
                    getVector3d(componentTraverser, "rotationAxis"), componentTraverser.get("rotationRate"), componentTraverser.get("rotationMax"),
                    getChar(componentTraverser, "keyUp"), getChar(componentTraverser, "keyDown")));
        }

        fixedComponents.add(fixedComponent);
    }

    @SuppressWarnings("DataFlowIssue")
    public VehicleDescription(String path) {
        this.path = path;
        YamlTraverser traverser = new YamlTraverser(Aircraft.getInstance(), path);
        Vector3f translation = getVector3f(traverser, "translation");
        mass = traverser.get("mass");
        momentOfInertia = traverser.get("momentOfInertia");
        velocityDampening = traverser.get("velocityDampening");
        angularVelocityDampening = traverser.get("angularVelocityDampening");
        thrustLocation = getVector3d(traverser, "thrustLocation");
        weightLocation = getVector3d(traverser, "weightLocation");
        thrust = traverser.get("thrust");
        frictionCoefficient = traverser.get("frictionCoefficient");
        gravityAcceleration = traverser.get("gravityAcceleration");
        airDensity = traverser.get("airDensity");

        Map<String, ComponentGroup> groups = new HashMap<>();
        for (YamlTraverser group : traverser.getSection("groups").getSections()) {
            groups.put(group.name(), new ComponentGroup(group.get("dragCoefficient"), group.get("liftCoefficient")));
        }

        for (YamlTraverser componentTraverser : traverser.getSection("components").getSections()) {
            processComponentFromTraverser(componentTraverser, groups, translation);
        }
    }

    @SuppressWarnings("SimplifyForEach")
    public Set<VehicleSurface> getSurfaces(Map<String, ControlSurfaceOrientation> orientations) {
        Set<VehicleSurface> surfaces = new HashSet<>();
        fixedComponents.forEach(component -> surfaces.addAll(component.getSurfaces()));
        hingeComponents.forEach(component -> surfaces.addAll(component.getSurfaces(orientations)));
        return surfaces;
    }

    @SuppressWarnings("SimplifyForEach")
    public Map<String, ModelAdvancedCuboid> getCuboids(Map<String, ControlSurfaceOrientation> orientations) {
        Map<String, ModelAdvancedCuboid> cuboids = new HashMap<>();
        fixedComponents.forEach(component -> cuboids.put(component.getName(), component.getCuboid()));
        hingeComponents.forEach(component -> cuboids.put(component.getName(), component.getCuboid(orientations)));
        return cuboids;
    }

    public void adjustHingeComponents(Map<String, ControlSurfaceOrientation> orientations, char key) {
        hingeComponents.forEach(component -> component.useKey(orientations, key));
    }

    public void moveHingeComponentsToCenter(Map<String, ControlSurfaceOrientation> orientations) {
        hingeComponents.forEach(component -> component.moveTowardsCenter(orientations));
    }

    public Map<String, ControlSurfaceOrientation> initializeOrientations() {
        return hingeComponents.stream().collect(Collectors.toMap(HingeComponent::getName, component -> new ControlSurfaceOrientation(), (name, orientation) -> orientation));
    }

    public void applyVelocityDampening(@NotNull VehicleState state) {
        state.velocity.mul(1.0 - velocityDampening);
    }

    public void applyAngularVelocityDampening(@NotNull VehicleState state) {
        state.angularVelocity.mul(1.0 - angularVelocityDampening);
    }
}
