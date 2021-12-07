package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

public class RenderEntityEvent extends Event {
    private final Render<Entity> renderer;
    private final Entity entity;

    private RenderEntityEvent(Render<Entity> renderer,
                              Entity entity) {
        this.renderer = renderer;
        this.entity = entity;
    }

    public Render<Entity> getRenderer() {
        return renderer;
    }

    public Entity getEntity() {
        return entity;
    }

    public static class Pre extends RenderEntityEvent {
        private final double posX;
        private final double posY;
        private final double posZ;
        private final float entityYaw;
        private final float partialTicks;

        public Pre(Render<Entity> renderer,
                    Entity entity,
                    double posX,
                    double posY,
                    double posZ,
                    float entityYaw,
                    float partialTicks) {
            super(renderer, entity);
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.entityYaw = entityYaw;
            this.partialTicks = partialTicks;
        }

        public double getPosX() {
            return posX;
        }

        public double getPosY() {
            return posY;
        }

        public double getPosZ() {
            return posZ;
        }

        public float getEntityYaw() {
            return entityYaw;
        }

        public float getPartialTicks() {
            return partialTicks;
        }
    }

    public static class Post extends RenderEntityEvent {

        public Post(Render<Entity> renderer,
                     Entity entity) {
            super(renderer, entity);
        }
    }
}
