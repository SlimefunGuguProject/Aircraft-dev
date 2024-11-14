package org.metamechanists.aircraft.vehicle.gui.main;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;


public class FuelItem extends AbstractItem {
    private final VehicleEntity vehicleEntity;

    public FuelItem(@NotNull VehicleEntity vehicleEntity) {
        super();
        this.vehicleEntity = vehicleEntity;
    }

    @Override
    public ItemProvider getItemProvider() {
        return new ItemBuilder(Material.CHARCOAL)
                .setDisplayName(Utils.formatMiniMessage("<color:d16c00>Fuel"));
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        if (vehicleEntity.canPickUp(player)) {
            player.closeInventory();
            vehicleEntity.pickUp();
        }
    }
}
