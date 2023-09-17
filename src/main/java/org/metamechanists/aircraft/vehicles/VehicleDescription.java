package org.metamechanists.aircraft.vehicles;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.utils.models.ModelComponent;
import org.metamechanists.aircraft.utils.models.ModelCuboid;
import org.metamechanists.aircraft.utils.models.ModelText;
import org.metamechanists.aircraft.vehicles.components.FixedComponent;
import org.metamechanists.aircraft.vehicles.components.HingeComponent;
import org.metamechanists.metalib.sefilib.entity.display.DisplayGroup;
import org.metamechanists.metalib.yaml.YamlTraverser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.PI;


public class VehicleDescription {
    private record ComponentGroup(double density, double dragCoefficient, double liftCoefficient) {}

    @Getter
    private final double mass;
    @Getter
    private final double momentOfInertia;
    private final double velocityDampening;
    private final double angularVelocityDampening;
    private final Set<FixedComponent> fixedComponents = new HashSet<>();
    @Getter
    private final Set<HingeComponent> hingeComponents = new HashSet<>();

    private static @NotNull Vector3d getVector3d(@NotNull final YamlTraverser traverser, final String name) {
        final List<Double> rotationList = traverser.get(name);
        return new Vector3d(rotationList.get(0), rotationList.get(1), rotationList.get(2));
    }
    private static @NotNull Vector3f getVector3f(@NotNull final YamlTraverser traverser, final String name) {
        final Vector3d as3d = getVector3d(traverser, name);
        return new Vector3f((float) as3d.x, (float) as3d.y, (float) as3d.z);
    }
    private static char getChar(@NotNull final YamlTraverser traverser, final String name) {
        return ((CharSequence) traverser.get(name)).charAt(0);
    }

    @SuppressWarnings("DataFlowIssue")
    private void processComponentFromTraverser(final @NotNull YamlTraverser componentTraverser, final @NotNull Map<String, ComponentGroup> groups) {
        final ComponentGroup group = groups.get(componentTraverser.get("group"));
        final Material material = Material.valueOf(componentTraverser.get("material"));
        final Vector3f size = getVector3f(componentTraverser, "size");
        final Vector3f location = getVector3f(componentTraverser, "location");
        final Vector3d rotation = getVector3d(componentTraverser, "rotation");
        final FixedComponent fixedComponent = new FixedComponent(componentTraverser.name(), group.density, group.dragCoefficient, group.liftCoefficient, material, size, location, rotation);

        if (componentTraverser.get("controlSurface", false)) {
            hingeComponents.add(new HingeComponent(fixedComponent,
                    getVector3d(componentTraverser, "rotationAxis"), componentTraverser.get("rotationRate"), componentTraverser.get("rotationMax"),
                    getChar(componentTraverser, "keyUp"), getChar(componentTraverser, "keyDown")));
        }

        fixedComponents.add(fixedComponent);
    }

    @SuppressWarnings("DataFlowIssue")
    public VehicleDescription(final @NotNull YamlTraverser traverser) {
        mass = traverser.get("mass");
        momentOfInertia = traverser.get("momentOfInertia");
        velocityDampening = traverser.get("velocityDampening");
        angularVelocityDampening = traverser.get("angularVelocityDampening");

        final Map<String, ComponentGroup> groups = new HashMap<>();
        for (final YamlTraverser group : traverser.getSection("groups").getSections()) {
            groups.put(group.name(), new ComponentGroup(group.get("density"), group.get("dragCoefficient"), group.get("liftCoefficient")));
        }

        for (final YamlTraverser componentTraverser : traverser.getSection("components").getSections()) {
            processComponentFromTraverser(componentTraverser, groups);
        }
    }

    @SuppressWarnings("SimplifyForEach")
    public Set<VehicleSurface> getSurfaces(final Map<String, ControlSurfaceOrientation> orientations) {
        final Set<VehicleSurface> surfaces = new HashSet<>();
        fixedComponents.forEach(component -> surfaces.addAll(component.getSurfaces()));
        hingeComponents.forEach(component -> surfaces.addAll(component.getSurfaces(orientations)));
        return surfaces;
    }

    @SuppressWarnings("SimplifyForEach")
    public Map<String, ModelCuboid> getCuboids(final Map<String, ControlSurfaceOrientation> orientations) {
        final Map<String, ModelCuboid> cuboids = new HashMap<>();
        fixedComponents.forEach(component -> cuboids.put(component.getName(), component.getCuboid()));
        hingeComponents.forEach(component -> cuboids.put(component.getName(), component.getCuboid(orientations)));
        return cuboids;
    }
    public Map<String, ModelComponent> getHud(final @NotNull Quaterniond rotation) {
        final Map<String, ModelComponent> hudComponents = new HashMap<>();
        hudComponents.put("horizon_altitude", new ModelText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .size(new Vector3d(0.7, 0.7, 0.7))
                .location(new Vector3d(0, 1, -2))
                .facing(BlockFace.WEST));

        final Vector3d rollAdjustment = new Vector3d(-rotation.getEulerAnglesYXZ(new Vector3d()).x, 0, 0);
        hudComponents.put("horizon_aircraft", new ModelText()
                .text(Component.text("< = [       ] = >").color(TextColor.color(255, 255, 255)))
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .size(new Vector3d(0.7, 0.7, 0.7))
                .location(new Vector3d(0, 1, -2))
                .rotation(rollAdjustment)
                .facing(BlockFace.WEST));


        final Vector3d lookingAt = new Vector3d(1, 0, 0).rotate(rotation);
        final Vector3d lookingAtWithoutY = new Vector3d(lookingAt.x, 0, lookingAt.z);
        final double adjustment = 2 * lookingAt.angle(lookingAtWithoutY);
        final Vector3d horizonOffset = new Vector3d(0, lookingAt.y < 0 ? adjustment : -adjustment, 0);
        hudComponents.put("horizon_center", new ModelText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .text(Component.text("----------------").color(TextColor.color(0, 255, 255)))
                .brightness(Utils.BRIGHTNESS_ON)
                .facing(BlockFace.WEST)
                .size(new Vector3d(0.3, 0.3, 0.6))
                .location(new Vector3d(horizonOffset).add(new Vector3d(0, 1, -2))));
        final int bars = 61;
        final double verticalSpacing = (PI / 1.14) / (bars / 2);
        for (int i = 0; i < bars; i++) {
            if (i == 7) {
                continue;
            }
            hudComponents.put("horizon" + i, new ModelText()
                    .background(Color.fromARGB(0, 0, 0, 0))
                    .text(Component.text("--------------").color(TextColor.color(0, 180, 255)))
                    .brightness(Utils.BRIGHTNESS_ON)
                    .facing(BlockFace.WEST)
                    .size(new Vector3d(0.2, 0.2, 0.4))
                    .location(new Vector3d(horizonOffset).add(new Vector3d(0, 1 - ((bars / 2) * verticalSpacing) + (verticalSpacing * i), -2))));
        }
        return hudComponents;
    }
    public void updateHud(final Quaterniond rotation, final int altitude, final @NotNull DisplayGroup hudGroup) {
        final Map<String, ModelComponent> hudComponents = getHud(rotation);
        hudComponents.forEach((name, component) -> hudGroup.getDisplays().get(name).setTransformationMatrix(hudComponents.get(name).getMatrix(rotation)));

        final TextDisplay altitudeText = (TextDisplay) hudGroup.getDisplays().get("horizon_altitude");
        altitudeText.text(Component.text(altitude).color(TextColor.color(0, 255, 0)));
        altitudeText.setAlignment(TextAlignment.CENTER);
    }

    public void adjustHingeComponents(final Map<String, ControlSurfaceOrientation> orientations, final char key) {
        hingeComponents.forEach(component -> component.useKey(orientations, key));
    }

    public void moveHingeComponentsToCenter(final Map<String, ControlSurfaceOrientation> orientations) {
        hingeComponents.forEach(component -> component.moveTowardsCenter(orientations));
    }

    public Map<String, ControlSurfaceOrientation> initializeOrientations() {
        return hingeComponents.stream().collect(Collectors.toMap(HingeComponent::getName, component -> new ControlSurfaceOrientation(), (name, orientation) -> orientation));
    }

    public void applyVelocityDampening(final @NotNull Vector3d velocity) {
        velocity.mul(1.0 - velocityDampening);
    }

    public void applyAngularVelocityDampening(final @NotNull Quaterniond angularVelocity) {
        if (angularVelocity.angle() != 0) {
            angularVelocity.rotateAxis(-angularVelocity.angle()*angularVelocityDampening, angularVelocity.x, angularVelocity.y, angularVelocity.z);
        }
    }
}
