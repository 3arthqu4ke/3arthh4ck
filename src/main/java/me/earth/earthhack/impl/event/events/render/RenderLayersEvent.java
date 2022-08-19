package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.event.events.StageEvent;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;

import java.util.List;

public class RenderLayersEvent extends StageEvent
{

    private final Render<EntityLivingBase> render;
    private final EntityLivingBase entity;
    private final List<LayerRenderer<?>> layers;

    public RenderLayersEvent(Render<EntityLivingBase> render,
                             EntityLivingBase entity,
                             List<LayerRenderer<?>> layers,
                             Stage stage)
    {
        super(stage);
        this.render = render;
        this.entity = entity;
        this.layers = layers;
    }

    public Render<EntityLivingBase> getRender()
    {
        return render;
    }

    public EntityLivingBase getEntity()
    {
        return entity;
    }

    public List<LayerRenderer<?>> getLayers()
    {
        return layers;
    }
}
