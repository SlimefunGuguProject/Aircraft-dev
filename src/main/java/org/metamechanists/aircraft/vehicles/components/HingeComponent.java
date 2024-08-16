package org.metamechanists.aircraft.vehicles.components;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.aircraft.vehicles.config.Util;
import org.metamechanists.aircraft.vehicles.surfaces.ControlSurfaceOrientation;
import org.metamechanists.aircraft.vehicles.surfaces.VehicleSurface;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedCuboid;
import org.metamechanists.metalib.yaml.YamlTraverser;
import org.metamechanists.metalib.yaml.YamlTraverser.ErrorSetting;

import java.util.Map;
import java.util.Set;

import static java.lang.Math.sin;


public class HingeComponent extends Component {
    private final Vector3d rotationAxis;
    private final double rotationRate;
    private final double rotationMax;
    private final char keyUp;
    private final char keyDown;

    public HingeComponent(
            String name, double dragCoefficient, double liftCoefficient, Material material, Vector3f size, Vector3f location, Vector3d rotation,
            Vector3d rotationAxis, double rotationRate, double rotationMax, char keyUp, char keyDown) {
        super(name, dragCoefficient, liftCoefficient, material, size, location, rotation);
        this.rotationAxis = rotationAxis;
        this.rotationRate = rotationRate;
        this.rotationMax = rotationMax;
        this.keyUp = keyUp;
        this.keyDown = keyDown;
    }

    @SuppressWarnings("DataFlowIssue")
    protected static @NotNull HingeComponent newFromTraverser(@NotNull YamlTraverser traverser, @NotNull YamlTraverser hingedTraverser, Vector3f translation) {
        Material material = Material.valueOf(traverser.get("material", ErrorSetting.LOG_MISSING_KEY));
        Vector3f size = Util.getVector3f(traverser, "size");
        Vector3f location = Util.getVector3f(traverser, "location").sub(translation);
        Vector3d rotation = Util.getVector3d(traverser, "rotation");
        String name = traverser.name();
        double dragCoefficient = traverser.get("dragCoefficient", ErrorSetting.LOG_MISSING_KEY);
        double liftCoefficient = traverser.get("liftCoefficient", ErrorSetting.LOG_MISSING_KEY);

        Vector3d rotationAxis = Util.getVector3d(hingedTraverser, "rotationAxis");
        double rotationRate = hingedTraverser.get("rotationRate", ErrorSetting.LOG_MISSING_KEY);
        double rotationMax = hingedTraverser.get("rotationMax", ErrorSetting.LOG_MISSING_KEY);
        char keyUp = hingedTraverser.get("keyUp", ErrorSetting.LOG_MISSING_KEY);
        char keyDown = hingedTraverser.get("keyDown", ErrorSetting.LOG_MISSING_KEY);

        return new HingeComponent(name, dragCoefficient, liftCoefficient, material, size, location, rotation, rotationAxis, rotationRate, rotationMax, keyUp, keyDown);
    }

    private Vector3d getRotation(@NotNull VehicleState state) {
        return new Quaterniond()
                .fromAxisAngleRad(new Vector3d(rotationAxis), state.orientations.get(name).getAngle())
                .getEulerAnglesXYZ(new Vector3d());
    }

    @Override
    protected Set<VehicleSurface> getSurfaces(@NotNull VehicleState state) {
        return getSurfaces(getRotation(state)); // Effect of translation is very small so we don't model it
    }

    @Override
    public ModelAdvancedCuboid getCuboid(@NotNull VehicleState state) {
        Vector3d translation = new Vector3d(0.0, -size.x / 2.0, 0.0)
                .mul(sin(state.orientations.get(name).getAngle()));
        return getCuboid(getRotation(state), translation);
    }

    @Override
    public void onKey(@NotNull VehicleState state, char key) {
        if (key == keyUp) {
            state.orientations.get(name).adjust(rotationRate, rotationMax);
        } else if (key == keyDown) {
            state.orientations.get(name).adjust(-rotationRate, rotationMax);
        }
    }

    @Override
    public void update(@NotNull VehicleState state) {
        state.orientations.get(name).moveTowardsCenter(rotationRate);
    }
}
