package org.metamechanists.aircraft.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.displaymodellib.models.components.ModelItem;
import org.metamechanists.displaymodellib.transformations.TransformationMatrixBuilder;

import static java.lang.Math.PI;


@CommandAlias("bruh")
@SuppressWarnings("unused")
public class BruhCommand extends BaseCommand {
    @Subcommand("bruh")
    public static void bruh(@NotNull Player player, String @NotNull [] args) {
        Display x = new ModelItem()
                .material(Material.GRAY_CONCRETE)
                .interpolationDuration(4)
                .build(player.getLocation());

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Aircraft.getInstance(), () -> {
            x.setTransformationMatrix(new TransformationMatrixBuilder()
                    .rotate(0.0, PI / 4 * Bukkit.getServer().getCurrentTick(), 0.0)
                    .scale(1.0F, 0.2F, 0.2F)
                    .buildForItemDisplay());
            x.setInterpolationDelay(0);
            x.setInterpolationDuration(4);
        }, 0, 4);
    }
}
