package org.metamechanists.aircraft.vehicles;

import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.items.Items;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;

public final class KeyboardHandler {
    private KeyboardHandler() {}

    private static void handleKey(@NotNull Player player, float rightLeft, float forwardbackwards) {
        Pig seat = Storage.getSeat(player);
        if (seat == null) {
            return;
        }

        PersistentDataTraverser traverser = new PersistentDataTraverser(seat);

        Vehicle vehicle = Storage.getVehicle(player);
        if (vehicle == null) {
            return;
        }

        if (rightLeft < 0) {
            vehicle.onKey(traverser, 'd');
        } else if (rightLeft > 0) {
            vehicle.onKey(traverser, 'a');
        }

        if (forwardbackwards < 0) {
            vehicle.onKey(traverser, 's');
        } else if (forwardbackwards > 0) {
            vehicle.onKey(traverser, 'w');
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

        manager.addPacketListener(new PacketAdapter(
                Aircraft.getInstance(),
                ListenerPriority.NORMAL,
                Client.ENTITY_ACTION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                int type = packet.getIntegers().readSafely(0);
                if (type == 7) {
                    Pig seat = Storage.getSeat(event.getPlayer());
                    if (seat == null) {
                        return;
                    }

                    PersistentDataTraverser traverser = new PersistentDataTraverser(seat);

                    Vehicle vehicle = Storage.getVehicle(event.getPlayer());
                    if (vehicle == null) {
                        return;
                    }

                    vehicle.onKey(traverser, 'e');
                }
            }
        });
    }
}
