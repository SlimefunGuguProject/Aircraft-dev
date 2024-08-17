package org.metamechanists.aircraft.vehicles;

import lombok.Getter;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.vehicles.components.AerodynamicComponent;
import org.metamechanists.aircraft.vehicles.components.BaseComponent;
import org.metamechanists.aircraft.vehicles.components.Component;
import org.metamechanists.aircraft.vehicles.components.HingeComponent;
import org.metamechanists.aircraft.vehicles.surfaces.ControlSurfaceOrientation;
import org.metamechanists.aircraft.vehicles.surfaces.VehicleSurface;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedCuboid;
import org.metamechanists.displaymodellib.models.components.ModelComponent;
import org.metamechanists.metalib.yaml.YamlTraverser;
import org.metamechanists.metalib.yaml.YamlTraverser.ErrorSetting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class VehicleConfig {
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
    private final double thrustForce;
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
    private final Set<Component> baseComponents = new HashSet<>();

    @SuppressWarnings("DataFlowIssue")
    public VehicleConfig(String path) {
        this.path = path;
        YamlTraverser traverser = new YamlTraverser(Aircraft.getInstance(), path);
        Vector3f translation = traverser.getVector3f("translation", ErrorSetting.LOG_MISSING_KEY);
        mass = traverser.get("mass");
        momentOfInertia = traverser.get("momentOfInertia", ErrorSetting.LOG_MISSING_KEY);
        airDensity = traverser.get("airDensity", ErrorSetting.LOG_MISSING_KEY);
        frictionCoefficient = traverser.get("frictionCoefficient", ErrorSetting.LOG_MISSING_KEY);

        YamlTraverser damping = traverser.getSection("damping", ErrorSetting.LOG_MISSING_KEY);
        velocityDamping = damping.get("velocity", ErrorSetting.LOG_MISSING_KEY);
        angularVelocityDamping = damping.get("angularVelocity", ErrorSetting.LOG_MISSING_KEY);
        groundPitchDamping = damping.get("groundPitch", ErrorSetting.LOG_MISSING_KEY);
        groundYawDamping = damping.get("groundYaw", ErrorSetting.LOG_MISSING_KEY);

        YamlTraverser weight = traverser.getSection("weight", ErrorSetting.LOG_MISSING_KEY);
        weightLocation = weight.getVector3d("location", ErrorSetting.LOG_MISSING_KEY);
        gravityAcceleration = weight.get("acceleration", ErrorSetting.LOG_MISSING_KEY);

        YamlTraverser thrust = traverser.getSection("thrust", ErrorSetting.LOG_MISSING_KEY);
        thrustLocation = thrust.getVector3d("location", ErrorSetting.LOG_MISSING_KEY);
        thrustForce = thrust.get("force", ErrorSetting.LOG_MISSING_KEY);

        for (YamlTraverser componentSectionTraverser : traverser.getSection("aerodynamicComponents").getSections()) {
            for (YamlTraverser componentTraverser : componentSectionTraverser.getSections()) {
                aerodynamicComponents.add(AerodynamicComponent.fromTraverser(componentTraverser, translation));
            }
        }

        for (YamlTraverser componentSectionTraverser : traverser.getSection("components").getSections()) {
            for (YamlTraverser componentTraverser : componentSectionTraverser.getSections()) {
                baseComponents.add(BaseComponent.fromTraverser(componentTraverser, translation));
            }
        }
    }

    @SuppressWarnings("SimplifyForEach")
    public Set<VehicleSurface> getSurfaces(VehicleState state) {
        Set<VehicleSurface> surfaces = new HashSet<>();
        aerodynamicComponents.forEach(component -> surfaces.addAll(component.getSurfaces(state)));
        return surfaces;
    }

    @SuppressWarnings("SimplifyForEach")
    public Map<String, ModelComponent> getCuboids(VehicleState state) {
        Map<String, ModelComponent> cuboids = new HashMap<>();
        baseComponents.forEach(baseComponent -> cuboids.put(baseComponent.getName(), baseComponent.getCuboid(state)));
        return cuboids;
    }

    public void onKey(VehicleState state, char key) {
        baseComponents.forEach(baseComponent -> baseComponent.onKey(state, key));
        aerodynamicComponents.forEach(component -> component.onKey(state, key));
    }

    public void update(VehicleState state) {
        baseComponents.forEach(baseComponent -> baseComponent.update(state));
        aerodynamicComponents.forEach(component -> component.update(state));
    }

    @SuppressWarnings("Convert2streamapi")
    public Map<String, ControlSurfaceOrientation> initializeOrientations() {
        Map<String, ControlSurfaceOrientation> map = new HashMap<>();
        for (Component component : baseComponents) {
            if (component instanceof HingeComponent) {
                map.put(component.getName(), new ControlSurfaceOrientation());
            }
        }

        for (AerodynamicComponent component : aerodynamicComponents) {
            if (component instanceof HingeComponent) {
                map.put(component.getName(), new ControlSurfaceOrientation());
            }
        }
        return map;
    }
}
