package org.metamechanists.aircraft.vehicles;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Interaction;
import org.metamechanists.aircraft.items.groups.Aircraft;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@UtilityClass
public class VehicleStorage {
    private Set<UUID> seatInteractions = new HashSet<>();

    public void add(final UUID seatInteractionId) {
        seatInteractions.add(seatInteractionId);
    }

    public void remove(final UUID seatInteractionId) {
        seatInteractions.remove(seatInteractionId);
    }

    public void tick() {
        seatInteractions = seatInteractions.stream().filter(id -> Bukkit.getEntity(id) != null).collect(Collectors.toSet());
        seatInteractions.stream()
                .map(Bukkit::getEntity)
                .map(Interaction.class::cast)
                .filter(seatInteraction -> new PersistentDataTraverser(seatInteraction).getString("name") != null)
                .forEach(VehicleStorage::tick);
    }

    private void tick(final Interaction seatInteraction) {
        final String name = new PersistentDataTraverser(seatInteraction).getString("name");
        Aircraft.getVehicle(name).tickAircraft(seatInteraction);
    }
}
