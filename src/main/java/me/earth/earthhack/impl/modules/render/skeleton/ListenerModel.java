package me.earth.earthhack.impl.modules.render.skeleton;

import me.earth.earthhack.impl.event.events.render.ModelRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;

final class ListenerModel extends ModuleListener<Skeleton, ModelRenderEvent.Post>
{
    public ListenerModel(Skeleton module)
    {
        super(module, ModelRenderEvent.Post.class);
    }

    @Override
    public void invoke(ModelRenderEvent.Post event)
    {
        if (event.getEntity() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (event.getModel() instanceof ModelBiped)
            {
                ModelBiped model = (ModelBiped) event.getModel();
                float[][] rotations = new float[5][3];

                rotations[0] = getRotations(model.bipedHead);
                rotations[1] = getRotations(model.bipedRightArm);
                rotations[2] = getRotations(model.bipedLeftArm);
                rotations[3] = getRotations(model.bipedRightLeg);
                rotations[4] = getRotations(model.bipedLeftLeg);

                module.rotations.put(player, rotations);
            }
        }
    }

    private float[] getRotations(ModelRenderer model)
    {
        return new float[]
                {
                        model.rotateAngleX,
                        model.rotateAngleY,
                        model.rotateAngleZ
                };
    }

}
