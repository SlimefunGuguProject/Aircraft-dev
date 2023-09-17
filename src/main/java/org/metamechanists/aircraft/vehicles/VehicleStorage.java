package org.metamechanists.aircraft.vehicles;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Horse;
import org.metamechanists.aircraft.items.groups.Aircraft;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@UtilityClass
public class VehicleStorage {
    private Set<UUID> horses = new HashSet<>();

    public void add(final UUID horseId) {
        horses.add(horseId);
    }

    public void remove(final UUID horseId) {
        horses.remove(horseId);
    }

    public void tick() {
        horses = horses.stream().filter(id -> Bukkit.getEntity(id) != null).collect(Collectors.toSet());
        horses.stream()
                .map(Bukkit::getEntity)
                .map(Horse.class::cast)
                .filter(horse -> new PersistentDataTraverser(horse).getString("name") != null)
                .forEach(VehicleStorage::tick);
    }

    private void tick(final Horse horse) {
        final String name = new PersistentDataTraverser(horse).getString("name");
        Aircraft.getVehicle(name).tickAircraft(horse);
    }
}
