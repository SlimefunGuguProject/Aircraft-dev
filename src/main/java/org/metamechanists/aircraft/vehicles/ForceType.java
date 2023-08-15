package org.metamechanists.aircraft.vehicles;

import lombok.Getter;
import org.bukkit.Material;


public enum ForceType {
    WEIGHT(Material.ORANGE_CONCRETE),
    DRAG(Material.BLUE_CONCRETE),
    LIFT(Material.LIME_CONCRETE),
    VELOCITY(Material.LIME_CONCRETE);

    @Getter
    private final Material material;

    ForceType(final Material material) {
        this.material = material;
    }
}
