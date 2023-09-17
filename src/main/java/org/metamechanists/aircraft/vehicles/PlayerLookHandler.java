package org.metamechanists.aircraft.vehicles;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerLookHandler {
    private static final ProtocolManager MANAGER = ProtocolLibrary.getProtocolManager();

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
        final int entityId = player.getEntityId();
        final Location location = player.getLocation();
        final PacketContainer teleportPacket = MANAGER.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        teleportPacket.getIntegers().writeSafely(0, entityId);
        teleportPacket.getDoubles().writeSafely(0, location.getX());
        teleportPacket.getDoubles().writeSafely(1, location.getY());
        teleportPacket.getDoubles().writeSafely(2, location.getZ());
        teleportPacket.getBytes().writeSafely(0, degreeToByte(yaw));
        teleportPacket.getBytes().writeSafely(1, degreeToByte(pitch));
        teleportPacket.getBooleans().writeSafely(0, player.isOnGround());

        final PacketContainer relativeEntityMoveLookPacket = MANAGER.createPacket(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
        relativeEntityMoveLookPacket.getIntegers().writeSafely(0, entityId);
        relativeEntityMoveLookPacket.getShorts().writeSafely(0, (short) 0);
        relativeEntityMoveLookPacket.getShorts().writeSafely(1, (short) 0);
        relativeEntityMoveLookPacket.getShorts().writeSafely(2, (short) 0);
        relativeEntityMoveLookPacket.getBytes().writeSafely(0, degreeToByte(yaw));
        relativeEntityMoveLookPacket.getBytes().writeSafely(1, degreeToByte(pitch));
        relativeEntityMoveLookPacket.getBooleans().writeSafely(0, player.isOnGround());

        final PacketContainer entityLookPacket = MANAGER.createPacket(PacketType.Play.Server.ENTITY_LOOK);
        entityLookPacket.getIntegers().writeSafely(0, entityId);
        entityLookPacket.getBytes().writeSafely(0, degreeToByte(yaw));
        entityLookPacket.getBytes().writeSafely(1, degreeToByte(pitch));
        entityLookPacket.getBooleans().writeSafely(0, player.isOnGround());

        final PacketContainer headRotationPacket = MANAGER.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        headRotationPacket.getIntegers().writeSafely(0, entityId);
        headRotationPacket.getBytes().writeSafely(0, degreeToByte(yaw));

        MANAGER.sendServerPacket(player, relativeEntityMoveLookPacket);
        MANAGER.sendServerPacket(player, entityLookPacket);
        MANAGER.sendServerPacket(player, headRotationPacket);
    }

    private static byte degreeToByte(final double degree) {
        return (byte) (degree * 256.0F / 360.0F);
    }
}