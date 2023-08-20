package org.metamechanists.aircraft.vehicles;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Pig;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@UtilityClass
public class VehicleStorage {
    private Set<UUID> groups = new HashSet<>();

    public void add(final UUID pigId) {
        groups.add(pigId);
    }

    public void remove(final UUID pigId) {
        groups.remove(pigId);
    }

    public void tick() {
        groups = groups.stream().filter(id -> Bukkit.getEntity(id) != null).collect(Collectors.toSet());
        groups.stream().map(Bukkit::getEntity).map(Pig.class::cast).forEach(Glider::tickAircraft);
    }
}
