package org.metamechanists.aircraft.utils;

import io.github.bakedlibs.dough.data.persistent.PersistentDataAPI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.utils.id.CustomId;
import org.metamechanists.aircraft.utils.id.simple.BlockDisplayId;
import org.metamechanists.aircraft.utils.id.simple.DisplayGroupId;
import org.metamechanists.aircraft.utils.id.simple.InteractionId;
import org.metamechanists.aircraft.utils.id.simple.TextDisplayId;
import org.metamechanists.aircraft.vehicles.ControlSurface;
import org.metamechanists.aircraft.vehicles.ControlSurfaces;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@SuppressWarnings({"WeakerAccess", "unused"})
public class PersistentDataTraverser {
    private final PersistentDataHolder persistentDataHolder;
    private static final Map<String, NamespacedKey> keys = new ConcurrentHashMap<>();

    public PersistentDataTraverser(@NotNull final CustomId id) {
        this.persistentDataHolder = Bukkit.getEntity(id.getUUID());
    }
    public PersistentDataTraverser(@NotNull final UUID id) {
        this.persistentDataHolder = Bukkit.getEntity(id);
    }
    public PersistentDataTraverser(@NotNull final PersistentDataHolder entity) {
        this.persistentDataHolder = entity;
    }
    public PersistentDataTraverser(@NotNull final ItemStack stack) {
        this.persistentDataHolder = stack.getItemMeta();
    }

    public void save(@NotNull final ItemStack stack) {
        stack.setItemMeta((ItemMeta) persistentDataHolder);
    }

    private static @NotNull NamespacedKey getKey(@NotNull final String key) {
        return keys.computeIfAbsent(key, k -> new NamespacedKey(Aircraft.getInstance(), key));
    }

    public void set(@NotNull final String key, final int value) {
        PersistentDataAPI.setInt(persistentDataHolder, getKey(key), value);
    }
    public void set(@NotNull final String key, final double value) {
        PersistentDataAPI.setDouble(persistentDataHolder, getKey(key), value);
    }
    public void set(@NotNull final String key, final boolean value) {
        PersistentDataAPI.setBoolean(persistentDataHolder, getKey(key), value);
    }
    public void set(@NotNull final String key, @Nullable final String value) {
        if (value == null) {
            PersistentDataAPI.remove(persistentDataHolder, getKey(key));
            return;
        }
        PersistentDataAPI.setString(persistentDataHolder, getKey(key), value);
    }
    public void set(@NotNull final String key, @Nullable final Vector value) {
        if (value == null) {
            PersistentDataAPI.remove(persistentDataHolder, getKey(key + "x"));
            PersistentDataAPI.remove(persistentDataHolder, getKey(key + "y"));
            PersistentDataAPI.remove(persistentDataHolder, getKey(key + "z"));
            return;
        }

        set(key + "x", value.getX());
        set(key + "y", value.getY());
        set(key + "z", value.getZ());
    }
    public void set(@NotNull final String key, @Nullable final Vector3f value) {
        if (value == null) {
            PersistentDataAPI.remove(persistentDataHolder, getKey(key + "x"));
            PersistentDataAPI.remove(persistentDataHolder, getKey(key + "y"));
            PersistentDataAPI.remove(persistentDataHolder, getKey(key + "z"));
            return;
        }

        set(key + "x", value.x);
        set(key + "y", value.y);
        set(key + "z", value.z);
    }
    public void set(@NotNull final String key, @Nullable final Vector3d value) {
        if (value == null) {
            PersistentDataAPI.remove(persistentDataHolder, getKey(key + "x"));
            PersistentDataAPI.remove(persistentDataHolder, getKey(key + "y"));
            PersistentDataAPI.remove(persistentDataHolder, getKey(key + "z"));
            return;
        }

        set(key + "x", value.x);
        set(key + "y", value.y);
        set(key + "z", value.z);
    }
    public void set(@NotNull final String key, @Nullable final Quaternionf value) {
        if (value == null) {
            PersistentDataAPI.remove(persistentDataHolder, getKey(key + "x"));
            PersistentDataAPI.remove(persistentDataHolder, getKey(key + "y"));
            PersistentDataAPI.remove(persistentDataHolder, getKey(key + "z"));
            PersistentDataAPI.remove(persistentDataHolder, getKey(key + "w"));
            return;
        }

        set(key + "x", value.x);
        set(key + "y", value.y);
        set(key + "z", value.z);
        set(key + "z", value.w);
    }
    public void set(@NotNull final String key, @Nullable final Quaterniond value) {
        if (value == null) {
            PersistentDataAPI.remove(persistentDataHolder, getKey(key + "x"));
            PersistentDataAPI.remove(persistentDataHolder, getKey(key + "y"));
            PersistentDataAPI.remove(persistentDataHolder, getKey(key + "z"));
            PersistentDataAPI.remove(persistentDataHolder, getKey(key + "w"));
            return;
        }

        set(key + "x", value.x);
        set(key + "y", value.y);
        set(key + "z", value.z);
        set(key + "z", value.w);
    }
    public void set(@NotNull final String key, @Nullable final UUID value) {
        if (value == null) {
            PersistentDataAPI.remove(persistentDataHolder, getKey(key));
            return;
        }

        PersistentDataAPI.setString(persistentDataHolder, getKey(key), value.toString());
    }
    public void set(@NotNull final String key, @Nullable final CustomId value) {
        set(key, value == null ? null : value.toString());
    }
    public void set(@NotNull final String key, @Nullable final ControlSurface value) {
        set(key + "angle", value == null ? 0 : value.getAngle());
        set(key + "ticksUntilReturn", value == null ? 0 : value.getTicksUntilReturn());
    }
    public void set(@NotNull final String key, @NotNull final ControlSurfaces value) {
        set(key + "aileron1", value.aileron1);
        set(key + "aileron2", value.aileron2);
        set(key + "elevator1", value.elevator1);
        set(key + "elevator2", value.elevator2);
    }
    public void set(@NotNull final String key, @NotNull final Map<String, ? extends CustomId> value) {
        set(key + "length", value.size());
        int i = 0;
        for (final Entry<String, ? extends CustomId> pair : value.entrySet()) {
            set(key + i + "key", pair.getKey());
            set(key + i + "value", pair.getValue().toString());
            i++;
        }
    }
    public void set(@NotNull final String key, @NotNull final List<UUID> value) {
        set(key + "length", value.size());
        int i = 0;
        for (final UUID uuid : value) {
            set(key + i, uuid.toString());
            i++;
        }
    }
    // 'both methods have the same erasure' I hate this language so much......
    public void setCustomIdList(@NotNull final String key, final @NotNull List<? extends CustomId> value) {
        set(key + "length", value.size());
        int i = 0;
        for (final CustomId uuid : value) {
            set(key + i, uuid.toString());
            i++;
        }
    }

    public int getInt(@NotNull final String key) {
        return PersistentDataAPI.getInt(persistentDataHolder, getKey(key));
    }
    public double getDouble(@NotNull final String key) {
        return PersistentDataAPI.getDouble(persistentDataHolder, getKey(key));
    }
    public boolean getBoolean(@NotNull final String key) {
        return PersistentDataAPI.getBoolean(persistentDataHolder, getKey(key));
    }
    public @Nullable String getString(@NotNull final String key) {
        return PersistentDataAPI.getString(persistentDataHolder, getKey(key));
    }
    public @Nullable Vector getVector(@NotNull final String key) {
        return new Vector(
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "x")),
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "y")),
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "z")));
    }
    public @Nullable Vector3f getVector3f(@NotNull final String key) {
        return new Vector3f(
                PersistentDataAPI.getFloat(persistentDataHolder, getKey(key + "x")),
                PersistentDataAPI.getFloat(persistentDataHolder, getKey(key + "y")),
                PersistentDataAPI.getFloat(persistentDataHolder, getKey(key + "z")));
    }
    public @Nullable Vector3d getVector3d(@NotNull final String key) {
        return new Vector3d(
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "x")),
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "y")),
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "z")));
    }
    public @Nullable Quaternionf getQuaternionf(@NotNull final String key) {
        return new Quaternionf(
                PersistentDataAPI.getFloat(persistentDataHolder, getKey(key + "x")),
                PersistentDataAPI.getFloat(persistentDataHolder, getKey(key + "y")),
                PersistentDataAPI.getFloat(persistentDataHolder, getKey(key + "z")),
                PersistentDataAPI.getFloat(persistentDataHolder, getKey(key + "w")));
    }
    public @Nullable Quaterniond getQuaterniond(@NotNull final String key) {
        return new Quaterniond(
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "x")),
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "y")),
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "z")),
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "w")));
    }
    public @Nullable UUID getUuid(@NotNull final String key) {
        final String uuidString = getString(key);
        if (uuidString == null) {
            return null;
        }

        return UUID.fromString(uuidString);
    }
    public @Nullable BlockDisplayId getBlockDisplayId(@NotNull final String key) {
        final String uuid = getString(key);
        return uuid == null ? null : new BlockDisplayId(uuid);
    }
    public @Nullable DisplayGroupId getDisplayGroupId(@NotNull final String key) {
        final String uuid = getString(key);
        return uuid == null ? null : new DisplayGroupId(uuid);
    }
    public @Nullable InteractionId getInteractionId(@NotNull final String key) {
        final String uuid = getString(key);
        return uuid == null ? null : new InteractionId(uuid);
    }
    public @Nullable TextDisplayId getTextDisplayId(@NotNull final String key) {
        final String uuid = getString(key);
        return uuid == null ? null : new TextDisplayId(uuid);
    }
    public @NotNull ControlSurface getControlSurface(@NotNull final String key) {
        return new ControlSurface(getDouble(key + "angle"), getInt(key + "ticksUntilReturn"));
    }
    public @NotNull ControlSurfaces getControlSurfaces(@NotNull final String key) {
        return new ControlSurfaces(
                getControlSurface(key + "aileron1"),
                getControlSurface(key + "aileron2"),
                getControlSurface(key + "elevator1"),
                getControlSurface(key + "elevator2"));
    }
    public @Nullable List<UUID> getUuidList(@NotNull final String key) {
        final int size = getInt(key + "length");
        if (size == 0) {
            return null;
        }
        return IntStream.range(0, size)
                .mapToObj(i -> getString(key + i))
                .filter(Objects::nonNull)
                .map(UUID::fromString).collect(Collectors.toList());
    }
    public @Nullable List<InteractionId> getCustomIdList(@NotNull final String key) {
        final int size = getInt(key + "length");
        if (size == 0) {
            return null;
        }
        return IntStream.range(0, size)
                .mapToObj(i -> getString(key + i))
                .filter(Objects::nonNull)
                .map(UUID::fromString)
                .map(InteractionId::new)
                .collect(Collectors.toList());
    }
}
