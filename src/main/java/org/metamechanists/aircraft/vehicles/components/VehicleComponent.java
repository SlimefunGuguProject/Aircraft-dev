package org.metamechanists.aircraft.vehicles.components;

import org.metamechanists.aircraft.vehicles.VehicleSurface;

import java.util.Collection;
import java.util.Set;


public interface VehicleComponent {
    Set<VehicleSurface> getSurfaces();
}
