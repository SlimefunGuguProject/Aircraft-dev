package org.metamechanists.aircraft.vehicles.surfaces;

import lombok.Getter;
import org.metamechanists.aircraft.utils.Utils;

@Getter
public class ControlSurfaceOrientation {
    private double angle;
    private int ticksUntilReturn;

    public ControlSurfaceOrientation() {}

    public ControlSurfaceOrientation(double angle, int ticksUntilReturn) {
        this.angle = angle;
        this.ticksUntilReturn = ticksUntilReturn;
    }

    public void adjust(double rotationRate, double rotationMax) {
        angle = Utils.clampToRange(angle + rotationRate, -rotationMax, rotationMax);
        ticksUntilReturn = 1;
    }

    public void moveTowardsCenter(double rotationRate) {
        if (ticksUntilReturn > 0) {
            ticksUntilReturn--;
            return;
        }

        if (Math.abs(angle) < rotationRate) {
            angle = 0;
        } else if (angle > 0) {
            angle -= rotationRate;
        } else if (angle < 0) {
            angle += rotationRate;
        }
    }
}
