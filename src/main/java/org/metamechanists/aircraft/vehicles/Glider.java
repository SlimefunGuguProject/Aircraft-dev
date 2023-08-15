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

    private static @NotNull ItemUseHandler onItemUse() {
        return event -> {
            if (event.getClickedBlock().isPresent()) {
                event.cancel();
                place(event.getClickedBlock().get().getRelative(event.getClickedFace()));
            }
        };
    }

    private static void place(final @NotNull Block block) {
        final DisplayGroup displayGroup = new ModelBuilder()
                .add("main", new ModelCuboid()
                        .material(Material.WHITE_CONCRETE)
                        .size(2.0F, 0.5F, 0.5F))
                .add("wing_front_1", new ModelCuboid()
                        .material(Material.GRAY_CONCRETE)
                        .size(0.6F, 0.1F, 0.8F)
                        .location(0.7F, 0.2F, 0.4F))
                .add("wing_front_2", new ModelCuboid()
                        .material(Material.GRAY_CONCRETE)
                        .size(0.6F, 0.1F, 0.8F)
                        .location(0.7F, 0.2F, -0.4F))
                .add("wing_back_1", new ModelCuboid()
                        .material(Material.BLUE_CONCRETE)
                        .size(0.4F, 0.1F, 0.6F)
                        .location(-0.8F, 0.2F, 0.3F))
                .add("wing_back_2", new ModelCuboid()
                        .material(Material.BLUE_CONCRETE)
                        .size(0.4F, 0.1F, 0.6F)
                        .location(-0.8F, 0.2F, -0.3F))
                .add("rudder", new ModelCuboid()
                        .material(Material.BLUE_CONCRETE)
                        .size(0.4F, 0.6F, 0.1F)
                        .location(-0.8F, 0.5F, 0.0F))
                .buildAtBlockCenter(block.getLocation());
        VehicleStorage.add(new DisplayGroupId(displayGroup.getParentUUID()));
    }
}
