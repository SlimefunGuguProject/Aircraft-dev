package org.metamechanists.aircraft.vehicles;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.MonitorAdapter;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.metamechanists.aircraft.Aircraft;


public class PlayerLookHandler {
    private static final ProtocolManager MANAGER = ProtocolLibrary.getProtocolManager();
    private static final double DEGREES_TO_RADIAN = Math.PI / 180;

    public static void addProtocolListener() {
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        manager.addPacketListener(new PacketAdapter(
                Aircraft.getInstance(),
                ListenerPriority.HIGH,
                PacketType.Play.Client.LOOK) {
            @Override
            public void onPacketReceiving(final PacketEvent event) {
                final PacketContainer packet = event.getPacket();
                final Location location = event.getPlayer().getLocation();
                final Vector coordinates = getVectorForLookAtPacket(location, 45, 5);
                packet.getIntegers().writeSafely(0, 1);
                packet.getDoubles().writeSafely(0, coordinates.getX());
                packet.getDoubles().writeSafely(1, coordinates.getY());
                packet.getDoubles().writeSafely(2, coordinates.getZ());
                packet.getBooleans().writeSafely(0, false);
                sendRotationPacket(event.getPlayer(), 5, 45);
            }
        });

        manager.addPacketListener(new MonitorAdapter(Aircraft.getInstance(), ConnectionSide.BOTH) {
            @Override
            public void onPacketReceiving(final PacketEvent event) {
                Bukkit.getLogger().info(event.getPacketType().toString());
            }

            @Override
            public void onPacketSending(final PacketEvent event) {
                Bukkit.getLogger().info(event.getPacketType().toString());
            }
        });
    }

    public static void sendYawPacket(Player player, double delta) {
        final Location eyeLocation = player.getEyeLocation();
        sendRotationPacket(player, eyeLocation.getYaw() + delta, eyeLocation.getPitch());
    }

    public static void sendPitchPacket(Player player, double delta) {
        final Location eyeLocation = player.getEyeLocation();
        sendRotationPacket(player, eyeLocation.getYaw(), eyeLocation.getPitch() + delta);
    }

    public static void sendYawPitchPacket(Player player, double deltaYaw, double deltaPitch) {
        final Location eyeLocation = player.getEyeLocation();
        sendRotationPacket(player, eyeLocation.getYaw() + deltaYaw, eyeLocation.getPitch() + deltaPitch);
    }

    private static void sendRotationPacket(Player player, double yaw, double pitch) {
        final Location location = player.getLocation();
        final PacketContainer lookAtPacket = MANAGER.createPacket(PacketType.Play.Server.LOOK_AT);
        final Vector coordinates = getVectorForLookAtPacket(location, pitch, yaw);
        lookAtPacket.getIntegers().writeSafely(0, 1);
        lookAtPacket.getDoubles().writeSafely(0, coordinates.getX());
        lookAtPacket.getDoubles().writeSafely(1, coordinates.getY());
        lookAtPacket.getDoubles().writeSafely(2, coordinates.getZ());
        lookAtPacket.getBooleans().writeSafely(0, false);
        MANAGER.sendServerPacket(player, lookAtPacket);
    }

    private static Vector getVectorForLookAtPacket(Location location, double pitch, double yaw) {
        double f = Math.cos(-yaw * DEGREES_TO_RADIAN - Math.PI);
        double f1 = Math.sin(-yaw * DEGREES_TO_RADIAN - Math.PI);
        double f2 = -Math.cos(-pitch * DEGREES_TO_RADIAN);
        double f3 = Math.sin(-pitch * DEGREES_TO_RADIAN);
        return new Vector(f1 * f2, f3, f * f2).multiply(100000).add(location.toVector());
    }
}