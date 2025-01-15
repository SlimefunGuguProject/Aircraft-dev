package org.metamechanists.aircraft.slimefun;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.aircraft.vehicle.VehicleEntitySchema;


public class VehicleItem extends SlimefunItem {
    public VehicleEntitySchema schema;

    public VehicleItem(String id, SlimefunItemStack item, VehicleEntitySchema schema, ItemGroup itemGroup, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        this.schema = schema;
        addItemHandler(onItemUse());
    }

    private @NotNull ItemUseHandler onItemUse() {
        return event -> {
            Player player = event.getPlayer();
            if (event.getClickedBlock().isPresent() && !player.isInsideVehicle()) {
                if (player.getGameMode() != GameMode.CREATIVE) {
                    player.getInventory().getItemInMainHand().subtract();
                }
                new VehicleEntity(schema, event.getClickedBlock().get(), player);
            }
        };
    }
}
