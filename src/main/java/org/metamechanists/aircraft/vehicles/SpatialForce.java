package org.metamechanists.aircraft.vehicles;

import dev.sefiraat.sefilib.misc.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.models.components.ModelLine;


public record SpatialForce(Vector3d force, Vector3d relativeLocation) {
    public Vector3d getTorqueVector() {
        return new Vector3d(force).cross(relativeLocation);
    }
    public void visualise(final @NotNull Location pigLocation) {
        final Location absoluteLocation = pigLocation.clone().add(Vector.fromJOML(relativeLocation));
        ParticleUtils.drawLine(Particle.WATER_BUBBLE, absoluteLocation, absoluteLocation.clone().add(Vector.fromJOML(force)), 0.1);
    }
}
