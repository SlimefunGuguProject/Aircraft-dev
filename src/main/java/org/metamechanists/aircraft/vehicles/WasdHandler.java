package org.metamechanists.aircraft.vehicles;

import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.metamechanists.aircraft.Aircraft;

import java.lang.reflect.Method;


public class WasdHandler  {
    public static void get_key(final float rightLeft, final float forwardbackwards) {
        if (rightLeft < 0) {
            Aircraft.getInstance().getLogger().info("A");
        } else if (rightLeft > 0) {
            Aircraft.getInstance().getLogger().info("D");
        } else if (forwardbackwards < 0) {
            Aircraft.getInstance().getLogger().info("S");
        } else if (forwardbackwards > 0) {
            Aircraft.getInstance().getLogger().info("W");
        }
    }

    public static void addProtocolListener() {
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(
                Aircraft.getInstance(),
                ListenerPriority.NORMAL,
                Client.STEER_VEHICLE) {
            @Override
            public void onPacketReceiving(final PacketEvent event) {
                final PacketContainer packet = event.getPacket();
                final float rightleft = packet.getFloat().readSafely(0);
                final float forwardbackwards = packet.getFloat().readSafely(1);
                get_key(rightleft, forwardbackwards);
            }
        });
    }

    protected float getRotation(final Object packet) {
        try {
            final Method method = packet.getClass().getDeclaredMethod("a");
            return (float) method.invoke(packet);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    protected float getDirection(final Object packet) {
        try {
            final Method method = packet.getClass().getDeclaredMethod("c");
            return (float) method.invoke(packet);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
