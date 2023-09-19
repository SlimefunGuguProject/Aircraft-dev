package org.metamechanists.aircraft.vehicles;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.utils.Utils;
import org.metamechanists.aircraft.vehicles.components.FixedComponent;
import org.metamechanists.aircraft.vehicles.components.HingeComponent;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedCuboid;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedText;
import org.metamechanists.displaymodellib.models.components.ModelComponent;
import org.metamechanists.displaymodellib.sefilib.entity.display.DisplayGroup;
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
    public Map<String, ModelAdvancedCuboid> getCuboids(final Map<String, ControlSurfaceOrientation> orientations) {
        final Map<String, ModelAdvancedCuboid> cuboids = new HashMap<>();
        fixedComponents.forEach(component -> cuboids.put(component.getName(), component.getCuboid()));
        hingeComponents.forEach(component -> cuboids.put(component.getName(), component.getCuboid(orientations)));
        return cuboids;
    }
    public Map<String, ModelComponent> getHud(final @NotNull Vector3d rotation) {
        final Map<String, ModelComponent> hudComponents = new HashMap<>();

        final Vector3d lookingAtForward = Utils.rotateByEulerAngles(new Vector3d(1, 0, 0), rotation);
        double pitch = lookingAtForward.angle(new Vector3d(lookingAtForward.x, 0, lookingAtForward.z));
        pitch = lookingAtForward.y > 0 ? pitch : -pitch;

        double yaw = new Vector3d(lookingAtForward.x, 0, lookingAtForward.z).angle(new Vector3d(1, 0, 0));
        yaw = lookingAtForward.z > 0 ? -yaw : yaw;

        final Vector3f hudCenter = new Vector3f(1.5F, 1, 0);

        hudComponents.put("horizon_altitude", new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(rotation)
                .translate(hudCenter)
                .rotateBackwards(rotation)
                .rotate(new Vector3d(0, yaw, pitch))
                .facing(BlockFace.WEST)
                .scale(new Vector3f(0.7F, 0.7F, 0.001F))
                .translate(0.5F, 0.35F, 0));
        hudComponents.put("horizon_aircraft", new ModelAdvancedText()
                .text(Component.text("< = [       ] = >").color(TextColor.color(255, 255, 255)))
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(rotation)
                .translate(hudCenter)
                .rotateBackwards(rotation)
                .rotate(new Vector3d(0, yaw, pitch))
                .facing(BlockFace.WEST)
                .scale(new Vector3f(0.7F, 0.7F, 0.001F))
                .translate(0.5F, 0.35F, 0));

        final float horizonAdjustment = (float) (2 * -pitch);
        final Vector3f horizonOffset = new Vector3f(0, horizonAdjustment, 0);
        final float maxHorizonRadius = 1.0F;
        final boolean shouldRenderCenter = Math.abs(horizonAdjustment) < maxHorizonRadius;
        hudComponents.put("horizon_center", new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .text(Component.text("----------------").color(TextColor.color(0, 255, 255)))
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(rotation)
                .translate(new Vector3f(horizonOffset))
                .translate(hudCenter)
                .facing(BlockFace.WEST)
                .scale(shouldRenderCenter ? new Vector3f(0.3F, 0.3F, 0.001F) : new Vector3f())
                .translate(0.5F, 0.5F, 0));
        final int bars = 30;
        final float verticalSpacing = (float) ((PI / 1.14) / (bars));
        for (int i = -bars; i < bars; i++) {
            if (i == 0) {
                continue;
            }
            final float barAdjustment = verticalSpacing * i;
            final Vector3f barOffset = new Vector3f(0, barAdjustment, 0);
            final float totalAdjustment = new Vector3f(barOffset).add(horizonOffset).y;
            final boolean shouldRender = Math.abs(totalAdjustment) < maxHorizonRadius;
            final boolean longBar = i % 5 == 0;
            hudComponents.put("horizon" + i, new ModelAdvancedText()
                    .background(Color.fromARGB(0, 0, 0, 0))
                    .text(Component.text("--------------" + (longBar ? "----" : "")).color(TextColor.color(0, 180, 255)))
                    .brightness(Utils.BRIGHTNESS_ON)
                    .rotate(rotation)
                    .translate(horizonOffset)
                    .translate(hudCenter)
                    .translate(barOffset)
                    .facing(BlockFace.WEST)
                    .scale(shouldRender ? new Vector3f(0.2F, 0.2F, 0.001F) : new Vector3f())
                    .translate(0.5F, 0.5F, 0));
        }
        return hudComponents;
    }

    public void updateHud(final Vector3d rotation, final int altitude, final @NotNull DisplayGroup hudGroup) {
        final Map<String, ModelComponent> hudComponents = getHud(rotation);
        final Map<String, Display> displays = hudGroup.getDisplays();
        hudComponents.forEach((name, component) -> displays.get(name).setTransformationMatrix(hudComponents.get(name).getMatrix()));

        final TextDisplay altitudeText = (TextDisplay) displays.get("horizon_altitude");
        altitudeText.text(Component.text(altitude).color(TextColor.color(0, 255, 0)));
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

    public void applyAngularVelocityDampening(final @NotNull Vector3d angularVelocity) {
        angularVelocity.mul(1.0 - angularVelocityDampening);
    }
}
