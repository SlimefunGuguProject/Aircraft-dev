package org.metamechanists.aircraft;

import java.util.List;


public record VehicleResource(double capacity, double drainRate, boolean isEngine, List<String> signals) {}
