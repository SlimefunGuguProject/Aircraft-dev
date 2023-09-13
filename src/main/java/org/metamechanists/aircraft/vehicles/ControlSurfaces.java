package org.metamechanists.aircraft.vehicles;

public class ControlSurfaces {
    public ControlSurface aileron1;
    public ControlSurface aileron2;
    public ControlSurface elevator1;
    public ControlSurface elevator2;

    public ControlSurfaces() {}

    public ControlSurfaces(final ControlSurface aileron1, final ControlSurface aileron2, final ControlSurface elevator1, final ControlSurface elevator2) {
        this.aileron1 = aileron1;
        this.aileron2 = aileron2;
        this.elevator1 = elevator1;
        this.elevator2 = elevator2;
    }
}