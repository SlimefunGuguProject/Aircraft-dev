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
import org.metamechanists.aircraft.vehicles.Storage;
import org.metamechanists.aircraft.vehicles.Vehicle;
import org.metamechanists.aircraft.vehicles.VehicleState;


public class SteerControl extends SlimefunItem {
    private final int direction;

    public SteerControl(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, int direction) {
        super(itemGroup, item, recipeType, recipe);
        this.direction = direction;
        addItemHandler(onItemUse());
    }

    private @NotNull ItemUseHandler onItemUse() {
        return event -> {
            Player player = event.getPlayer();
            Pig pig = Storage.getPig(player);
            if (pig == null) {
                return;
            }

            VehicleState state = VehicleState.fromPig(pig);
            if (state == null) {
                return;
            }

            Vehicle vehicle = Storage.getVehicle(pig);
            if (vehicle == null) {
                return;
            }

            if (!Vehicle.isOnGround(pig)) {
                return;
            }

            state.angularVelocity.y += direction * vehicle.getConfig().getSteeringSpeed();
            state.write(pig);
        };
    }
}
