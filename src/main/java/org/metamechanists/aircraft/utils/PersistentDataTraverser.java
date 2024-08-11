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
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.utils.id.CustomId;
import org.metamechanists.aircraft.utils.id.simple.BlockDisplayId;
import org.metamechanists.aircraft.utils.id.simple.DisplayGroupId;
import org.metamechanists.aircraft.utils.id.simple.InteractionId;
import org.metamechanists.aircraft.utils.id.simple.TextDisplayId;
import org.metamechanists.aircraft.vehicles.surfaces.ControlSurfaceOrientation;

import java.util.HashMap;
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

    public PersistentDataTraverser(@NotNull CustomId id) {
        this.persistentDataHolder = Bukkit.getEntity(id.getUUID());
    }
    public PersistentDataTraverser(@NotNull UUID id) {
        this.persistentDataHolder = Bukkit.getEntity(id);
    }
    public PersistentDataTraverser(@NotNull PersistentDataHolder entity) {
        this.persistentDataHolder = entity;
    }
    public PersistentDataTraverser(@NotNull ItemStack stack) {
        this.persistentDataHolder = stack.getItemMeta();
    }

    public void save(@NotNull ItemStack stack) {
        stack.setItemMeta((ItemMeta) persistentDataHolder);
    }

    private static @NotNull NamespacedKey getKey(@NotNull String key) {
        return keys.computeIfAbsent(key, k -> new NamespacedKey(Aircraft.getInstance(), key));
    }

    public void unset(@NotNull String key) {
        PersistentDataAPI.remove(persistentDataHolder, getKey(key));
    }

    public void set(@NotNull String key, int value) {
        PersistentDataAPI.setInt(persistentDataHolder, getKey(key), value);
    }
    public void set(@NotNull String key, double value) {
        PersistentDataAPI.setDouble(persistentDataHolder, getKey(key), value);
    }
    public void set(@NotNull String key, boolean value) {
        PersistentDataAPI.setBoolean(persistentDataHolder, getKey(key), value);
    }
    public void set(@NotNull String key, @Nullable String value) {
        if (value == null) {
            PersistentDataAPI.remove(persistentDataHolder, getKey(key));
            return;
        }
        PersistentDataAPI.setString(persistentDataHolder, getKey(key), value);
    }
    public void set(@NotNull String key, @Nullable Vector value) {
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
    public void set(@NotNull String key, @Nullable Vector3f value) {
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
    public void set(@NotNull String key, @Nullable Vector3d value) {
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
    public void set(@NotNull String key, @Nullable Quaterniond value) {
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
        set(key + "w", value.w);
    }
    public void set(@NotNull String key, @Nullable UUID value) {
        if (value == null) {
            PersistentDataAPI.remove(persistentDataHolder, getKey(key));
            return;
        }

        PersistentDataAPI.setString(persistentDataHolder, getKey(key), value.toString());
    }
    public void set(@NotNull String key, @Nullable CustomId value) {
        set(key, value == null ? null : value.toString());
    }
    public void set(@NotNull String key, @Nullable ControlSurfaceOrientation value) {
        set(key + "angle", value == null ? 0 : value.getAngle());
        set(key + "ticksUntilReturn", value == null ? 0 : value.getTicksUntilReturn());
    }
    public void set(@NotNull String key, @NotNull List<UUID> value) {
        set(key + "length", value.size());
        int i = 0;
        for (UUID uuid : value) {
            set(key + i, uuid.toString());
            i++;
        }
    }
    // 'both methods have the same erasure' I hate this language so much......
    public void setCustomIdList(@NotNull String key, @NotNull List<? extends CustomId> value) {
        set(key + "length", value.size());
        int i = 0;
        for (CustomId uuid : value) {
            set(key + i, uuid.toString());
            i++;
        }
    }
    public void setControlSurfaceOrientations(@NotNull String key, @NotNull Map<String, ControlSurfaceOrientation> value) {
        set(key + "length", value.size());
        int i = 0;
        for (Entry<String, ControlSurfaceOrientation> pair : value.entrySet()) {
            set(key + i + "key", pair.getKey());
            set(key + i + "value_angle", pair.getValue().getAngle());
            set(key + i + "value_ticksUntilReturn", pair.getValue().getTicksUntilReturn());
            i++;
        }
    }

    public int getInt(@NotNull String key) {
        return PersistentDataAPI.getInt(persistentDataHolder, getKey(key));
    }
    public double getDouble(@NotNull String key) {
        return PersistentDataAPI.getDouble(persistentDataHolder, getKey(key));
    }
    public boolean getBoolean(@NotNull String key) {
        return PersistentDataAPI.getBoolean(persistentDataHolder, getKey(key));
    }
    public @Nullable String getString(@NotNull String key) {
        return PersistentDataAPI.getString(persistentDataHolder, getKey(key));
    }
    public @Nullable Vector getVector(@NotNull String key) {
        return new Vector(
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "x")),
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "y")),
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "z")));
    }
    public @Nullable Vector3f getVector3f(@NotNull String key) {
        return new Vector3f(
                PersistentDataAPI.getFloat(persistentDataHolder, getKey(key + "x")),
                PersistentDataAPI.getFloat(persistentDataHolder, getKey(key + "y")),
                PersistentDataAPI.getFloat(persistentDataHolder, getKey(key + "z")));
    }
    public @Nullable Vector3d getVector3d(@NotNull String key) {
        return new Vector3d(
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "x")),
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "y")),
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "z")));
    }
    public @Nullable Quaterniond getQuaterniond(@NotNull String key) {
        return new Quaterniond(
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "x")),
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "y")),
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "z")),
                PersistentDataAPI.getDouble(persistentDataHolder, getKey(key + "w")));
    }
    public @Nullable UUID getUuid(@NotNull String key) {
        String uuidString = getString(key);
        if (uuidString == null) {
            return null;
        }

        return UUID.fromString(uuidString);
    }
    public @Nullable BlockDisplayId getBlockDisplayId(@NotNull String key) {
        String uuid = getString(key);
        return uuid == null ? null : new BlockDisplayId(uuid);
    }
    public @Nullable DisplayGroupId getDisplayGroupId(@NotNull String key) {
        String uuid = getString(key);
        return uuid == null ? null : new DisplayGroupId(uuid);
    }
    public @Nullable InteractionId getInteractionId(@NotNull String key) {
        String uuid = getString(key);
        return uuid == null ? null : new InteractionId(uuid);
    }
    public @Nullable TextDisplayId getTextDisplayId(@NotNull String key) {
        String uuid = getString(key);
        return uuid == null ? null : new TextDisplayId(uuid);
    }
    public @Nullable List<UUID> getUuidList(@NotNull String key) {
        int size = getInt(key + "length");
        if (size == 0) {
            return null;
        }
        return IntStream.range(0, size)
                .mapToObj(i -> getString(key + i))
                .filter(Objects::nonNull)
                .map(UUID::fromString).collect(Collectors.toList());
    }
    public @Nullable List<InteractionId> getCustomIdList(@NotNull String key) {
        int size = getInt(key + "length");
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
    public @Nullable Map<String, ControlSurfaceOrientation> getControlSurfaceOrientations(@NotNull String key) {

        int size = getInt(key + "length");
        if (size == 0) {
            return new HashMap<>();
        }
        Map<String, ControlSurfaceOrientation> controlSurfaceOrientations = new HashMap<>();
        IntStream.range(0, size).forEach(i -> {
            String name = getString(key + i + "key");
            ControlSurfaceOrientation orientation = new ControlSurfaceOrientation(getDouble(key + i + "value_angle"), getInt(key + i + "value_ticksUntilReturn"));
            controlSurfaceOrientations.put(name, orientation);
        });
        return controlSurfaceOrientations;
    }
}
