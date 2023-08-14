package org.metamechanists.aircraft.vehicles;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class Glider extends SlimefunItem {
    public static final SlimefunItemStack GLIDER = new SlimefunItemStack(
            "ACR_GLIDER",
            Material.GRAY_CONCRETE,
            "&4&ljustin don't hurt me",
            "&cpls");

    public Glider(final ItemGroup itemGroup, final SlimefunItemStack item, final RecipeType recipeType, final ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }
}
