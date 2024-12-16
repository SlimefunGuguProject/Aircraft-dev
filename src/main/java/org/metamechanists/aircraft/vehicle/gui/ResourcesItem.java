package org.metamechanists.aircraft.vehicle.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.aircraft.vehicle.VehicleResource;
import org.metamechanists.kinematiccore.api.item.ItemStackBuilder;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import static org.metamechanists.aircraft.utils.Utils.formatMiniMessage;

public class ResourcesItem extends AbstractItem {
    private final VehicleEntity vehicleEntity;
    private final String resourceName;

    private static final int RESOURCE_BARS = 20;
    private static final String GRAY = "<color:#777777>";
    private static final String EMPTY_BAR = "<color:#444444>";
    private static final String INFO = "<color:#aaaaaa>";

    private static @NotNull String resourceBar(@NotNull VehicleEntity vehicleEntity, @NotNull String name, @NotNull VehicleResource resource) {
        int filledBars = (int) (RESOURCE_BARS * vehicleEntity.remainingResource(name) / resource.capacity());
        int emptyBars = RESOURCE_BARS - filledBars;
        return GRAY + "[" + resource.type().color() + "|".repeat(filledBars) + INFO + "|".repeat(emptyBars) + GRAY + "]";
    }

    private static @NotNull String resourceRemaining(@NotNull VehicleEntity vehicleEntity, @NotNull String name, @NotNull VehicleResource resource) {
        double remaining = vehicleEntity.remainingResource(name);
        double capacity = resource.capacity();
        int green = (int) (255 * remaining / capacity);
        int red = 255 - green;
        String color = "<color:#" + Integer.toHexString(red) + Integer.toHexString(green) + "00>";
        return GRAY + "(" + color + Math.round(vehicleEntity.remainingResource(name)) + GRAY + "/" + "<color:#ffffff>" + Math.round(capacity) + GRAY + ")";
    }

    private static @NotNull String resourceItemName(@NotNull VehicleEntity vehicleEntity, @NotNull String name, @NotNull VehicleResource resource) {
        String title = switch (resource.type()) {
            case VehicleResource.ResourceType.COMBUSTIBLE -> "Fuel";
            case VehicleResource.ResourceType.WATER -> "Water";
        };

        return formatMiniMessage(resource.type().color()
                + title
                + " "
                + resourceBar(vehicleEntity, name, resource)
                + " "
                + resourceRemaining(vehicleEntity, name, resource));
    }

    public ResourcesItem(@NotNull VehicleEntity vehicleEntity, String resourceName) {
        super();
        this.vehicleEntity = vehicleEntity;
        this.resourceName = resourceName;
    }

    @Override
    public ItemProvider getItemProvider() {
        VehicleResource resource = vehicleEntity.schema().getResources().get(resourceName);
        return new ItemBuilder(resource.type().icon())
                .setDisplayName(resourceItemName(vehicleEntity, resourceName, resource))
                .addLoreLines(formatMiniMessage("<color:#999999>" + ItemStackBuilder.RIGHT_ARROW + " Click to refuel"));
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {}
}
