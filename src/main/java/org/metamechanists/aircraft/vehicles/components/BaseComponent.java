package org.metamechanists.aircraft.vehicles.components;

import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.displaymodellib.models.components.ModelCuboid;
import org.metamechanists.metalib.yaml.YamlTraverser;
import org.metamechanists.metalib.yaml.YamlTraverser.ErrorSetting;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseComponent implements Component {
    @Getter
    protected final String name;
    private final Material material;
    @Getter
    protected final Vector3f size;
    @Getter
    private final Vector3f location;
    @Getter
    private final Vector3d rotation;

    protected BaseComponent(@NotNull YamlTraverser traverser, String name, Vector3f location, Vector3d rotation) {
        this.name = name;
        this.material = Material.valueOf(traverser.get("material", ErrorSetting.LOG_MISSING_KEY));
        this.size = traverser.getVector3f("size", ErrorSetting.LOG_MISSING_KEY);
        this.location = location;
        this.rotation = rotation;
    }

    public static @NotNull List<BaseComponent> fromTraverser(@NotNull YamlTraverser traverser, Vector3f translation) {
        YamlTraverser hingedTraverser = traverser.getSection("hinged", ErrorSetting.NO_BEHAVIOUR);
        YamlTraverser propellerTraverser = traverser.getSection("propeller", ErrorSetting.NO_BEHAVIOUR);

        String name = traverser.path();
        boolean mirror = traverser.get("mirror", false);
        Vector3f location = traverser.getVector3f("location", ErrorSetting.LOG_MISSING_KEY).sub(translation);
        Vector3d rotation = traverser.getVector3d("rotation", ErrorSetting.LOG_MISSING_KEY);
        Vector3f mirrorLocation = new Vector3f(location.x, location.y, -location.z);
        Vector3d mirrorRotation = new Vector3d(-rotation.x, -rotation.y, rotation.z);

        List<BaseComponent> components = new ArrayList<>();
        if (hingedTraverser != null) {
            if (mirror) {
                components.add(new HingeComponent(traverser, hingedTraverser, name + "-mirror", mirrorLocation, mirrorRotation));
            }
            components.add(new HingeComponent(traverser, hingedTraverser, name, location, rotation));

        } else if (propellerTraverser != null) {
            if (mirror) {
                components.add(new PropellerComponent(traverser, propellerTraverser, name + "-mirror", mirrorLocation, mirrorRotation));
            }
            components.add(new PropellerComponent(traverser, propellerTraverser, name, location, rotation));

        } else {
            if (mirror) {
                components.add(new FixedComponent(traverser, name + "-mirror", mirrorLocation, mirrorRotation));
            }
            components.add(new FixedComponent(traverser, name, location, rotation));
        }

        return components;
    }

    protected ModelCuboid getCuboid(Vector3d rotation, @NotNull Vector3d translation) {
        return new ModelCuboid()
                .interpolationDuration(1)
                .material(material)
                .translate(location)
                .rotate(this.rotation)
                .translate(new Vector3f((float) translation.x, (float) translation.y, (float) translation.z))
                .rotate(rotation)
                .scale(size);
    }
}
