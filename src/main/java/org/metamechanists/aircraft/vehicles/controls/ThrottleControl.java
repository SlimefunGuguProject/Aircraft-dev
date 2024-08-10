package org.metamechanists.aircraft.vehicles.controls;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;
import org.metamechanists.aircraft.vehicles.Storage;


public class ThrottleControl extends SlimefunItem {
    private static final int MIN_THROTTLE = 0;
    private static final int MAX_THROTTLE = 100;
    private final int increment;

    public ThrottleControl(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, int increment) {
        super(itemGroup, item, recipeType, recipe);
        this.increment = increment;
        addItemHandler(onItemUse());
    }

    private @NotNull ItemUseHandler onItemUse() {
        return event -> {
            Player player = event.getPlayer();
            Pig pig = Storage.getPig(player);
            if (pig == null) {
                return;
            }

            PersistentDataTraverser traverser = new PersistentDataTraverser(pig);
            int newThrottle = traverser.getInt("throttle") + increment;
            if (newThrottle >= MIN_THROTTLE && newThrottle <= MAX_THROTTLE) {
                traverser.set("throttle", newThrottle);
            }
        };
    }
}
