package org.metamechanists.aircraft.vehicles;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;
import org.metamechanists.aircraft.utils.id.simple.DisplayGroupId;
import org.metamechanists.aircraft.utils.models.ModelBuilder;
import org.metamechanists.aircraft.utils.models.components.ModelLine;
import org.metamechanists.metalib.sefilib.entity.display.DisplayGroup;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@UtilityClass
public class VehicleStorage {
    private Set<DisplayGroupId> activeVehicles = new HashSet<>();
    private Set<DisplayGroupId> activeForceVisuals = new HashSet<>();

    public void add(final DisplayGroupId id) {
        activeVehicles.add(id);
    }

    public void addForceVisual(final @NotNull ModelBuilder forceVisualBuilder, final Material material, final @NotNull Vector3d origin, final @NotNull Vector3d force) {
        final Vector3f originFloat = new Vector3f((float)origin.x, (float)origin.y, (float)origin.z);
        final Vector3f destinationFloat = new Vector3f(originFloat).add((float)force.x, (float)force.y, (float)force.z);
        forceVisualBuilder.add(UUID.randomUUID().toString(), new ModelLine()
                .material(material)
                .brightness(15)
                .from(originFloat)
                .to(destinationFloat)
                .thickness(0.12F));
    }

    private void tick(final @NotNull DisplayGroupId id) {
        final ModelBuilder forceVisualBuilder = new ModelBuilder();
        final DisplayGroup displayGroup = id.get().get();
        final PersistentDataTraverser traverser = new PersistentDataTraverser(displayGroup.getParentUUID());
        final Vector3d velocity = traverser.getVector3d("velocity");
        final Vector3d angularVelocity = traverser.getVector3d("angularVelocity");
        final Vector3d rotation = traverser.getVector3d("rotation");
        if (velocity == null || angularVelocity == null || rotation == null) {
            return;
        }

        final double mass = 1;
        final double momentOfInertia = mass; // silly approximation
        final Vector3d centerOfMass = new Vector3d(0.0, 0.0, 0.0);

        final SpatialForce weight = new SpatialForce(new Vector3d(0, -0.01 * mass, 0), centerOfMass);
        final Set<SpatialForce> dragForces = Glider.getSurfaces().stream()
                .map(aircraftSurface -> aircraftSurface.getDragForce(velocity))
                .collect(Collectors.toSet());
        final Set<SpatialForce> liftForces =  Glider.getSurfaces().stream()
                .map(aircraftSurface -> aircraftSurface.getLiftForce(rotation, velocity))
                .collect(Collectors.toSet());

        addForceVisual(forceVisualBuilder, Material.ORANGE_CONCRETE, weight.relativeLocation(), new Vector3d(weight.force()).mul(500));
        dragForces.forEach(force -> addForceVisual(forceVisualBuilder, Material.BLUE_CONCRETE, force.relativeLocation(), new Vector3d(force.force()).mul(500)));
        liftForces.forEach(force -> addForceVisual(forceVisualBuilder, Material.LIME_CONCRETE, force.relativeLocation(), new Vector3d(force.force()).mul(500)));

        final Set<SpatialForce> forces = new HashSet<>();
        forces.add(weight);
        forces.addAll(dragForces);
        forces.addAll(liftForces);

        final Set<Vector3d> torqueVectors = forces.stream().map(SpatialForce::getTorqueVector).collect(Collectors.toSet());

        // Newton's 2nd law to calculate resultant force and then acceleration
        final Vector3d resultantForce = new Vector3d();
        forces.stream().map(SpatialForce::force).forEach(resultantForce::add);
        final Vector3d resultantAcceleration = new Vector3d(resultantForce).div(mass).div(10);

        // Sum torque vectors to find resultant torque
        final Vector3d resultantTorque = new Vector3d();
        torqueVectors.forEach(resultantTorque::add);
        final Vector3d resultantAngularAcceleration = new Vector3d(resultantTorque).div(momentOfInertia);

        velocity.add(resultantAcceleration);
        angularVelocity.add(resultantAngularAcceleration);
        rotation.add(angularVelocity);

        traverser.set("velocity", velocity);
        traverser.set("angular_velocity", angularVelocity);
        traverser.set("rotation", rotation);

        activeForceVisuals.add(new DisplayGroupId(forceVisualBuilder.buildAtLocation(displayGroup.getLocation()).getParentUUID()));

        try {
            displayGroup.getDisplays().values().forEach(display -> display.getPassengers()
                    .forEach(passenger -> {
                        passenger.teleportAsync(passenger.getLocation().add(Vector.fromJOML(velocity)));
                        display.addPassenger(passenger);
                    }));
            displayGroup.getParentDisplay().teleportAsync(displayGroup.getParentDisplay().getLocation().add(Vector.fromJOML(velocity)));
            displayGroup.getDisplays().values().forEach(display -> display.teleportAsync(display.getLocation().add(Vector.fromJOML(velocity))));
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
        activeForceVisuals.stream()
                .map(DisplayGroupId::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(displayGroup -> {
                    displayGroup.getDisplays().values().forEach(Entity::remove);
                    displayGroup.getParentDisplay().remove();
                });
        activeForceVisuals.clear();
        activeVehicles = activeVehicles.stream().filter(id -> id.get().isPresent()).collect(Collectors.toSet());
        for (final DisplayGroupId id : activeVehicles) {
            tick(id);
        }
    }
}
