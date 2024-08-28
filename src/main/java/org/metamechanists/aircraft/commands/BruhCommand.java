package org.metamechanists.aircraft.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.displaymodellib.models.components.ModelCuboid;
import org.metamechanists.displaymodellib.transformations.TransformationMatrixBuilder;

import static java.lang.Math.PI;


@CommandAlias("bruh")
@SuppressWarnings("unused")
public class BruhCommand extends BaseCommand {
    @Subcommand("bruh")
    public static void bruh(@NotNull Player player, String @NotNull [] args) {
        Display x = new ModelCuboid()
                .material(Material.GRAY_CONCRETE)
                .build(player.getLocation());

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Aircraft.getInstance(), () -> {
            x.setInterpolationDelay(0);
            x.setInterpolationDuration(4);
            x.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f((float) (PI / 8 * Bukkit.getServer().getCurrentTick()), 0.0F, 1.0F, 0.0F), new Vector3f(1.0F, 0.2F, 0.2F), new AxisAngle4f()));
        }, 0, 4);
    }
}
