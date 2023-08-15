package org.metamechanists.aircraft.vehicles;

import org.joml.Vector3d;


public record SpatialForce(Vector3d force, Vector3d relativeLocation) {}
