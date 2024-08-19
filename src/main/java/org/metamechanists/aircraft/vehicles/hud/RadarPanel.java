package org.metamechanists.aircraft.vehicles.hud;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelComponent;

import java.util.Map;

import static java.lang.Math.PI;


public final class RadarPanel {
    private static double RADAR_LINE_SIZE = 0.01;

    private RadarPanel() {}

    private static void addOctagon(
            @NotNull Map<String, ModelComponent> hudComponents, String name,
            Vector3f center, double rotation, Material material, double diameter) {
        double sideSize = diameter / (1.0 + Math.sqrt(2));

        hudComponents.put(name + "-1", HudUtil.rollCuboid(center)
                .rotateY(rotation)
                .material(material)
                .rotateZ(0.0)
                .translate(diameter / 2, 0, 0)
                .scale(new Vector3d(RADAR_LINE_SIZE, sideSize, 0.00001)));
        hudComponents.put(name + "-2", HudUtil.rollCuboid(center)
                .rotateY(rotation)
                .material(material)
                .rotateZ(PI / 4)
                .translate(diameter / 2, 0, 0)
                .scale(new Vector3d(RADAR_LINE_SIZE, sideSize, 0.00001)));
        hudComponents.put(name + "-3", HudUtil.rollCuboid(center)
                .rotateY(rotation)
                .material(material)
                .rotateZ(PI / 2)
                .translate(diameter / 2, 0, 0)
                .scale(new Vector3d(RADAR_LINE_SIZE, sideSize, 0.00001)));
        hudComponents.put(name + "-4", HudUtil.rollCuboid(center)
                .rotateY(rotation)
                .material(material)
                .rotateZ(PI * 3 / 4)
                .translate(diameter / 2, 0, 0)
                .scale(new Vector3d(RADAR_LINE_SIZE, sideSize, 0.00001)));
    }

    public static void build(VehicleState state, @NotNull Map<String, ModelComponent> hudComponents, Vector3f hudCenter) {
        Vector3f center = new Vector3f(0.0F, 0.0F, -0.4F).add(hudCenter);
        double rotation = 0.785;

        addOctagon(hudComponents, "outer-octagon", center, rotation, Material.GREEN_TERRACOTTA, 0.3);
        addOctagon(hudComponents, "middle-octagon", center, rotation, Material.GREEN_CONCRETE, 0.2);
        addOctagon(hudComponents, "inner-octagon", center, rotation, Material.LIME_TERRACOTTA, 0.1);
    }
}
