package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;

public class ModelRenderEvent extends Event
{
    private final RenderLivingBase<?> renderLiving;
    private final EntityLivingBase entity;
    private final ModelBase model;
    private final float limbSwing;
    private final float limbSwingAmount;
    private final float ageInTicks;
    private final float netHeadYaw;
    private final float headPitch;
    private final float scale;

    private ModelRenderEvent(RenderLivingBase<?> renderLiving,
                             EntityLivingBase entity,
                             ModelBase model, float limbSwing,
                             float limbSwingAmount, float ageInTicks,
                             float netHeadYaw, float headPitch, float scale)
    {
        this.renderLiving = renderLiving;
        this.entity       = entity;
        this.model        = model;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
        this.scale = scale;
    }

    public RenderLivingBase<?> getRenderLiving()
    {
        return renderLiving;
    }

    public EntityLivingBase getEntity()
    {
        return entity;
    }

    public ModelBase getModel()
    {
        return model;
    }

    public float getLimbSwing()
    {
        return limbSwing;
    }

    public float getLimbSwingAmount()
    {
        return limbSwingAmount;
    }

    public float getAgeInTicks()
    {
        return ageInTicks;
    }

    public float getNetHeadYaw()
    {
        return netHeadYaw;
    }

    public float getHeadPitch()
    {
        return headPitch;
    }

    public float getScale()
    {
        return scale;
    }

    public static class Pre extends ModelRenderEvent
    {
        public Pre(RenderLivingBase<?> renderLiving,
                      EntityLivingBase entity,
                      ModelBase model,
                      float limbSwing,
                      float limbSwingAmount,
                      float ageInTicks,
                      float netHeadYaw,
                      float headPitch,
                      float scale)
        {
            super(renderLiving, entity, model, limbSwing, limbSwingAmount,
                  ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    public static class Post extends ModelRenderEvent
    {
        public Post(RenderLivingBase<?> renderLiving,
                   EntityLivingBase entity,
                   ModelBase model,
                   float limbSwing,
                   float limbSwingAmount,
                   float ageInTicks,
                   float netHeadYaw,
                   float headPitch,
                   float scale)
        {
            super(renderLiving, entity, model, limbSwing, limbSwingAmount,
                  ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

}
