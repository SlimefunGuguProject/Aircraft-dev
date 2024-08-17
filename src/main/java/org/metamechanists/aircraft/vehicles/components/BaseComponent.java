package org.metamechanists.aircraft.vehicles.components;

import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.metamechanists.displaymodellib.models.components.ModelCuboid;
import org.metamechanists.metalib.yaml.YamlTraverser;
import org.metamechanists.metalib.yaml.YamlTraverser.ErrorSetting;


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

    protected BaseComponent(@NotNull YamlTraverser traverser, Vector3f translation) {
        name = traverser.path();
        material = Material.valueOf(traverser.get("material", ErrorSetting.LOG_MISSING_KEY));
        size = traverser.getVector3f("size", ErrorSetting.LOG_MISSING_KEY);
        location = traverser.getVector3f("location", ErrorSetting.LOG_MISSING_KEY).sub(translation);
        rotation = traverser.getVector3d("rotation", ErrorSetting.LOG_MISSING_KEY);
    }

    public static @NotNull BaseComponent fromTraverser(@NotNull YamlTraverser traverser, Vector3f translation) {
        YamlTraverser hingedTraverser = traverser.getSection("hinged", ErrorSetting.NO_BEHAVIOUR);
        if (hingedTraverser != null) {
            return new HingeComponent(traverser, hingedTraverser, translation);
        }
        return new FixedComponent(traverser, translation);
    }

    protected ModelCuboid getCuboid(Vector3d rotation, @NotNull Vector3d translation) {
        return new ModelCuboid()
                .material(material)
                .translate(location)
                .rotate(this.rotation)
                .rotate(rotation)
                .translate(new Vector3f((float) translation.x, (float) translation.y, (float) translation.z))
                .scale(size);
    }
}
