package org.metamechanists.aircraft.vehicle.component;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.metamechanists.aircraft.Aircraft;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public final class Hider implements Listener {
    private static class HideSpecification {
        private final @NotNull List<UUID> toHide;
        private @Nullable UUID playerToExempt;

        private HideSpecification(@NotNull List<UUID> toHide, @Nullable UUID playerToExempt) {
            this.toHide = toHide;
            this.playerToExempt = playerToExempt;
        }
    }

    private static final Map<UUID, HideSpecification> hideSpecifications = new HashMap<>();

    public static void init() {
        Bukkit.getPluginManager().registerEvents(new Hider(), Aircraft.getInstance());
    }

    private static void hide(@NotNull Player player, @NotNull HideSpecification hideSpecification) {
        if (player.getUniqueId().equals(hideSpecification.playerToExempt)) {
            return;
        }

        for (UUID uuid : hideSpecification.toHide) {
            Entity entity = Bukkit.getEntity(uuid);
            if (entity == null) {
                continue;
            }

            player.hideEntity(Aircraft.getInstance(), entity);
        }
    }

    public static void hide(@NotNull UUID uuid, @NotNull List<UUID> toHide, @Nullable UUID playerToExempt) {
        HideSpecification hideSpecification = new HideSpecification(toHide, playerToExempt);
        hideSpecifications.put(uuid, hideSpecification);
        for (Player player : Bukkit.getOnlinePlayers()) {
            hide(player, hideSpecification);
        }
    }

    public static void setExempt(@NotNull UUID uuid, @Nullable Player exempt) {
        hideSpecifications.get(uuid).playerToExempt = (exempt == null) ? null : exempt.getUniqueId();
    }

    @EventHandler
    private static void onJoin(PlayerJoinEvent event) {
        for (HideSpecification hideSpecification : hideSpecifications.values()) {
            hide(event.getPlayer(), hideSpecification);
        }
    }
}
