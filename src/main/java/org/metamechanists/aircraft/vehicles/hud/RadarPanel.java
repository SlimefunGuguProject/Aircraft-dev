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
    private RadarPanel() {}

    private static void addOctagon(
            VehicleState state, @NotNull Map<String, ModelComponent> hudComponents, String name,
            Vector3f center, Material material, double diameter, double width) {
        double sideSize = diameter / (1.0 + Math.sqrt(2));

        hudComponents.put(name + "-1", HudUtil.rollIndependentCuboid(state, center)
                .material(material)
                .rotateZ(0.0)
                .scale(new Vector3d(sideSize, diameter, width)));
        hudComponents.put(name + "-2", HudUtil.rollIndependentCuboid(state, center)
                .material(material)
                .rotateZ(PI / 2)
                .scale(new Vector3d(sideSize, diameter, width)));
        hudComponents.put(name + "-3", HudUtil.rollIndependentCuboid(state, center)
                .material(material)
                .rotateZ(PI)
                .scale(new Vector3d(sideSize, diameter, width)));
        hudComponents.put(name + "-4", HudUtil.rollIndependentCuboid(state, center)
                .material(material)
                .rotateZ(PI * 3 / 2)
                .scale(new Vector3d(sideSize, diameter, width)));
    }

    public static void build(VehicleState state, @NotNull Map<String, ModelComponent> hudComponents, Vector3f hudCenter) {
        Vector3f center = new Vector3f(0.0F, 0.0F, -1.0F).add(hudCenter);

        addOctagon(state, hudComponents, "outer-octagon", center, Material.GREEN_TERRACOTTA, 0.6, 0.0002);
        addOctagon(state, hudComponents, "middle-octagon", center, Material.GREEN_CONCRETE, 0.4, 0.0004);
        addOctagon(state, hudComponents, "inner-octagon", center, Material.LIME_TERRACOTTA, 0.2, 0.0006);
    }
}
