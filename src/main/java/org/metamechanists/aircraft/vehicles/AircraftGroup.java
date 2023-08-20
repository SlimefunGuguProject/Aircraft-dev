package org.metamechanists.aircraft.vehicles;

import org.bukkit.Bukkit;
import org.metamechanists.aircraft.utils.id.simple.DisplayGroupId;

import java.util.UUID;


public record AircraftGroup(UUID pigId, DisplayGroupId componentGroupId, DisplayGroupId forceArrowGroupId) {
    public boolean isValid() {
        return Bukkit.getEntity(pigId) != null && componentGroupId.get().isPresent() && forceArrowGroupId.get().isPresent();
    }
}
