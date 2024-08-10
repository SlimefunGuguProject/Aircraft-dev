package org.metamechanists.aircraft.utils.id.simple;

import org.metamechanists.aircraft.utils.id.CustomId;
import org.metamechanists.displaymodellib.sefilib.entity.display.DisplayGroup;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("unused")
public class DisplayGroupId extends CustomId {
    public DisplayGroupId() {
        super();
    }
    public DisplayGroupId(CustomId id) {
        super(id);
    }
    public DisplayGroupId(String uuid) {
        super(uuid);
    }
    public DisplayGroupId(UUID uuid) {
        super(uuid);
    }
    @Override
    public Optional<DisplayGroup> get() {
        return Optional.ofNullable(DisplayGroup.fromUUID(getUUID()));
    }
}
