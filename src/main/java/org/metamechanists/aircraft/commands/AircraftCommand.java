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
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.items.Items;
import org.metamechanists.aircraft.vehicles.Storage;
import org.metamechanists.aircraft.vehicles.VehicleState;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


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
        Pig pig = Storage.getPig(player);
        if (pig == null) {
            player.sendMessage(ChatColor.RED + "You are not in an aircraft");
            return;
        }

        VehicleState state = VehicleState.fromPig(pig);
        if (state == null) {
            player.sendMessage(ChatColor.RED + "You are not in an aircraft");
            return;
        }

        Map<String, Integer> groupCounts = getGroupCounts(state);

        player.sendMessage(ChatColor.GRAY + "Total components: " + state.componentGroup.getDisplays().size() + state.hudGroup.getDisplays().size());

        player.sendMessage(ChatColor.YELLOW + "-----------");

        player.sendMessage(ChatColor.GRAY + "Model components: " + state.componentGroup.getDisplays().size());
        player.sendMessage(ChatColor.GRAY + "HUD components: " + state.hudGroup.getDisplays().size());

        player.sendMessage(ChatColor.YELLOW + "-----------");

        for (Entry<String, Integer> entry : groupCounts.entrySet()) {
            player.sendMessage(ChatColor.GRAY + entry.getKey() + ": " + entry.getValue());
        }
    }

    @NotNull
    private static Map<String, Integer> getGroupCounts(@NotNull VehicleState state) {
        Map<String, Integer> groupCounts = new HashMap<>();

        for (String key : state.componentGroup.getDisplays().keySet()) {
            String group = key.split("[.]")[0] + "." + key.split("[.]")[1];
            if (groupCounts.containsKey(group)) {
                groupCounts.put(group, groupCounts.get(group) + 1);
            } else {
                groupCounts.put(group, 1);
            }
        }

        for (String key : state.hudGroup.getDisplays().keySet()) {
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