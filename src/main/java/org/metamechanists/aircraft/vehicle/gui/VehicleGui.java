package org.metamechanists.aircraft.vehicle.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.window.Window;


public final class VehicleGui {
    private VehicleGui() {}

    public static void show(@NotNull VehicleEntity vehicleEntity, @NotNull Player player) {
        Gui gui = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # i # # # #",
                        "# # # # # # # # #",
                        "# 1 2 # # # # # #",
                        "# # # # # # # # #")
                .addIngredient('#', new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE))
                .addIngredient('i', vehicleEntity.schema().getItemStack())
                .addIngredient('1', () -> new FlyItem(vehicleEntity, player))
                .addIngredient('2', () -> new PickUpItem(vehicleEntity, player))
                .build();

        Window.single()
                .setViewer(player)
                .setTitle(vehicleEntity.schema().getItemStack().getItemMeta().getDisplayName())
                .setGui(gui)
                .build()
                .open();
        // customise, resources, passengers, cargo, fly, permissions, pick up,
    }
}
