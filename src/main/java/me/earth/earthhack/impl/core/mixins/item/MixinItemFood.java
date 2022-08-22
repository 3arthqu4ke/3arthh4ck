package me.earth.earthhack.impl.core.mixins.item;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.misc.EatEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFood.class)
public abstract class MixinItemFood
{
    @Inject(
        method = "onItemUseFinish",
        at = @At("HEAD"))
    public void onItemUseFinishHook(ItemStack stack,
                                     World world,
                                     EntityLivingBase entity,
                                     CallbackInfoReturnable<ItemStack> info)
    {
        if (entity instanceof EntityPlayer)
        {
            Bus.EVENT_BUS.post(new EatEvent(stack, entity));
        }
    }

}
