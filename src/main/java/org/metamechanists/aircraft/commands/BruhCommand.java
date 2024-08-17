package org.metamechanists.aircraft.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedCuboid;


@CommandAlias("bruh")
@SuppressWarnings("unused")
public class BruhCommand extends BaseCommand {
    @Subcommand("bruh")
    public static void bruh(@NotNull Player player, String @NotNull [] args) {
        new ModelAdvancedCuboid()
                .material(Material.GRAY_CONCRETE)
                .rotate(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]))
                .build(player.getLocation());
    }
}
