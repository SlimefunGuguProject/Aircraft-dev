package org.metamechanists.aircraft.vehicles.config;

import lombok.Getter;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.aircraft.vehicles.components.AerodynamicComponent;
import org.metamechanists.aircraft.vehicles.components.Component;
import org.metamechanists.aircraft.vehicles.components.HingeComponent;
import org.metamechanists.aircraft.vehicles.surfaces.ControlSurfaceOrientation;
import org.metamechanists.aircraft.vehicles.surfaces.VehicleSurface;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedCuboid;
import org.metamechanists.metalib.yaml.YamlTraverser;
import org.metamechanists.metalib.yaml.YamlTraverser.ErrorSetting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class VehicleConfig {
    private record ComponentGroup(double dragCoefficient, double liftCoefficient) {}

    @Getter
    private final String path;
    @Getter
    private final double mass;
    @Getter
    private final double momentOfInertia;
    @Getter
    private final double velocityDamping;
    @Getter
    private final double angularVelocityDamping;
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
    @Getter
    private final double groundPitchDamping;
    @Getter
    private final double groundYawDamping;
    private final Set<AerodynamicComponent> aerodynamicComponents = new HashSet<>();
    private final Set<Component> components = new HashSet<>();

    @SuppressWarnings("DataFlowIssue")
    public VehicleConfig(String path) {
        this.path = path;
        YamlTraverser traverser = new YamlTraverser(Aircraft.getInstance(), path);
        Vector3f translation = Util.getVector3f(traverser, "translation");
        mass = traverser.get("mass");
        momentOfInertia = traverser.get("momentOfInertia", ErrorSetting.LOG_MISSING_KEY);
        velocityDamping = traverser.get("velocityDamping", ErrorSetting.LOG_MISSING_KEY);
        angularVelocityDamping = traverser.get("angularVelocityDamping", ErrorSetting.LOG_MISSING_KEY);
        thrustLocation = Util.getVector3d(traverser, "thrustLocation");
        weightLocation = Util.getVector3d(traverser, "weightLocation");
        thrust = traverser.get("thrust", ErrorSetting.LOG_MISSING_KEY);
        frictionCoefficient = traverser.get("frictionCoefficient", ErrorSetting.LOG_MISSING_KEY);
        gravityAcceleration = traverser.get("gravityAcceleration", ErrorSetting.LOG_MISSING_KEY);
        airDensity = traverser.get("airDensity", ErrorSetting.LOG_MISSING_KEY);
        groundPitchDamping = traverser.get("groundPitchDamping", ErrorSetting.LOG_MISSING_KEY);
        groundYawDamping = traverser.get("groundYawDamping", ErrorSetting.LOG_MISSING_KEY);

        for (YamlTraverser componentTraverser : traverser.getSection("aerodynamicComponents").getSections()) {
            aerodynamicComponents.add(AerodynamicComponent.fromTraverser(componentTraverser, translation));
        }

        for (YamlTraverser componentTraverser : traverser.getSection("components").getSections()) {
            components.add(Component.fromTraverser(componentTraverser, translation));
        }
    }

    @SuppressWarnings("SimplifyForEach")
    public Set<VehicleSurface> getSurfaces(VehicleState state) {
        Set<VehicleSurface> surfaces = new HashSet<>();
        aerodynamicComponents.forEach(component -> surfaces.addAll(component.getSurfaces(state)));
        return surfaces;
    }

    @SuppressWarnings("SimplifyForEach")
    public Map<String, ModelAdvancedCuboid> getCuboids(VehicleState state) {
        Map<String, ModelAdvancedCuboid> cuboids = new HashMap<>();
        components.forEach(component -> cuboids.put(component.getName(), component.getCuboid(state)));
        return cuboids;
    }

    public void onKey(VehicleState state, char key) {
        components.forEach(component -> component.onKey(state, key));
        aerodynamicComponents.forEach(component -> component.getComponent().onKey(state, key));
    }

    public void update(VehicleState state) {
        components.forEach(component -> component.update(state));
        aerodynamicComponents.forEach(component -> component.getComponent().update(state));
    }

    @SuppressWarnings("Convert2streamapi")
    public Map<String, ControlSurfaceOrientation> initializeOrientations() {
        Map<String, ControlSurfaceOrientation> map = new HashMap<>();
        for (Component component : components) {
            if (component instanceof HingeComponent) {
                map.put(component.getName(), new ControlSurfaceOrientation());
            }
        }

        for (AerodynamicComponent component : aerodynamicComponents) {
            if (component.getComponent() instanceof HingeComponent) {
                map.put(component.getComponent().getName(), new ControlSurfaceOrientation());
            }
        }
        return map;
    }
}
