package org.metamechanists.aircraft.vehicles;

import org.metamechanists.aircraft.utils.id.simple.DisplayGroupId;


public record AircraftGroup(DisplayGroupId componentGroupId, DisplayGroupId forceArrowGroupId) {
    public boolean isValid() {
        return componentGroupId.get().isPresent() && forceArrowGroupId.get().isPresent();
    }
}
