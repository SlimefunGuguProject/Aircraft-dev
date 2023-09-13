package org.metamechanists.aircraft.vehicles;

import lombok.Getter;
import org.metamechanists.aircraft.utils.Utils;

import static org.metamechanists.aircraft.vehicles.Glider.MAX_CONTROL_SURFACE_ROTATION;

@Getter
public class ControlSurface {
    private double angle;
    private boolean modifiedLastTick;

    public ControlSurface(final double angle, final boolean modifiedLastTick) {
        this.angle = angle;
        this.modifiedLastTick = modifiedLastTick;
    }

    public void adjust(final double amount) {
        angle += Utils.clampToRange(angle + amount, -MAX_CONTROL_SURFACE_ROTATION, MAX_CONTROL_SURFACE_ROTATION);
        modifiedLastTick = false;
    }

    public void moveTowardsCenter(final double amount) {
        if (modifiedLastTick) {
            modifiedLastTick = false;
            return;
        }

        if (Math.abs(angle) < amount) {
            angle = 0;
        } else if (angle > 0) {
            angle -= amount;
        } else if (angle < 0) {
            angle += amount;
        }
    }
}
