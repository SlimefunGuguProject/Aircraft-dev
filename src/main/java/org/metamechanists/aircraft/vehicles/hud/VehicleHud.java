package org.metamechanists.aircraft.vehicles.hud;

import org.bukkit.Location;
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

    public static @NotNull Map<String, ModelComponent> build(@NotNull VehicleState state, Location location) {
        Vector3f hudCenter = new Vector3f(0.5F, -0.2F, 0.0F);
        Map<String, ModelComponent> hudComponents = new HashMap<>();
        Horizon.update(state, hudComponents, hudCenter, location);
        Compass.update(state, hudComponents, hudCenter);
        return hudComponents;
    }

    public static void update(@NotNull VehicleState state, Location location) {
        Map<String, ModelComponent> hudComponents = build(state, location);
        for (Entry<String, ModelComponent> entry : hudComponents.entrySet()) {
            Matrix4f matrix = Utils.getHudMatrix(hudComponents.get(entry.getKey()));
            state.hudGroup.getDisplays().get(entry.getKey()).setTransformationMatrix(matrix);
        }
    }
}
