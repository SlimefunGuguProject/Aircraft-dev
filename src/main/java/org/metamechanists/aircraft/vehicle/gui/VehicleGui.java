package org.metamechanists.aircraft.vehicle.gui;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;


public class VehicleGui {
    public static void show(@NotNull VehicleEntity vehicleEntity, @NotNull Player player) {
        Gui gui = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # i # # # #",
                        "# # # # # # # # #",
                        "# 1 # # # # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('i', vehicleEntity.schema().getItemStack())
                .addIngredient('1', vehicleEntity.schema().getItemStack())
                .build();
        Window.single()
                .setViewer(player)
                .setTitle(vehicleEntity.schema().idWithoutNamespace())
                .setGui(gui)
                .build()
                .open();
        // customise, resources, passengers, cargo, fly, permissions, pick up,
    }
}
