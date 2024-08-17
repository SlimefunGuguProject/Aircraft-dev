package org.metamechanists.aircraft.vehicles;

import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Pig;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.PersistentDataTraverser;
import org.metamechanists.aircraft.utils.id.simple.DisplayGroupId;
import org.metamechanists.aircraft.vehicles.forces.SpatialForce;
import org.metamechanists.displaymodellib.models.components.ModelCuboid;
import org.metamechanists.displaymodellib.sefilib.entity.display.DisplayGroup;
import org.metamechanists.displaymodellib.transformations.TransformationMatrixBuilder;

import java.util.HashSet;
import java.util.Set;


public final class VehicleDebug {
    private VehicleDebug() {}

    public static void tickDebug(Pig pig, VehicleConfig config, VehicleState state, boolean isOnGround) {
        PersistentDataTraverser traverser = new PersistentDataTraverser(pig);
        DisplayGroupId forceArrowGroupId = traverser.getDisplayGroupId("forceArrowGroupId");
        DisplayGroup forceArrowGroup;
        if (forceArrowGroupId == null || forceArrowGroupId.get().isEmpty()) {
            forceArrowGroup = new DisplayGroup(pig.getLocation());
            forceArrowGroupId = new DisplayGroupId(forceArrowGroup.getParentUUID());
            traverser.set("forceArrowGroupId", forceArrowGroupId);
            pig.addPassenger(forceArrowGroup.getParentDisplay());
        } else {
            forceArrowGroup = forceArrowGroupId.get().get();
        }

        Set<String> notUpdated = new HashSet<>(forceArrowGroup.getDisplays().keySet());
        for (SpatialForce force : VehicleForces.getForces(config, state, isOnGround)) {
            processForce(pig, force, notUpdated, forceArrowGroup);
        }

        for (String id : notUpdated) {
            Display display = forceArrowGroup.removeDisplay(id);
            if (display != null) {
                display.remove();
            }
        }
    }

    private static void processForce(Pig pig, @NotNull SpatialForce force, @NotNull Set<String> notUpdated, @NotNull DisplayGroup forceArrowGroup) {
        String id = force.location().toString() + force.type().toString();
        notUpdated.remove(id);
        Display display = forceArrowGroup.getDisplays().get(id);

        if (display == null) {
            Material material = switch (force.type()) {
                case DRAG -> Material.BLUE_CONCRETE;
                case LIFT -> Material.LIME_CONCRETE;
                case WEIGHT -> Material.ORANGE_CONCRETE;
                case THRUST -> Material.PURPLE_CONCRETE;
                case FRICTION -> Material.YELLOW_CONCRETE;
            };

            display = new ModelCuboid()
                    .material(material)
                    .brightness(15)
                    .scale(0.1F, 0.01F, 0.01F)
                    .build(pig.getLocation());
            forceArrowGroup.addDisplay(id, display);
            pig.addPassenger(display);
        }

        display.setTransformationMatrix(new TransformationMatrixBuilder()
                .translate(new Vector3f((float) force.location().x, (float) force.location().y, (float) force.location().z))
                .translate(0.0F, 1.2F, 0.0F)
                .lookAlong(new Vector3f((float) force.force().x, (float) force.force().y, (float) force.force().z))
                .translate(0.0F, 0.0F, (float) force.force().length())
                .scale(0.1F, 0.1F, 2.0F * (float) force.force().length())
                .buildForBlockDisplay());
    }
}
