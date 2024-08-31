package org.metamechanists.aircraft.vehicles;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.metamechanists.aircraft.items.Items;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;
import org.metamechanists.displaymodellib.models.components.ModelComponent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@UtilityClass
public class Storage {
    private Set<UUID> pigs = new HashSet<>();
    private final Map<UUID, Map<String, ModelComponent>> previousAircraftComponents = new HashMap<>();
    private final Map<UUID, Map<String, ModelComponent>> previousHudComponents = new HashMap<>();

    public @Nullable Pig get(UUID uuid) {
        Entity entity = Bukkit.getEntity(uuid);
        if (entity == null) {
            return null;
        }

        if (!(entity instanceof Pig pig)) {
            return null;
        }

        if (!has(pig.getUniqueId())) {
            return null;
        }

        return pig;
    }

    public void add(UUID pigId) {
        pigs.add(pigId);
    }

    public void remove(UUID pigId) {
        pigs.remove(pigId);
    }

    public boolean has(UUID pigId) {
        return pigs.contains(pigId);
    }

    public void tick() {
        pigs = pigs.stream().filter(id -> Bukkit.getEntity(id) != null).collect(Collectors.toSet());
        for (UUID uuid : pigs) {
            Entity entity = Bukkit.getEntity(uuid);
            if (!(entity instanceof Pig pig)) {
                return;
            }

            if (new PersistentDataTraverser(pig).getString("id") == null) {
                continue;
            }

            tick(pig);
        }
    }

    private void tick(Pig pig) {
        String name = new PersistentDataTraverser(pig).getString("id");
        if (name == null) {
            return;
        }

        Items.getVehicle(name).tickAircraft(pig);
    }

    public @Nullable Pig getPig(@NotNull Player player) {
        Entity entity = player.getVehicle();
        if (entity == null) {
            return null;
        }

        if (!(entity instanceof Pig pig)) {
            return null;
        }

        if (!has(pig.getUniqueId())) {
            return null;
        }

        return pig;
    }

    public @Nullable Vehicle getVehicle(@NotNull Player player) {
        Pig pig = getPig(player);
        if (pig == null) {
            return null;
        }

        return getVehicle(pig);
    }

    public @Nullable Vehicle getVehicle(@NotNull Pig pig) {
        PersistentDataTraverser traverser = new PersistentDataTraverser(pig);
        String name = traverser.getString("id");
        if (name == null) {
            return null;
        }

        return Items.getVehicle(name);
    }

    public void updatePreviousComponents(@NotNull Pig pig,
                                         @NotNull Map<String, ModelComponent> newPreviousAircraftComponents,
                                         @NotNull Map<String, ModelComponent> newPreviousHudComponents) {
        previousAircraftComponents.put(pig.getUniqueId(), newPreviousAircraftComponents);
        previousHudComponents.put(pig.getUniqueId(), newPreviousHudComponents);
    }

    public Map<String, ModelComponent> previousAircraftComponents(@NotNull Pig pig) {
        return previousAircraftComponents.get(pig.getUniqueId());
    }

    public Map<String, ModelComponent> previousHudComponents(@NotNull Pig pig) {
        return previousHudComponents.get(pig.getUniqueId());
    }
}
