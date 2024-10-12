package org.metamechanists.aircraft.vehicle.component.base;

import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Pig;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.displaymodellib.models.components.ModelItem;
import org.metamechanists.displaymodellib.models.components.ModelText;
import org.metamechanists.kinematiccore.api.entity.KinematicEntity;
import org.metamechanists.kinematiccore.api.entity.KinematicEntitySchema;
import org.metamechanists.kinematiccore.api.state.StateReader;
import org.metamechanists.kinematiccore.api.state.StateWriter;
import org.metamechanists.metalib.yaml.YamlTraverser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public abstract class HudSection<T extends HudSection.HudSectionSchema> extends KinematicEntity<Interaction, T> {
    @Getter
    public abstract static class HudSectionSchema extends KinematicEntitySchema {
        private final Vector3f position;
        private final Vector3f rotation;

        protected HudSectionSchema(
                @NotNull String id,
                @NotNull YamlTraverser traverser,
                @NotNull Class<? extends KinematicEntity<?, ?>> kinematicClass,
                @NotNull Class<? extends Entity> entityClass) {
            super(id, Aircraft.class, kinematicClass, entityClass);
            this.position = traverser.getVector3f("position");
            this.rotation = traverser.getVector3f("rotation");
        }

        private static ModelText defaultText(@NotNull VehicleEntity vehicleEntity) {
            return Utils.defaultModelText(vehicleEntity.getRotation())
                    .background(Color.fromARGB(0, 0, 0, 0))
                    .brightness(15);
        }

        private static ModelItem defaultCuboid(@NotNull VehicleEntity vehicleEntity) {
            return Utils.defaultModelItem(vehicleEntity.getRotation())
                    .brightness(15);
        }

        public ModelText rollText(@NotNull VehicleEntity vehicleEntity, @NotNull Vector3f offset) {
            return defaultText(vehicleEntity)
                    .translate(position)
                    .translate(offset)
                    .lookAlong(BlockFace.WEST);
        }

        public ModelText rollText(@NotNull VehicleEntity vehicleEntity) {
            return rollText(vehicleEntity, new Vector3f());
        }

        public ModelItem rollCuboid(@NotNull VehicleEntity vehicleEntity) {
            return defaultCuboid(vehicleEntity)
                    .translate(position)
                    .lookAlong(BlockFace.WEST);
        }

        public ModelText rollIndependentText(@NotNull VehicleEntity vehicleEntity) {
            return defaultText(vehicleEntity)
                    .translate(position)
                    .undoRotate(vehicleEntity.getRotation())
                    .rotate(new Vector3d(0, vehicleEntity.yaw(), vehicleEntity.pitch()))
                    .lookAlong(BlockFace.WEST);
        }

        public ModelItem rollIndependentCuboid(@NotNull VehicleEntity vehicleEntity) {
            return defaultCuboid(vehicleEntity)
                    .translate(position)
                    .undoRotate(vehicleEntity.getRotation())
                    .rotate(new Vector3d(0, vehicleEntity.yaw(), vehicleEntity.pitch()))
                    .lookAlong(BlockFace.WEST);
        }
    }

    private final List<UUID> components;

    protected HudSection(@NotNull T schema, @NotNull VehicleEntity vehicleEntity) {
        super(schema, () -> {
            Pig pig = vehicleEntity.entity();
            assert pig != null;
            Interaction interaction =(Interaction) pig.getLocation().getWorld().spawnEntity(pig.getLocation(), EntityType.INTERACTION);
            pig.addPassenger(interaction);
            return interaction;
        });
        components = buildComponents(vehicleEntity);
    }

    protected HudSection(@NotNull StateReader reader) {
        super(reader);
        components = reader.get("components", new ArrayList<>());
    }

    @Override
    public void write(@NotNull StateWriter writer) {
        writer.set("components", components);
    }

    protected abstract List<UUID> buildComponents(@NotNull VehicleEntity vehicleEntity);

    public void update(@NotNull VehicleEntity vehicleEntity) {
        for (UUID uuid : components) {
            if (KinematicEntity.get(uuid) instanceof HudTextComponent<?> component) {
                component.update(vehicleEntity);
            }
            if (KinematicEntity.get(uuid) instanceof HudItemComponent<?> component) {
                component.update(vehicleEntity);
            }
        }
    }
}
