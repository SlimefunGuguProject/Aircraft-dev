package org.metamechanists.aircraft.vehicles;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;
import org.metamechanists.aircraft.items.Items;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@UtilityClass
public class VehicleStorage {
    private Set<UUID> seats = new HashSet<>();

    public void add(UUID seatId) {
        seats.add(seatId);
    }

    public void remove(UUID seatId) {
        seats.remove(seatId);
    }

    public boolean has(UUID seatId) {
        return seats.contains(seatId);
    }

    public void tick() {
        seats = seats.stream().filter(id -> Bukkit.getEntity(id) != null).collect(Collectors.toSet());
        for (UUID seat : seats) {
            Entity entity = Bukkit.getEntity(seat);
            if (!(entity instanceof Pig pig)) {
                return;
            }

            if (new PersistentDataTraverser(pig).getString("name") == null) {
                continue;
            }
            tick(pig);
        }
    }

    private void tick(Pig seat) {
        String name = new PersistentDataTraverser(seat).getString("name");
        if (name == null) {
            return;
        }

        Items.getVehicle(name).tickAircraft(seat);
    }
}
