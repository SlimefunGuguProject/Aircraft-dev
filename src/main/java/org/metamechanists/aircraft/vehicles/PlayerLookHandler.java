package org.metamechanists.aircraft.vehicles;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import io.github.bakedlibs.dough.collections.Pair;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.Aircraft;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@UtilityClass
public class PlayerLookHandler {
    private Map<Integer, Pair<Double, Double>> playerRotationDeltas = new HashMap<>();

    private byte degreeToByte(final double degree) {
        return (byte) (degree * 256.0F / 360.0F);
    }

    public void adjustRotation(final @NotNull Player player, final double deltaYaw, final double deltaPitch) {
        playerRotationDeltas.put(player.getEntityId(), new Pair<>(0.0, 0.0));
    }

    public void removePlayer(final @NotNull Player player) {
        playerRotationDeltas.remove(player.getEntityId());
    }

    // https://github.com/CitizensDev/Citizens2/blob/2931f95939699d9b53435d0282a167585e09ee7e/main/src/main/java/net/citizensnpcs/ProtocolLibListener.java#L286
    public void addProtocolListener() {
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(Aircraft.getInstance(), ListenerPriority.MONITOR,
                Arrays.asList(Server.ENTITY_HEAD_ROTATION, Server.ENTITY_LOOK, Server.REL_ENTITY_MOVE_LOOK, Server.POSITION, Server.ENTITY_TELEPORT),
                ListenerOptions.ASYNC) {
            @Override
            public void onPacketSending(final PacketEvent event) {
                final Integer id = event.getPacket().getIntegers().readSafely(0);
                if (id == null) {
                    return;
                }

                if (!playerRotationDeltas.containsKey(id)) {
                    return;
                }

                final Pair<Double, Double> rotationDelta = playerRotationDeltas.get(id);

                final PacketContainer packet = event.getPacket();
                final PacketType type = event.getPacketType();
                if (Objects.equals(type, Server.ENTITY_HEAD_ROTATION)) {
                    packet.getBytes().write(0, degreeToByte(0));
                } else if (Objects.equals(type, Server.ENTITY_LOOK)) {
                    packet.getBytes().write(0, degreeToByte(0));
                    packet.getBytes().write(1, degreeToByte(0));
                } else if (Objects.equals(type, Server.REL_ENTITY_MOVE_LOOK)) {
                    packet.getBytes().write(0, degreeToByte(0));
                    packet.getBytes().write(1, degreeToByte(0));
                }
            }
        });

    }
}
