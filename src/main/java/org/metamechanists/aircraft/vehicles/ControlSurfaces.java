package org.metamechanists.aircraft.vehicles;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ControlSurfaces {
    double aileron1;
    double aileron2;
    double elevator1;
    double elevator2;

    public ControlSurfaces(final double aileron1, final double aileron2, final double elevator1, final double elevator2) {
        this.aileron1 = aileron1;
        this.aileron2 = aileron2;
        this.elevator1 = elevator1;
        this.elevator2 = elevator2;
    }
}