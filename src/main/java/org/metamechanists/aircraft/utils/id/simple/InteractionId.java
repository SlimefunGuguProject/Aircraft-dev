package org.metamechanists.aircraft.utils.id.simple;

import org.bukkit.Bukkit;
import org.bukkit.entity.Interaction;
import org.metamechanists.aircraft.utils.id.CustomId;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("unused")
public class InteractionId extends CustomId {
    public InteractionId() {
        super();
    }
    public InteractionId(CustomId id) {
        super(id);
    }
    public InteractionId(String uuid) {
        super(uuid);
    }
    public InteractionId(UUID uuid) {
        super(uuid);
    }
    @Override
    public Optional<Interaction> get() {
        return (Bukkit.getEntity(getUUID()) instanceof Interaction interaction)
                ? Optional.of(interaction)
                : Optional.empty();
    }
}
