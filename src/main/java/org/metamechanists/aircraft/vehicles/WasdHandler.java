package org.metamechanists.aircraft.vehicles;

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
import org.metamechanists.aircraft.utils.PersistentDataTraverser;

public final class WasdHandler  {
    private WasdHandler() {}

    private static void handleKey(final @NotNull Player player, final float rightLeft, final float forwardbackwards) {
        if (player.getVehicle() == null) {
            return;
        }

        final PersistentDataTraverser traverser = new PersistentDataTraverser(player.getVehicle());
        if (traverser.getString("name") == null) {
            return;
        }

        if (rightLeft < 0) {
            Vehicle.onKeyD(traverser);
        } else if (rightLeft > 0) {
            Vehicle.onKeyA(traverser);
        }

        if (forwardbackwards < 0) {
            Vehicle.onKeyS(traverser);
        } else if (forwardbackwards > 0) {
            Vehicle.onKeyW(traverser);
        }
    }

    public static void addProtocolListener() {
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(
                Aircraft.getInstance(),
                ListenerPriority.NORMAL,
                Client.STEER_VEHICLE) {
            @Override
            public void onPacketReceiving(final PacketEvent event) {
                final PacketContainer packet = event.getPacket();
                final float rightleft = packet.getFloat().readSafely(0);
                final float forwardbackwards = packet.getFloat().readSafely(1);
                handleKey(event.getPlayer(), rightleft, forwardbackwards);
            }
        });
    }
}
