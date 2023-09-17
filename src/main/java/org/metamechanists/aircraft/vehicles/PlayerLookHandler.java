package org.metamechanists.aircraft.vehicles;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PlayerLookHandler {
    private static final ProtocolManager MANAGER = ProtocolLibrary.getProtocolManager();
    private static final double DEGRESS_TO_RADIAN = Math.PI / 180;

    public static void changePlayerCamera(Player player, Entity entity) {
        final PacketContainer cameraPacket = MANAGER.createPacket(PacketType.Play.Server.CAMERA);
        cameraPacket.getIntegers().writeSafely(0, entity.getEntityId());
        MANAGER.sendServerPacket(player, cameraPacket);
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
        double f = Math.cos(-yaw * DEGRESS_TO_RADIAN - Math.PI);
        double f1 = Math.sin(-yaw * DEGRESS_TO_RADIAN - Math.PI);
        double f2 = -Math.cos(-pitch * DEGRESS_TO_RADIAN);
        double f3 = Math.sin(-pitch * DEGRESS_TO_RADIAN);
        return new Vector(f1 * f2, f3, f * f2).multiply(100000).add(location.toVector());
    }

    private static byte degreeToByte(final double degree) {
        return (byte) (degree * 256.0F / 360.0F);
    }
}