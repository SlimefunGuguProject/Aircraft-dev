package org.metamechanists.aircraft.vehicle;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.kinematiccore.api.item.ItemStackBuilder;


public final class VehicleItemStack {

    private VehicleItemStack() {}

    public static ItemStack fromVehicleEntitySchema(@NotNull VehicleEntitySchema schema) {
        ItemStackBuilder builder = new ItemStackBuilder(schema.getMaterial())
                .name(Component.text(schema.getName()).color(NamedTextColor.WHITE))
                .loreLine(ItemStackBuilder.VEHICLE)
                .loreLine("")
                .loreLineWrapped(schema.getDescription())
                .loreLine("");
        for (VehicleEntitySchema.LoreData loreData : schema.getLoreData()) {
            builder = builder.loreLine(loreData.name(), loreData.amount(), loreData.unit());
        }
        return builder.build();
        // speed
        // turn speed
        // vertical speed
        // strafe speed

        // hud components
    }
}
