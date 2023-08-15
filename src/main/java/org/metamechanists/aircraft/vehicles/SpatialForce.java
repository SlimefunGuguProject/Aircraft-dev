package org.metamechanists.aircraft.vehicles;

import lombok.Getter;
import org.joml.Vector3d;


@Getter
public record SpatialForce(Vector3d force, Vector3d relativeLocation) {}
