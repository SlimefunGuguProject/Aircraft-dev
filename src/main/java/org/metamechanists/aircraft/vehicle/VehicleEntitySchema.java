package org.metamechanists.aircraft.vehicle;

import lombok.Getter;
import org.bukkit.entity.Pig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.vehicle.component.base.VehicleComponent;
import org.metamechanists.aircraft.vehicle.component.hud.compass.Compass;
import org.metamechanists.aircraft.vehicle.component.hud.horizon.Horizon;
import org.metamechanists.kinematiccore.api.entity.KinematicEntitySchema;
import org.metamechanists.kinematiccore.api.storage.EntitySchemas;
import org.metamechanists.metalib.yaml.YamlTraverser;

import java.util.HashMap;
import java.util.Map;


@Getter
public class VehicleEntitySchema extends KinematicEntitySchema {
    private final double mass;
    private final double momentOfInertia;
    private final double velocityDamping;
    private final double angularVelocityDamping;
    private final Vector3d thrustLocation;
    private final Vector3d weightLocation;
    private final double thrustForce;
    private final double frictionCoefficient;
    private final double steeringSpeed;
    private final double gravityAcceleration;
    private final double airDensity;
    private final double groundRollDamping;
    private final double groundPitchDamping;
    private final Map<String, VehicleComponent.VehicleComponentSchema> components = new HashMap<>();
    private final @Nullable Horizon.HorizonSchema horizonSchema;
    private final @Nullable Compass.CompassSchema compassSchema;

    @SuppressWarnings("DataFlowIssue")
    public VehicleEntitySchema(@NotNull String id) {
        super(id, Aircraft.class, VehicleEntity.class, Pig.class);

        YamlTraverser traverser = new YamlTraverser(Aircraft.getInstance(), "vehicles/" + id + ".yml");
        Vector3f translation = traverser.getVector3f("translation");
        mass = traverser.get("mass");
        momentOfInertia = traverser.get("momentOfInertia");
        airDensity = traverser.get("airDensity");
        frictionCoefficient = traverser.get("frictionCoefficient");
        steeringSpeed = traverser.get("steeringSpeed");

        YamlTraverser damping = traverser.getSection("damping");
        velocityDamping = damping.get("velocity");
        angularVelocityDamping = damping.get("angularVelocity");
        groundRollDamping = damping.get("groundRoll");
        groundPitchDamping = damping.get("groundPitch");

        YamlTraverser weight = traverser.getSection("weight");
        weightLocation = weight.getVector3d("location");
        gravityAcceleration = weight.get("acceleration");

        YamlTraverser thrust = traverser.getSection("thrust");
        thrustLocation = thrust.getVector3d("location");
        thrustForce = thrust.get("force");

        for (YamlTraverser componentSectionTraverser : traverser.getSection("components").getSections()) {
            for (YamlTraverser componentTraverser : componentSectionTraverser.getSections()) {
                String componentId = id + "_" + componentTraverser.path();
                VehicleComponent.VehicleComponentSchema schema = VehicleComponent.VehicleComponentSchema.fromTraverser(
                        componentId, componentTraverser, translation, false);
                components.put(schema.getId(), schema);
                if (componentTraverser.get("mirror", false)) {
                    VehicleComponent.VehicleComponentSchema mirroredSchema = VehicleComponent.VehicleComponentSchema.fromTraverser(
                            componentId, componentTraverser, translation, true);
                    components.put(mirroredSchema.getId(), mirroredSchema);
                }
            }
        }

        YamlTraverser hudTraverser = traverser.getSection("hud");

        YamlTraverser horizonSection = hudTraverser.getSection("horizon", YamlTraverser.ErrorSetting.NO_BEHAVIOUR);
        if (horizonSection == null) {
            horizonSchema = null;
        } else {
            horizonSchema = new Horizon.HorizonSchema(id + "_horizon", horizonSection);
        }

        YamlTraverser compassSection = hudTraverser.getSection("compass", YamlTraverser.ErrorSetting.NO_BEHAVIOUR);
        if (compassSection == null) {
            compassSchema = null;
        } else {
            compassSchema  = new Compass.CompassSchema(id + "_compass", compassSection);
        }
    }

    @Override
    public void unregister() {
        super.unregister();
        for (VehicleComponent.VehicleComponentSchema schema : components.values()) {
            EntitySchemas.unregister(schema.getId());
        }
        if (horizonSchema != null) {
            horizonSchema.unregister();
        }
        EntitySchemas.unregister(getId());
    }
}
