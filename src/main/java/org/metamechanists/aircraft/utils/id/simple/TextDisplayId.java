package org.metamechanists.aircraft.utils.id.simple;

import org.bukkit.Bukkit;
import org.bukkit.entity.TextDisplay;
import org.metamechanists.aircraft.utils.id.CustomId;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("unused")
public class TextDisplayId extends CustomId {
    public TextDisplayId() {
        super();
    }
    public TextDisplayId(CustomId id) {
        super(id);
    }
    public TextDisplayId(String uuid) {
        super(uuid);
    }
    public TextDisplayId(UUID uuid) {
        super(uuid);
    }
    @Override
    public Optional<TextDisplay> get() {
        return (Bukkit.getEntity(getUUID()) instanceof TextDisplay textDisplay)
                ? Optional.of(textDisplay)
                : Optional.empty();
    }
}
