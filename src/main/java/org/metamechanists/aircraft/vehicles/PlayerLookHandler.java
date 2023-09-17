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

    private static byte degreeToByte(final double degree) {
        return (byte) (degree * 256.0F / 360.0F);
    }
}