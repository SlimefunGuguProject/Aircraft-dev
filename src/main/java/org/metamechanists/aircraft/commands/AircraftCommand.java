package org.metamechanists.aircraft.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Private;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.items.Items;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.kinematiccore.api.entity.KinematicEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@CommandAlias("aircraft")
@SuppressWarnings("unused")
public class AircraftCommand extends BaseCommand {
    @HelpCommand
    @Syntax("")
    @Private
    public static void helpCommand(CommandSender sender, @NotNull CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("reload")
    @Description("Reloads the plugin")
    @CommandPermission("aircraft.command.reload")
    public static void reload(@NotNull Player player, String @NotNull [] args) {
        player.sendMessage(ChatColor.GREEN + "Aircraft reloaded!");
        Items.reload();
    }

    @Subcommand("displaycount")
    @Description("")
    @CommandPermission("aircraft.command.diagnostics")
    public static void diagnostics(@NotNull Player player, String @NotNull [] args) {
        Entity riding = player.getVehicle();
        if (riding == null) {
            player.sendMessage(ChatColor.RED + "You are not in an aircraft");
            return;
        }

        if (!(KinematicEntity.get(riding.getUniqueId()) instanceof VehicleEntity vehicleEntity)) {
            player.sendMessage(ChatColor.RED + "You are not in an aircraft");
            return;
        }

        Map<String, Integer> groupCounts = getGroupCounts(vehicleEntity);
        List<String> sorted = new ArrayList<>(groupCounts.keySet().stream().toList());
        sorted.sort(Comparator.comparingInt(groupCounts::get));

        player.sendMessage(ChatColor.GRAY + "Total components: " + (vehicleEntity.getComponents().size() + vehicleEntity.getHud().size()));

        player.sendMessage(ChatColor.YELLOW + "-----------");

        player.sendMessage(ChatColor.GRAY + "Model components: " + vehicleEntity.getComponents().size());
        player.sendMessage(ChatColor.GRAY + "HUD components: " + vehicleEntity.getHud().size());

        player.sendMessage(ChatColor.YELLOW + "-----------");

        for (String key : sorted) {
            player.sendMessage(ChatColor.GRAY + key + ": " + groupCounts.get(key));
        }
    }

    @NotNull
    private static Map<String, Integer> getGroupCounts(@NotNull VehicleEntity state) {
        Map<String, Integer> groupCounts = new HashMap<>();

        for (String key : state.getComponents().keySet()) {
            String group = key.split("[.]")[0] + "." + key.split("[.]")[1];
            if (groupCounts.containsKey(group)) {
                groupCounts.put(group, groupCounts.get(group) + 1);
            } else {
                groupCounts.put(group, 1);
            }
        }

        for (String key : state.getHud().keySet()) {
            String group = key.split("[.]")[0] + "." + key.split("[.]")[1];
            if (groupCounts.containsKey(group)) {
                groupCounts.put(group, groupCounts.get(group) + 1);
            } else {
                groupCounts.put(group, 1);
            }
        }
        return groupCounts;
    }
}