package org.metamechanists.aircraft.vehicles;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;
import org.metamechanists.aircraft.utils.id.simple.DisplayGroupId;
import org.metamechanists.metalib.sefilib.entity.display.DisplayGroup;

import java.util.HashSet;
import java.util.Set;


@UtilityClass
public class VehicleStorage {
    private final Set<DisplayGroupId> activeVehicles = new HashSet<>();

    public void add(final DisplayGroupId id) {
        activeVehicles.add(id);
    }

    private void tick(final @NotNull DisplayGroup displayGroup) {
        displayGroup.getDisplays().values().forEach(display -> display.teleportAsync(display.getLocation().add(-0.04, 0, 0)));
        final PersistentDataTraverser traverser = new PersistentDataTraverser(displayGroup.getParentUUID());
        final Vector3d rotation = traverser.getVector3d("rotation");
        if (rotation == null) {
            return;
        }

        rotation.add(new Vector3d(0.0, Math.PI / 4, Math.PI / 16));
        traverser.set("rotation", rotation);

        displayGroup.getDisplays().get("main").setTransformationMatrix(Glider.modelMain().getMatrix(rotation));
        displayGroup.getDisplays().get("wing_front_1").setTransformationMatrix(Glider.modelWingFront1().getMatrix(rotation));
        displayGroup.getDisplays().get("wing_front_2").setTransformationMatrix(Glider.modelWingFront2().getMatrix(rotation));
        displayGroup.getDisplays().get("wing_back_1").setTransformationMatrix(Glider.modelWingBack1().getMatrix(rotation));
        displayGroup.getDisplays().get("wing_back_2").setTransformationMatrix(Glider.modelWingBack2().getMatrix(rotation));
        displayGroup.getDisplays().get("rudder").setTransformationMatrix(Glider.modelRudder().getMatrix(rotation));
    }

    public void tick() {
        for (final DisplayGroupId id : activeVehicles) {
            id.get().ifPresent(VehicleStorage::tick);
        }
    }
}
