package org.metamechanists.aircraft.vehicles;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
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
        final Display display = displayGroup.getDisplays().get("main");
        display.teleportAsync(display.getLocation().add(0, -0.01, 0));
    }

    public void tick() {
        for (final DisplayGroupId id : activeVehicles) {
            id.get().ifPresent(VehicleStorage::tick);
        }
    }
}
