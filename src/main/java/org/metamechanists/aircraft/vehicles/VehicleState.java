package org.metamechanists.aircraft.vehicles;

import org.bukkit.entity.Pig;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;
import org.metamechanists.aircraft.utils.id.simple.DisplayGroupId;
import org.metamechanists.aircraft.vehicles.surfaces.ControlSurfaceOrientation;
import org.metamechanists.displaymodellib.sefilib.entity.display.DisplayGroup;

import java.util.Map;


public class VehicleState {
    public int throttle;
    public Vector3d velocity;
    public Quaterniond rotation;
    public Vector3d angularVelocity;
    public Map<String, ControlSurfaceOrientation> orientations;
    public DisplayGroup componentGroup;
    public DisplayGroup hudGroup;

    public VehicleState(int throttle, Vector3d velocity, Quaterniond rotation, Vector3d angularVelocity,
                        Map<String, ControlSurfaceOrientation> orientations, DisplayGroup componentGroup, DisplayGroup hudGroup) {
        this.throttle = throttle;
        this.velocity = velocity;
        this.rotation = rotation;
        this.angularVelocity = angularVelocity;
        this.orientations = orientations;
        this.componentGroup = componentGroup;
        this.hudGroup = hudGroup;
    }

    public static @Nullable VehicleState fromPig(Pig pig) {
        PersistentDataTraverser traverser = new PersistentDataTraverser(pig);
        int throttle = traverser.getInt("throttle");
        Vector3d velocity = traverser.getVector3d("velocity");
        Quaterniond rotation = traverser.getQuaterniond("rotation");
        Vector3d angularVelocity = traverser.getVector3d("angularVelocity");
        Map<String, ControlSurfaceOrientation> orientations = traverser.getControlSurfaceOrientations("orientations");
        DisplayGroupId componentGroupId = traverser.getDisplayGroupId("componentGroupId");
        DisplayGroupId hudGroupId = traverser.getDisplayGroupId("hudGroupId");
        if (velocity == null || angularVelocity == null || rotation == null || orientations == null
                || componentGroupId == null || componentGroupId.get().isEmpty()
                || hudGroupId == null || hudGroupId.get().isEmpty()) {
            return null;
        }

        DisplayGroup componentGroup = componentGroupId.get().get();
        DisplayGroup hudGroup = hudGroupId.get().get();

        return new VehicleState(throttle, velocity, rotation, angularVelocity, orientations, componentGroup, hudGroup);
    }

    public void write(Pig pig) {
        PersistentDataTraverser traverser = new PersistentDataTraverser(pig);
        traverser.set("throttle", throttle);
        traverser.set("velocity", velocity);
        traverser.set("rotation", rotation);
        traverser.set("angularVelocity", angularVelocity);
        traverser.setControlSurfaceOrientations("orientations", orientations);
        traverser.set("componentGroupId", componentGroup.getParentUUID());
        traverser.set("hudGroupId", hudGroup.getParentUUID());
    }

    public double roll() {
        Vector3d rotationVector = new Vector3d();
        rotation.getEulerAnglesXYZ(rotationVector);
        return rotationVector.x;
    }

    public double pitch() {
        Vector3d rotationVector = new Vector3d();
        rotation.getEulerAnglesXYZ(rotationVector);
        return rotationVector.z;
    }

    public double yaw() {
        Vector3d rotationVector = new Vector3d();
        rotation.getEulerAnglesXYZ(rotationVector);
        return rotationVector.y;
    }
}
