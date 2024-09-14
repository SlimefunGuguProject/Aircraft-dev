package org.metamechanists.aircraft.vehicle.component.base;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.kinematiccore.api.entity.KinematicEntity;
import org.metamechanists.kinematiccore.api.entity.KinematicEntitySchema;
import org.metamechanists.kinematiccore.api.storage.StateReader;
import org.metamechanists.metalib.yaml.YamlTraverser;


public abstract class HudTextComponent<T extends HudTextComponent.HudTextComponentSchema> extends TextComponent<T> {
    @Getter
    public abstract static class HudTextComponentSchema extends KinematicEntitySchema {
        private final HudSection.HudSectionSchema sectionSchema;

        protected HudTextComponentSchema(
                @NotNull String id,
                @NotNull HudSection.HudSectionSchema sectionSchema,
                @NotNull YamlTraverser traverser,
                @NotNull Class<? extends KinematicEntity<?, ?>> kinematicClass,
                @NotNull Class<? extends Entity> entityClass) {
            super(sectionSchema.getId() + "_" + id, Aircraft.class, kinematicClass, entityClass);
            this.sectionSchema = sectionSchema;
        }
    }

    protected HudTextComponent(@NotNull T schema, @NotNull VehicleEntity vehicleEntity) {
        super(schema, () -> {
            Entity entity = vehicleEntity.entity();
            assert entity != null;
            return (TextDisplay) entity.getLocation().getWorld().spawnEntity(entity.getLocation(), EntityType.TEXT_DISPLAY);
        });
    }

    protected HudTextComponent(@NotNull StateReader reader) {
        super(reader);
    }
}
