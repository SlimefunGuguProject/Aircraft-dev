package org.metamechanists.aircraft.utils.id.simple;

import org.bukkit.Bukkit;
import org.bukkit.entity.BlockDisplay;
import org.metamechanists.aircraft.utils.id.CustomId;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("unused")
public class BlockDisplayId extends CustomId {
    public BlockDisplayId() {
        super();
    }
    public BlockDisplayId(CustomId id) {
        super(id);
    }
    public BlockDisplayId(String uuid) {
        super(uuid);
    }
    public BlockDisplayId(UUID uuid) {
        super(uuid);
    }
    @Override
    public Optional<BlockDisplay> get() {
        return Bukkit.getEntity(getUUID()) instanceof BlockDisplay blockDisplay
                ? Optional.of(blockDisplay)
                : Optional.empty();
    }
}
