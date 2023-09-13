package org.metamechanists.aircraft.vehicles;

import lombok.Getter;
import org.bukkit.Material;


public enum ForceType {
    VELOCITY(Material.RED_CONCRETE), // for visualisation
    ANGULAR_VELOCITY(Material.YELLOW_CONCRETE), // for visualisation
    WEIGHT(Material.ORANGE_CONCRETE),
    THRUST(Material.PURPLE_CONCRETE),
    LIFT(Material.LIME_CONCRETE),
    DRAG(Material.CYAN_CONCRETE);

    @Getter
    private final Material material;

    ForceType(final Material material) {
        this.material = material;
    }
}
