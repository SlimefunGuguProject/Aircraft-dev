package org.metamechanists.aircraft.vehicles.hud;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.displaymodellib.models.components.ModelComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public final class VehicleHud {
    private VehicleHud() {}

    public static @NotNull Map<String, ModelComponent> build(@NotNull VehicleState state) {
        Vector3f hudCenter = new Vector3f(0.5F, -0.2F, 0.0F);
        Map<String, ModelComponent> hudComponents = new HashMap<>();
        Horizon.build(state, hudComponents, hudCenter);
        Compass.build(state, hudComponents, hudCenter);
        RightPanel.build(state, hudComponents, hudCenter);
        return hudComponents;
    }

    public static void update(@NotNull VehicleState state, Location location, double acceleration, double angularAcceleration) {
        Map<String, ModelComponent> hudComponents = build(state);
        Map<String, Display> displays = state.hudGroup.getDisplays();
        for (Entry<String, ModelComponent> entry : hudComponents.entrySet()) {
            Matrix4f matrix = Utils.getHudMatrix(hudComponents.get(entry.getKey()));
            displays.get(entry.getKey()).setTransformationMatrix(matrix);
        }

        Horizon.update(displays, location);
        RightPanel.update(state, displays, acceleration, angularAcceleration);
    }
}
