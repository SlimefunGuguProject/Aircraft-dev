package org.metamechanists.aircraft.vehicle.handler;

import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.kinematiccore.api.storage.EntityStorage;


public final class KeyboardHandler {
    private KeyboardHandler() {}

    private static void handleKey(@NotNull Player player, float rightLeft, float forwardbackwards) {
        if (player.getVehicle() == null) {
            return;
        }

        if (!(EntityStorage.kinematicEntity(player.getVehicle().getUniqueId()) instanceof VehicleEntity vehicleEntity)) {
            return;
        }

        if (rightLeft < 0) {
            vehicleEntity.onKey('d');
        } else if (rightLeft > 0) {
            vehicleEntity.onKey('a');
        }

        if (forwardbackwards < 0) {
            vehicleEntity.onKey('s');
        } else if (forwardbackwards > 0) {
            vehicleEntity.onKey('w');
        }
    }

    public static void addProtocolListener() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        manager.addPacketListener(new PacketAdapter(
                Aircraft.getInstance(),
                ListenerPriority.NORMAL,
                Client.STEER_VEHICLE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                float rightleft = packet.getFloat().readSafely(0);
                float forwardbackwards = packet.getFloat().readSafely(1);
                handleKey(event.getPlayer(), rightleft, forwardbackwards);
            }
        });
    }
}
