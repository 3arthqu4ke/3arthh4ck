package me.earth.earthhack.impl.event.events.network;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.event.events.StageEvent;
import net.minecraft.entity.Entity;

public class EntityChunkEvent extends StageEvent {

    private final Entity entity;

    public EntityChunkEvent(Stage stage, Entity entity) {
        super(stage);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

}
