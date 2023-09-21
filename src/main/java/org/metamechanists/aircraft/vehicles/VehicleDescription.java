package org.metamechanists.aircraft.vehicles;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.Aircraft;
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
    private record ComponentGroup(double dragCoefficient, double liftCoefficient) {}

    @Getter
    private final String path;
    @Getter
    private final Vector3f relativeCenterOfMass;
    @Getter
    private final double mass;
    @Getter
    private final double momentOfInertia;
    private final double velocityDampening;
    private final double angularVelocityDampening;
    @Getter
    private final double thrust;
    @Getter
    private final double airDensity;
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
        final FixedComponent fixedComponent = new FixedComponent(componentTraverser.name(),
                group.dragCoefficient, group.liftCoefficient, material, size, location, new Vector3f(location).sub(relativeCenterOfMass), rotation);

        if (componentTraverser.get("controlSurface", false)) {
            hingeComponents.add(new HingeComponent(fixedComponent,
                    getVector3d(componentTraverser, "rotationAxis"), componentTraverser.get("rotationRate"), componentTraverser.get("rotationMax"),
                    getChar(componentTraverser, "keyUp"), getChar(componentTraverser, "keyDown")));
        }

        fixedComponents.add(fixedComponent);
    }

    @SuppressWarnings("DataFlowIssue")
    public VehicleDescription(final String path) {
        this.path = path;
        final YamlTraverser traverser = new YamlTraverser(Aircraft.getInstance(), path);
        relativeCenterOfMass = getVector3f(traverser, "centerOfMass");
        mass = traverser.get("mass");
        momentOfInertia = traverser.get("momentOfInertia");
        velocityDampening = traverser.get("velocityDampening");
        angularVelocityDampening = traverser.get("angularVelocityDampening");
        thrust = traverser.get("thrust");
        airDensity = traverser.get("airDensity");

        final Map<String, ComponentGroup> groups = new HashMap<>();
        for (final YamlTraverser group : traverser.getSection("groups").getSections()) {
            groups.put(group.name(), new ComponentGroup(group.get("dragCoefficient"), group.get("liftCoefficient")));
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

    public Vector3f getAbsoluteCenterOfMass(final Vector3d rotation) {
        return Utils.rotateByEulerAngles(relativeCenterOfMass, rotation);
    }

    private static double getPitch(final @NotNull Vector3d rotation) {
        final Vector3d lookingAtForward = Utils.rotateByEulerAngles(new Vector3d(1, 0, 0), rotation);
        final double pitch = lookingAtForward.angle(new Vector3d(lookingAtForward.x, 0, lookingAtForward.z));
        return lookingAtForward.y > 0 ? pitch : -pitch;
    }
    private static double getYaw(final @NotNull Vector3d rotation) {
        final Vector3d lookingAtForward = Utils.rotateByEulerAngles(new Vector3d(1, 0, 0), rotation);
        final double yaw = new Vector3d(lookingAtForward.x, 0, lookingAtForward.z).angle(new Vector3d(1, 0, 0));
        return lookingAtForward.z > 0 ? -yaw : yaw;
    }
    private static ModelAdvancedText getAltitudeIndicator(final @NotNull Vector3f hudCenter, final @NotNull Vector3d rotation) {
        return new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(rotation)
                .translate(hudCenter)
                .rotateBackwards(rotation)
                .rotate(new Vector3d(0, getYaw(rotation), getPitch(rotation)))
                .facing(BlockFace.WEST)
                .scale(new Vector3f(0.4F, 0.4F, 0.001F))
                .translate(0.5F, 0.5F, -0.01F);
    }
    private static ModelAdvancedText getHorizonIndicator(final @NotNull Vector3f hudCenter, final @NotNull Vector3d rotation) {
        return new ModelAdvancedText()
                .text(Component.text("[ = <     > = ]").color(TextColor.color(255, 255, 255)))
                .background(Color.fromARGB(0, 0, 0, 0))
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(rotation)
                .translate(hudCenter)
                .rotateBackwards(rotation)
                .rotate(new Vector3d(0, getYaw(rotation), getPitch(rotation)))
                .facing(BlockFace.WEST)
                .scale(new Vector3f(0.4F, 0.4F, 0.001F))
                .translate(0.5F, 0.5F, -0.01F);
    }
    private static ModelAdvancedText getArtificialHorizonCenter(final @NotNull Vector3f hudCenter, final @NotNull Vector3d rotation, final Vector3f horizonOffset, final boolean shouldRender) {
         return new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .text(Component.text("------------------").color(TextColor.color(0, 255, 255)).decorate(TextDecoration.BOLD))
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(rotation)
                .translate(horizonOffset)
                .translate(hudCenter)
                .facing(BlockFace.WEST)
                .scale(shouldRender ? new Vector3f(0.3F, 0.3F, 0.001F) : new Vector3f())
                .translate(0.5F, 0.5F, 0);
    }
    private static ModelAdvancedText getArtificialHorizonBar(final Component component,
                                                             final @NotNull Vector3f hudCenter, final @NotNull Vector3d rotation, final Vector3f horizonOffset,
                                                             final Vector3f barOffset, final boolean shouldRender) {
        return new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .text(component)
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(rotation)
                .translate(horizonOffset)
                .translate(hudCenter)
                .translate(barOffset)
                .facing(BlockFace.WEST)
                .scale(shouldRender ? new Vector3f(0.2F, 0.2F, 0.001F) : new Vector3f())
                .translate(0.5F, 0.5F, 0);
    }
    private static ModelAdvancedText getArtificialHorizonDegree(final Component component,
                                                                final @NotNull Vector3f hudCenter, final @NotNull Vector3d rotation, final Vector3f horizonOffset,
                                                                final Vector3f barOffset, final boolean shouldRender) {
        return new ModelAdvancedText()
                .background(Color.fromARGB(0, 0, 0, 0))
                .text(component)
                .brightness(Utils.BRIGHTNESS_ON)
                .rotate(rotation)
                .translate(horizonOffset)
                .translate(hudCenter)
                .translate(barOffset)
                .translate(new Vector3f(0, 0, 0.38F))
                .facing(BlockFace.WEST)
                .scale(shouldRender ? new Vector3f(0.2F, 0.2F, 0.001F) : new Vector3f())
                .translate(0.5F, 0.5F, 0);
    }
    public Map<String, ModelComponent> getHud(final @NotNull Vector3d rotation) {
        final Map<String, ModelComponent> hudComponents = new HashMap<>();
        final Vector3f hudCenter = new Vector3f(1, 0, 0);

        hudComponents.put("altitude", getAltitudeIndicator(hudCenter, rotation));
        hudComponents.put("horizon", getHorizonIndicator(hudCenter, rotation));

        final Vector3f horizonOffset = new Vector3f(0, (float) (2 * -getPitch(rotation)), 0);
        final float horizonRadius = 0.4F;
        final boolean shouldRenderCenter = Math.abs(horizonOffset.y) < horizonRadius;

        hudComponents.put("horizon_center", getArtificialHorizonCenter(hudCenter, rotation, horizonOffset, shouldRenderCenter));

        final int bars = 31;
        final float verticalSpacing = (float) ((PI / 1.14) / (bars));
        for (int i = -bars; i < bars; i++) {
            if (i == 0) {
                continue;
            }

            final Vector3f barOffset = new Vector3f(0, verticalSpacing * i, 0);
            final float totalAdjustment = new Vector3f(barOffset).add(horizonOffset).y;
            final boolean longBar = i % 5 == 0;
            final String text = "--------------" + (longBar ? "------" : "");
            final TextColor color = longBar ? TextColor.color(0, 180, 255) : TextColor.color(180, 180, 180);
            final Component component = Component.text(text).color(color);
            final boolean shouldRender = Math.abs(totalAdjustment) < horizonRadius;

            hudComponents.put("horizon_bar_" + i, getArtificialHorizonBar(component, hudCenter, rotation, horizonOffset, barOffset, shouldRender));

            if (longBar) {
                final Component degreeComponent = Component.text(i * (90 / (bars-1)));
                hudComponents.put("horizon_degree_" + i, getArtificialHorizonDegree(degreeComponent, hudCenter, rotation, horizonOffset, barOffset, shouldRender));
            }
        }

        return hudComponents;
    }

    public void updateHud(final Vector3d rotation, final int altitude, final @NotNull DisplayGroup hudGroup) {
        final Map<String, ModelComponent> hudComponents = getHud(rotation);
        final Map<String, Display> displays = hudGroup.getDisplays();
        hudComponents.forEach((name, component) -> displays.get(name).setTransformationMatrix(Utils.getHudMatrix(hudComponents.get(name))));

        final TextDisplay altitudeText = (TextDisplay) displays.get("altitude");
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
