package org.metamechanists.aircraft.vehicle;

import org.bukkit.entity.Interaction;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.aircraft.Aircraft;
import org.metamechanists.displaymodellib.builders.InteractionBuilder;
import org.metamechanists.kinematiccore.api.entity.KinematicEntity;
import org.metamechanists.kinematiccore.api.entity.KinematicEntitySchema;
import org.metamechanists.kinematiccore.api.state.StateReader;
import org.metamechanists.kinematiccore.api.state.StateWriter;

import java.util.UUID;


public class VehicleInteractor extends KinematicEntity<Interaction, KinematicEntitySchema> {
    private final UUID vehicleEntity;

    public VehicleInteractor(@NotNull VehicleEntity vehicleEntity) {
        super(vehicleEntity.schema().getInteractorSchema(), () -> {
            Pig pig = vehicleEntity.entity();
            assert pig != null;
            Interaction interaction = new InteractionBuilder()
                .width(1.2F)
                .height(1.2F)
                .build(pig.getLocation());
            pig.addPassenger(interaction);
            return interaction;
        });

        this.vehicleEntity = vehicleEntity.uuid();
    }

    public VehicleInteractor(StateReader reader) {
        super(reader);
        vehicleEntity = reader.get("vehicleEntity", UUID.class);
    }

    @Override
    public void write(@NotNull StateWriter writer) {
        writer.set("vehicleEntity", vehicleEntity);
    }

    @Override
    public void onRightClick(Player player) {
        if (player.isInsideVehicle()) {
            player.eject();
        }

        VehicleEntity vehicleEntity = (VehicleEntity) KinematicEntity.get(this.vehicleEntity);
        assert vehicleEntity != null;
        vehicleEntity.mount(player);
    }
}
