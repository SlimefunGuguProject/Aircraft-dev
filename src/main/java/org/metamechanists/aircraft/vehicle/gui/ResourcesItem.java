package org.metamechanists.aircraft.vehicle.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.aircraft.vehicle.VehicleResource;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.Map;


public class ResourcesItem extends AbstractItem {
    private final VehicleEntity vehicleEntity;

    private static final int RESOURCE_BARS = 20;
    private static final String GRAY = "<color:#777777>";
    private static final String EMPTY_BAR = "<color:#444444>";
    private static final String INFO = "<color:#aaaaaa>";

    private static String resourceColor(@NotNull VehicleResource resource) {
        return switch (resource.type()) {
            case VehicleResource.ResourceType.COMBUSTIBLE -> "<color:#e08523>";
            case VehicleResource.ResourceType.WATER -> "<color:#237aed>";
        };
    }

    private static @NotNull String resourceBar(@NotNull VehicleEntity vehicleEntity, @NotNull String name, @NotNull VehicleResource resource) {
        int filledBars = (int) (RESOURCE_BARS * vehicleEntity.remainingResource(name) / resource.capacity());
        int emptyBars = RESOURCE_BARS - filledBars;
        return GRAY + "[" + resourceColor(resource) + "|".repeat(filledBars) + INFO + "|".repeat(emptyBars) + GRAY + "]";
    }

    private static @NotNull String resourceRemaining(@NotNull VehicleEntity vehicleEntity, @NotNull String name, @NotNull VehicleResource resource) {
        double remaining = vehicleEntity.remainingResource(name);
        double capacity = resource.capacity();
        int green = (int) (255 * remaining / capacity);
        int red = 255 - green;
        String color = "<color:#" + Integer.toHexString(red) + Integer.toHexString(green) + "00>";
        return GRAY + "(" + color + Math.round(vehicleEntity.remainingResource(name)) + GRAY + "/" + "<color:#ffffff>" + Math.round(capacity) + GRAY + ")";
    }

    private static @NotNull String resourceLoreLine(@NotNull VehicleEntity vehicleEntity, @NotNull String name, @NotNull VehicleResource resource) {
        String title = switch (resource.type()) {
            case VehicleResource.ResourceType.COMBUSTIBLE -> "Fuel";
            case VehicleResource.ResourceType.WATER -> "Water";
        };

        return Utils.formatMiniMessage(resourceColor(resource)
                + title
                + " "
                + resourceBar(vehicleEntity, name, resource)
                + " "
                + resourceRemaining(vehicleEntity, name, resource));
    }

    public ResourcesItem(@NotNull VehicleEntity vehicleEntity) {
        super();
        this.vehicleEntity = vehicleEntity;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder itemBuilder = new ItemBuilder(Material.CHARCOAL)
                .setDisplayName(Utils.formatMiniMessage("<color:#fc8207>Resources"));

        for (Map.Entry<String, VehicleResource> pair : vehicleEntity.schema().getResources().entrySet()) {
            itemBuilder.addLoreLines(resourceLoreLine(vehicleEntity, pair.getKey(), pair.getValue()));
        }

//        itemBuilder.addLoreLines(resourceLoreLine(INFO + "Refuel using a Maintenance Station."));

        return itemBuilder;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {}
}
