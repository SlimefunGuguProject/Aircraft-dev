package org.metamechanists.aircraft.vehicles.components;

import org.metamechanists.aircraft.utils.models.ModelCuboid;
import org.metamechanists.aircraft.vehicles.VehicleSurface;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public interface VehicleComponent {
    String getName();
    Set<VehicleSurface> getSurfaces();
    ModelCuboid getCuboid();
}
