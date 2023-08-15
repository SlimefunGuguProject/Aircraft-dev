package org.metamechanists.aircraft.vehicles;

import lombok.experimental.UtilityClass;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;
import org.metamechanists.aircraft.utils.id.simple.DisplayGroupId;
import org.metamechanists.metalib.sefilib.entity.display.DisplayGroup;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@UtilityClass
public class VehicleStorage {
    private Set<DisplayGroupId> activeVehicles = new HashSet<>();

    public void add(final DisplayGroupId id) {
        activeVehicles.add(id);
    }

    private void tick(final @NotNull DisplayGroupId id) {
        final DisplayGroup displayGroup = id.get().get();
        final PersistentDataTraverser traverser = new PersistentDataTraverser(displayGroup.getParentUUID());
        final Vector3d rotation = traverser.getVector3d("rotation");
        final Vector3d velocity = traverser.getVector3d("velocity");
        if (rotation == null || velocity == null) {
            return;
        }

        final double mass = 200;
        final double momentOfInertia = mass; // silly approximation
        final Vector3d centerOfMass = new Vector3d(0.0, 0.0, 0.0);
        final Vector3d weight = new Vector3d(0, -0.5 * mass, 0);

        final Set<SpatialForce> forces = new HashSet<>();
        forces.add(new SpatialForce(weight, centerOfMass));
        forces.addAll(Glider.getSurfaces().stream()
                .map(aircraftSurface -> aircraftSurface.getDragForce(velocity))
                .collect(Collectors.toSet()));
        forces.addAll(Glider.getSurfaces().stream()
                .map(aircraftSurface -> aircraftSurface.getLiftForce(velocity))
                .collect(Collectors.toSet()));
        final Set<Vector3d> torqueVectors = forces.stream().map(SpatialForce::getTorqueVector).collect(Collectors.toSet());


        // Newton's 2nd law to calculate resultant force and then acceleration
        final Vector3d resultantForce = new Vector3d();
        forces.stream().map(SpatialForce::force).forEach(resultantForce::add);
        final Vector3d resultantAcceleration = new Vector3d(resultantForce).div(mass).div(400);

        // Sum torque vectors to find resultant torque
        final Vector3d resultantTorque = new Vector3d();
        torqueVectors.forEach(resultantTorque::add);
        final Vector3d resultantRotation = new Vector3d(resultantTorque).div(momentOfInertia).mul(40);

        velocity.add(resultantAcceleration);
        rotation.add(resultantRotation);

        traverser.set("velocity", velocity);
        traverser.set("rotation", rotation);

        displayGroup.getDisplays().values().forEach(display -> display.getPassengers()
                .forEach(passenger -> passenger.teleportAsync(passenger.getLocation().add(Vector.fromJOML(velocity)))));
        displayGroup.getDisplays().values().forEach(display -> display.teleportAsync(display.getLocation().add(Vector.fromJOML(velocity))));

        try {
            displayGroup.getDisplays().get("main").setTransformationMatrix(Glider.modelMain().getMatrix(rotation));
            displayGroup.getDisplays().get("wing_front_1").setTransformationMatrix(Glider.modelWingFront1().getMatrix(rotation));
            displayGroup.getDisplays().get("wing_front_2").setTransformationMatrix(Glider.modelWingFront2().getMatrix(rotation));
            displayGroup.getDisplays().get("wing_back_1").setTransformationMatrix(Glider.modelWingBack1().getMatrix(rotation));
            displayGroup.getDisplays().get("wing_back_2").setTransformationMatrix(Glider.modelWingBack2().getMatrix(rotation));
            displayGroup.getDisplays().get("rudder").setTransformationMatrix(Glider.modelRudder().getMatrix(rotation));
        } catch (final NullPointerException e) {
            activeVehicles.remove(id);
            e.printStackTrace();
        }
    }

    public void tick() {
        activeVehicles = activeVehicles.stream().filter(id -> id.get().isPresent()).collect(Collectors.toSet());
        for (final DisplayGroupId id : activeVehicles) {
            tick(id);
        }
    }
}
