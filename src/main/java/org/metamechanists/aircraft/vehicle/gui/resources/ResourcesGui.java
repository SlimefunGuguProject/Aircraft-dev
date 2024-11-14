package org.metamechanists.aircraft.vehicle.gui.resources;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.aircraft.vehicle.VehicleResource;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;


public final class ResourcesGui {
    private ResourcesGui() {}

    private static final int RESOURCE_BARS = 20;
    private static final String GRAY = "<color:777777>";
    private static final String EMPTY_BAR = "<color:444444>";
    private static final String INFO = "<color:aaaaaa>";

    private static String resourceColor(@NotNull VehicleResource resource) {
        return switch (resource.type()) {
            case VehicleResource.ResourceType.COMBUSTIBLE -> "<color:e08523>";
            case VehicleResource.ResourceType.WATER -> "<color:237aed>";
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
        return GRAY + "(<color:" + red + green + "00>" + vehicleEntity.remainingResource(name) + GRAY + "/" + "<color:ffffff>" + capacity + GRAY + ")";
    }

    private static @NotNull Item item(@NotNull VehicleEntity vehicleEntity, @NotNull String name, @NotNull VehicleResource resource) {
        return new SimpleItem(switch (resource.type()) {
            case VehicleResource.ResourceType.COMBUSTIBLE -> new ItemBuilder(Material.CHARCOAL)
                    .setDisplayName(Utils.formatMiniMessage(resourceColor(resource) + "Fuel "
                            + resourceBar(vehicleEntity, name, resource)
                            + " "
                            + resourceRemaining(vehicleEntity, name, resource)))
                    .addLoreLines("", INFO + "\u2193 Place fuel here \u2193");

            case VehicleResource.ResourceType.WATER -> new ItemBuilder(Material.ICE)
                    .setDisplayName(Utils.formatMiniMessage(resourceColor(resource) + "Water "
                            + resourceBar(vehicleEntity, name, resource)
                            + " "
                            + resourceRemaining(vehicleEntity, name, resource)))
                    .addLoreLines("", INFO + "\u2193 Place water here \u2193");
        });
    }

    public static void show(@NotNull VehicleEntity vehicleEntity, @NotNull Player player) {
        Gui gui = Gui.empty(9, 5);
        gui.fill(new SimpleItem(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)), false);

        int i = 1;
        for (String name : vehicleEntity.schema().getResources().keySet()) {
            gui.setItem(i, 1, item(vehicleEntity, name, vehicleEntity.schema().getResources().get(name)));
            i++;
        }


        Window.single()
                .setViewer(player)
                .setTitle(vehicleEntity.schema().getItemStack().getItemMeta().getDisplayName())
                .setGui(gui)
                .build()
                .open();
    }
}
