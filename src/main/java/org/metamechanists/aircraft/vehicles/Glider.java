package org.metamechanists.aircraft.vehicles;

import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.utils.id.simple.DisplayGroupId;
import org.metamechanists.aircraft.utils.models.ModelBuilder;
import org.metamechanists.aircraft.utils.models.components.ModelCuboid;


public class Glider extends SlimefunItem {
    public static final SlimefunItemStack GLIDER = new SlimefunItemStack(
            "ACR_GLIDER",
            Material.FEATHER,
            "&4&ljustin don't hurt me",
            "&cpls");

    public Glider(final ItemGroup itemGroup, final SlimefunItemStack item, final RecipeType recipeType, final ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        addItemHandler(onItemUse());
    }

    private @NotNull ItemUseHandler onItemUse() {
        return event -> {
            if (event.getClickedBlock().isPresent()) {
                event.cancel();
                place(event.getClickedBlock().get().getRelative(event.getClickedFace()));
            }
        };
    }

    private void place(final @NotNull Block block) {
        final DisplayGroup displayGroup = new ModelBuilder()
                .add("main", new ModelCuboid()
                        .size(1.5F, 0.5F, 1.5F)
                        .rotation(Math.PI / 2))
                .buildAtBlockCenter(block.getLocation());
        VehicleStorage.add(new DisplayGroupId(displayGroup.getParentUUID()));
    }
}
