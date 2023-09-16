package org.metamechanists.aircraft.vehicles;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.toDegrees;

@UtilityClass
public class PlayerLookHandler {
    private byte degreeToByte(final double degree) {
        return (byte) (degree * 256.0F / 360.0F);
    }

    public void adjustRotation(final @NotNull Player player, final double deltaYaw, final double deltaPitch) {
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        final PacketContainer packet = new PacketContainer(Server.ENTITY_LOOK);
        packet.getIntegers().write(0, player.getEntityId());
        packet.getBytes()
                .write(0, degreeToByte(player.getLocation().getYaw() + toDegrees(deltaYaw)))
                .write(1, degreeToByte(player.getLocation().getPitch() + toDegrees(deltaPitch)));
        packet.getBooleans().write(0, false);
        manager.broadcastServerPacket(packet);
    }
}
