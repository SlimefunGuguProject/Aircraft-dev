package org.metamechanists.aircraft.slimefun;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.aircraft.vehicle.VehicleEntitySchema;
import org.metamechanists.kinematiccore.api.item.ItemStackBuilder;


public class VehicleItem extends SlimefunItem {
    private final VehicleEntitySchema schema;

    public VehicleItem(String id, VehicleEntitySchema schema, ItemGroup itemGroup, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, new SlimefunItemStack(id, fromVehicleEntitySchema(schema)), recipeType, recipe);
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

    private static ItemStack fromVehicleEntitySchema(@NotNull VehicleEntitySchema schema) {
        ItemStackBuilder builder = new ItemStackBuilder(schema.getMaterial())
                .name(Component.text(schema.getName())
                        .color(NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false))
                .loreLine(ItemStackBuilder.VEHICLE)
                .loreLine("")
                .loreLine(schema.getDescription())
                .loreLine("");
        for (VehicleEntitySchema.LoreData loreData : schema.getLoreData()) {
            builder = builder.loreLine(loreData.name(), loreData.amount(), loreData.unit());
        }
        return builder.build();

        // hud components
    }
}
