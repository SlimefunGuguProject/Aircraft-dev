package org.metamechanists.aircraft.vehicles;

import lombok.Getter;
import org.joml.Vector3d;
import org.metamechanists.aircraft.utils.Utils;

import static org.metamechanists.aircraft.vehicles.Vehicle.MAX_CONTROL_SURFACE_ROTATION;

@Getter
public class ControlSurfaceOrientation {
    private double angle;
    private int ticksUntilReturn;

    public ControlSurfaceOrientation() {}

    public ControlSurfaceOrientation(final double angle, final int ticksUntilReturn) {
        this.angle = angle;
        this.ticksUntilReturn = ticksUntilReturn;
    }

    public void adjust(final double amount) {
        angle = Utils.clampToRange(angle + amount, -MAX_CONTROL_SURFACE_ROTATION, MAX_CONTROL_SURFACE_ROTATION);
        ticksUntilReturn = 1;
    }

    public void moveTowardsCenter(final double amount) {
        if (ticksUntilReturn > 0) {
            ticksUntilReturn--;
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
