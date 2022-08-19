package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.entity.Entity;

public class RenderEntityInWorldEvent extends Event {

    private final Entity entity;

    private RenderEntityInWorldEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public static class Pre extends RenderEntityInWorldEvent {
        private final float partialTicks;

        public Pre(Entity entity,
                   float partialTicks) {
            super(entity);
            this.partialTicks = partialTicks;
        }

        public float getPartialTicks() {
            return partialTicks;
        }
    }

    public static class Post extends RenderEntityInWorldEvent {

        public Post(Entity entity) {
            super(entity);
        }
    }
}
