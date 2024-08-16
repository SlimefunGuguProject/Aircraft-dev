package org.metamechanists.aircraft.vehicles.components;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.aircraft.vehicles.VehicleState;
import org.metamechanists.aircraft.vehicles.config.Util;
import org.metamechanists.aircraft.vehicles.surfaces.VehicleSurface;
import org.metamechanists.displaymodellib.models.components.ModelAdvancedCuboid;
import org.metamechanists.metalib.yaml.YamlTraverser;
import org.metamechanists.metalib.yaml.YamlTraverser.ErrorSetting;

import java.util.Set;


public class FixedComponent extends Component {

    public FixedComponent(String name, double dragCoefficient, double liftCoefficient, Material material, Vector3f size, Vector3f location, Vector3d rotation) {
        super(name, dragCoefficient, liftCoefficient, material, size, location, rotation);
    }

    @SuppressWarnings("DataFlowIssue")
    protected static @NotNull FixedComponent newFromTraverser(@NotNull YamlTraverser traverser, Vector3f translation) {
        Material material = Material.valueOf(traverser.get("material", ErrorSetting.LOG_MISSING_KEY));
        Vector3f size = Util.getVector3f(traverser, "size");
        Vector3f location = Util.getVector3f(traverser, "location").sub(translation);
        Vector3d rotation = Util.getVector3d(traverser, "rotation");
        String name = traverser.name();
        double dragCoefficient = traverser.get("dragCoefficient", ErrorSetting.LOG_MISSING_KEY);
        double liftCoefficient = traverser.get("liftCoefficient", ErrorSetting.LOG_MISSING_KEY);
        return new FixedComponent(name, dragCoefficient, liftCoefficient, material, size, location, rotation);
    }

    @Override
    protected Set<VehicleSurface> getSurfaces(@NotNull VehicleState state) {
        return getSurfaces(new Vector3d());
    }

    @Override
    public ModelAdvancedCuboid getCuboid(@NotNull VehicleState state) {
        return getCuboid(new Vector3d(), new Vector3d());
    }

    @Override
    public void onKey(@NotNull VehicleState state, char key) {}

    @Override
    public void update(@NotNull VehicleState state) {}
}
