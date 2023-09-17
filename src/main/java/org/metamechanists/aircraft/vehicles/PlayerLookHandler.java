package org.metamechanists.aircraft.vehicles;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerLookHandler {
    private static final ProtocolManager MANAGER = ProtocolLibrary.getProtocolManager();

    public static void sendYawPacket(Player player, double deltaDegrees) {
        final Location eyeLocation = player.getEyeLocation();
        sendRotationPacket(player, eyeLocation.getYaw() + deltaDegrees, eyeLocation.getPitch());
    }

    public static void sendPitchPacket(Player player, double deltaDegrees) {
        final Location eyeLocation = player.getEyeLocation();
        sendRotationPacket(player, eyeLocation.getYaw(), eyeLocation.getPitch() + deltaDegrees);
    }

    public static void sendRotationPacket(Player player, double yaw, double pitch) {
        final Location location = player.getLocation();
        final PacketContainer packet = MANAGER.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().writeSafely(0, player.getEntityId());
        packet.getDoubles().writeSafely(0, location.getX());
        packet.getDoubles().writeSafely(1, location.getY());
        packet.getDoubles().writeSafely(2, location.getZ());
        packet.getBytes().writeSafely(0, degreeToByte(yaw));
        packet.getBytes().writeSafely(1, degreeToByte(pitch));
        packet.getBooleans().writeSafely(0, false);
        MANAGER.sendServerPacket(player, packet);
    }

    private static byte degreeToByte(final double degree) {
        return (byte) (degree * 256.0F / 360.0F);
    }
}