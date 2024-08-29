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

        player.sendMessage(ChatColor.GRAY + "Model components: " + state.componentGroup.getDisplays().size());
        player.sendMessage(ChatColor.GRAY + "HUD components: " + state.componentGroup.getDisplays().size());
    }
}