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
        double speed = traverser.getDouble("speed");
        if (rotation == null) {
            return;
        }

        rotation.add(new Vector3d(0.0, 0.0, Math.PI / 16));
        speed += 0.002;
        traverser.set("speed", speed);
        traverser.set("rotation", rotation);

        final Vector3d velocity = new Vector3d(1.0, 0.0, 0.0).rotateX(rotation.x).rotateY(rotation.y).rotateZ(rotation.z).mul(speed);

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
