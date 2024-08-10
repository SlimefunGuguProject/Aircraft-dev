package org.metamechanists.aircraft.utils;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import lombok.experimental.UtilityClass;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.metamechanists.aircraft.utils.id.CustomId;
import org.metamechanists.aircraft.utils.id.simple.DisplayGroupId;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@UtilityClass
@SuppressWarnings({"unused", "WeakerAccess"})
public class BlockStorageAPI {
    public @Nullable SlimefunItem check(Location location) {
        return BlockStorage.check(location);
    }

    public @Nullable SlimefunItem check(Block block) {
        return BlockStorage.check(block);
    }

    public void removeData(Location location) {
        BlockStorage.clearBlockInfo(location);
    }
    private void removeData(Location location, String key) {
        BlockStorage.addBlockInfo(location, key, null);
    }

    public boolean hasData(Location location) {
        return BlockStorage.hasBlockInfo(location);
    }
    public boolean hasData(Location location, String key) {
        return hasData(location) && getString(location, key) != null;
    }

    private void set(Location location, String key, String value) {
        BlockStorage.addBlockInfo(location, key, value);
    }
    public void set(Location location, String key, boolean value) {
        set(location, key, Objects.toString(value));
    }
    public void set(Location location, String key, int value) {
        set(location, key, Objects.toString(value));
    }
    public void set(Location location, String key, double value) {
        set(location, key, Objects.toString(value));
    }
    public void set(Location location, String key, BlockFace face) {
        set(location, key, Objects.toString(face));
    }
    public void set(Location location, String key, @Nullable Vector value) {
        if (value == null) {
            removeData(location, key + "x");
            removeData(location, key + "y");
            removeData(location, key + "z");
            return;
        }
        set(location, key + "x", value.getX());
        set(location, key + "y", value.getY());
        set(location, key + "z", value.getZ());
    }
    public void set(Location location, String key, @Nullable UUID value) {
        if (value == null) {
            removeData(location, key);
            return;
        }
        set(location, key, value.toString());
    }
    public void set(Location location, String key, @Nullable CustomId value) {
        if (value == null) {
            removeData(location, key);
            return;
        }
        set(location, key, value.toString());
    }

    private String getString(Location location, String key) {
        return BlockStorage.getLocationInfo(location, key);
    }
    public boolean getBoolean(Location location, String key) {
        return "true".equals(getString(location, key));
    }
    public int getInt(Location location, String key) {
        return hasData(location, key)
                ? Integer.parseInt(getString(location, key))
                : 0;
    }
    public double getDouble(Location location, String key) {
        return hasData(location, key)
                ? Double.parseDouble(getString(location, key))
                : 0;
    }
    public Optional<Vector> getVector(Location location, String key) {
        return Stream.of("x", "y", "z").allMatch(uuid -> hasData(location, key + uuid))
                ? Optional.of(new Vector(getDouble(location, key + "x"), getDouble(location, key + "y"), getDouble(location, key + "z")))
                : Optional.empty();
    }
    public Optional<UUID> getUuid(Location location, String key) {
        return hasData(location, key)
                ? Optional.of(UUID.fromString(getString(location, key)))
                : Optional.empty();
    }
    public Optional<BlockFace> getBlockFace(Location location, String key) {
        return hasData(location, key)
                ? Optional.of(BlockFace.valueOf(getString(location, key)))
                : Optional.empty();
    }
    public Optional<DisplayGroupId> getDisplayGroupId(Location location, String key) {
        return hasData(location, key)
                ? Optional.of(new DisplayGroupId(getString(location, key)))
                : Optional.empty();
    }
}
