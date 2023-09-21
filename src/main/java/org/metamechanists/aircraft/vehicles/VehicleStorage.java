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
    private Set<UUID> seats = new HashSet<>();

    public void add(final UUID seatId) {
        seats.add(seatId);
    }

    public void remove(final UUID seatId) {
        seats.remove(seatId);
    }

    public void tick() {
        seats = seats.stream().filter(id -> Bukkit.getEntity(id) != null).collect(Collectors.toSet());
        seats.stream()
                .map(Bukkit::getEntity)
                .map(Pig.class::cast)
                .filter(seat -> new PersistentDataTraverser(seat).getString("name") != null)
                .forEach(VehicleStorage::tick);
    }

    private void tick(final Pig seat) {
        final String name = new PersistentDataTraverser(seat).getString("name");
        Aircraft.getVehicle(name).tickAircraft(seat);
    }
}
