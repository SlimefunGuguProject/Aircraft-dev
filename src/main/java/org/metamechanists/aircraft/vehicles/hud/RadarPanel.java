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
            Vector3f center, double rotation, Material material, double diameter, double offset) {
        double sideSize = diameter / (1.0 + Math.sqrt(2));

        hudComponents.put(name + "-1", HudUtil.rollIndependentCuboid(state, center)
                .rotateY(rotation)
                .translate(0, 0, -offset)
                .material(material)
                .rotateZ(0.0)
                .scale(new Vector3d(sideSize, diameter, 0.0001)));
        hudComponents.put(name + "-2", HudUtil.rollIndependentCuboid(state, center)
                .rotateY(rotation)
                .translate(0, 0, -offset - 0.0003)
                .material(material)
                .rotateZ(PI / 4)
                .scale(new Vector3d(sideSize, diameter, 0.0001)));
        hudComponents.put(name + "-3", HudUtil.rollIndependentCuboid(state, center)
                .rotateY(rotation)
                .translate(0, 0, -offset - 0.0006)
                .material(material)
                .rotateZ(PI / 2)
                .scale(new Vector3d(sideSize, diameter, 0.0001)));
        hudComponents.put(name + "-4", HudUtil.rollIndependentCuboid(state, center)
                .rotateY(rotation)
                .translate(0, 0, -offset - 0.0009)
                .material(material)
                .rotateZ(PI * 3 / 4)
                .scale(new Vector3d(sideSize, diameter, 0.001)));
    }

    public static void build(VehicleState state, @NotNull Map<String, ModelComponent> hudComponents, Vector3f hudCenter) {
        Vector3f center = new Vector3f(0.0F, 0.0F, -0.4F).add(hudCenter);
        double rotation = -0.4;

        addOctagon(state, hudComponents, "outer-octagon", center, rotation, Material.GREEN_STAINED_GLASS, 0.3, 0.009);
        addOctagon(state, hudComponents, "middle-octagon", center, rotation, Material.GREEN_STAINED_GLASS, 0.2, 0.006);
        addOctagon(state, hudComponents, "inner-octagon", center, rotation, Material.GREEN_STAINED_GLASS, 0.1, 0.003);
    }
}
