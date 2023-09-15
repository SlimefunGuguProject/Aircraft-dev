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
    public void adjustRotation(final @NotNull Player player, final double deltaYaw, final double deltaPitch) {
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        final PacketContainer packet = new PacketContainer(Server.ENTITY_HEAD_ROTATION);
        packet.getIntegers().write(0, player.getEntityId());
        packet.getDoubles()
                .write(0, player.getLocation().getYaw() + toDegrees(deltaYaw))
                .write(1, player.getLocation().getPitch() + toDegrees(deltaPitch));
        packet.getBooleans().write(0, false);
        manager.broadcastServerPacket(packet);
    }
}
