package org.metamechanists.aircraft.vehicles;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Pig;
import org.metamechanists.aircraft.items.groups.Aircraft;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@UtilityClass
public class VehicleStorage {
    private Set<UUID> pigs = new HashSet<>();

    public void add(final UUID pigId) {
        pigs.add(pigId);
    }

    public void remove(final UUID pigId) {
        pigs.remove(pigId);
    }

    public void tick() {
        pigs = pigs.stream().filter(id -> Bukkit.getEntity(id) != null).collect(Collectors.toSet());
        pigs.stream()
                .map(Bukkit::getEntity)
                .map(Pig.class::cast)
                .filter(pig -> new PersistentDataTraverser(pig).getString("name") != null)
                .forEach(VehicleStorage::tick);
    }

    private void tick(final Pig pig) {
        final String name = new PersistentDataTraverser(pig).getString("name");
        Aircraft.getVehicle(name).tickAircraft(pig);
    }
}
