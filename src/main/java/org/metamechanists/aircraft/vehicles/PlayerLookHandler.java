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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.Aircraft;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


@UtilityClass
public class PlayerLookHandler {
    private Map<Integer, UUID> rotatedPlayers = new HashMap<>();
    private Map<Integer, Pair<Double, Double>> playerRotations = new HashMap<>();

    private byte degreeToByte(final double degree) {
        return (byte) (degree * 256.0F / 360.0F);
    }

    public void adjustRotation(final @NotNull Player player, final double deltaYaw, final double deltaPitch) {
        rotatedPlayers.put(player.getEntityId(), player.getUniqueId());
        playerRotations.put(player.getUniqueId(), new Pair<>(0.0, 0.0));
    }

    public void removePlayer(final @NotNull Player player) {
        rotatedPlayers.remove(player.getEntityId());
        playerRotations.remove(player.getUniqueId());
    }

    // https://github.com/CitizensDev/Citizens2/blob/2931f95939699d9b53435d0282a167585e09ee7e/main/src/main/java/net/citizensnpcs/ProtocolLibListener.java#L286
    public void addProtocolListener() {
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(Aircraft.getInstance(), ListenerPriority.MONITOR,
                Arrays.asList(Server.ENTITY_HEAD_ROTATION, Server.ENTITY_LOOK, Server.REL_ENTITY_MOVE_LOOK, Server.POSITION, Server.ENTITY_TELEPORT),
                ListenerOptions.ASYNC) {
            @Override
            public void onPacketSending(final PacketEvent event) {
                final Integer eid = event.getPacket().getIntegers().readSafely(0);
                if (eid == null) {
                    return;
                }

                if (!rotatedPlayers.containsKey(eid)) {
                    return;
                }

                final Pair<Double, Double> rotation = Bukkit.getPlayer(rotatedPlayers.get(eid));

                final PacketContainer packet = event.getPacket();
                final PacketType type = event.getPacketType();
                if (Objects.equals(type, Server.ENTITY_HEAD_ROTATION)) {
                    packet.getBytes().write(0, degreeToByte(session.getHeadYaw()));
                } else if (Objects.equals(type, Server.ENTITY_LOOK)) {
                    packet.getBytes().write(0, degreeToByte(session.getBodyYaw()));
                    packet.getBytes().write(1, degreeToByte(session.getPitch()));
                } else if (Objects.equals(type, Server.REL_ENTITY_MOVE_LOOK)) {
                    packet.getBytes().write(0, degreeToByte(session.getBodyYaw()));
                    packet.getBytes().write(1, degreeToByte(session.getPitch()));
                } else if (Objects.equals(type, Server.POSITION)) {
                    StructureModifier<Set<PlayerTeleportFlag>> flagsModifier = packet
                            .getSets(EnumWrappers.getGenericConverter(flagsClass, PlayerTeleportFlag.class));
                    Set<PlayerTeleportFlag> rel = flagsModifier.read(0);
                    rel.remove(PlayerTeleportFlag.ZYAW);
                    rel.remove(PlayerTeleportFlag.ZPITCH);
                    flagsModifier.write(0, rel);
                    packet.getFloat().write(0, session.getBodyYaw());
                    packet.getFloat().write(1, session.getPitch());
                }

                session.onPacketOverwritten();
                Messaging.debug("OVERWRITTEN " + type + " " + packet.getHandle());
            }
        });

    }
}
