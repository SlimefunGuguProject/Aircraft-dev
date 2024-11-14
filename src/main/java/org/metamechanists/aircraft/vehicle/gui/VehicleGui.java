package org.metamechanists.aircraft.vehicle.gui;

import org.bukkit.entity.Player;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import xyz.xenondevs.invui.gui.Gui;


public class VehicleGui {
    public static void show(VehicleEntity vehicleEntity, Player player) {
        Gui gui = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # i # # # #",
                        "# # # # # # # # #",
                        "# 1 2 3 4 5 6 7 #",
                        "# # # # # # # # #"
                )
                .addIngredient('i', vehicleEntity.schema().geti)
                // customise, resources, passengers, cargo, fly, permissions, pick up,
    }
}
