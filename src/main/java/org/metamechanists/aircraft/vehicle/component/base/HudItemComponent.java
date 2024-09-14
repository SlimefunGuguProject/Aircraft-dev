package org.metamechanists.aircraft.vehicle.component.base;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.aircraft.vehicle.VehicleEntity;
import org.metamechanists.kinematiccore.api.entity.KinematicEntity;
import org.metamechanists.kinematiccore.api.entity.KinematicEntitySchema;
import org.metamechanists.kinematiccore.api.storage.StateReader;
import org.metamechanists.metalib.yaml.YamlTraverser;


public abstract class HudItemComponent<T extends HudItemComponent.HudItemComponentSchema> extends ItemComponent<T> {
    @Getter
    public abstract static class HudItemComponentSchema extends KinematicEntitySchema {
        private final HudSection.HudSectionSchema sectionSchema;

        protected HudItemComponentSchema (
                @NotNull String id,
                @NotNull HudSection.HudSectionSchema sectionSchema,
                @NotNull YamlTraverser traverser,
                @NotNull Class<? extends KinematicEntity<?, ?>> kinematicClass,
                @NotNull Class<? extends Entity> entityClass) {
            super(sectionSchema.getId(), Aircraft.class, kinematicClass, entityClass);
            this.sectionSchema = sectionSchema;
        }
    }

    protected HudItemComponent(@NotNull T schema, @NotNull VehicleEntity vehicleEntity) {
        super(schema, () -> {
            Entity entity = vehicleEntity.entity();
            assert entity != null;
            return (ItemDisplay) entity.getLocation().getWorld().spawnEntity(entity.getLocation(), EntityType.ITEM_DISPLAY);
        });
    }

    protected HudItemComponent(@NotNull StateReader reader) {
        super(reader);
    }
}
