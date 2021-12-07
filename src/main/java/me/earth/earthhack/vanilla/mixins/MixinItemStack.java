package me.earth.earthhack.vanilla.mixins;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.truedurability.TrueDurability;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class MixinItemStack
{
    // TODO: find out why???
    /** Static initializer for this doesn't get called. */
    private static ModuleCache<TrueDurability> trueDurability;

    @Shadow
    int itemDamage;

    @Inject(method = "<init>(Lnet/minecraft/item/Item;II)V",
            at = @At("RETURN"))
    private void initHook(Item itemIn, int amount, int meta, CallbackInfo ci)
    {
        if (trueDurability == null)
        {
            trueDurability = Caches.getModule(TrueDurability.class);
        }

        this.itemDamage = this.checkDurability(this.itemDamage,
                                               meta);
    }

    @Inject(
        method = "<init>(Lnet/minecraft/nbt/NBTTagCompound;)V",
        at = @At("RETURN"))
    private void initHook1(NBTTagCompound compound, CallbackInfo info)
    {
        if (trueDurability == null)
        {
            trueDurability = Caches.getModule(TrueDurability.class);
        }

        this.itemDamage = this.checkDurability(this.itemDamage,
                                               compound.getShort("Damage"));
    }

    private int checkDurability(int damage, int meta)
    {
        int durability = damage;

        if (trueDurability != null && trueDurability.isEnabled() && meta < 0)
        {
            durability = meta;
        }

        return durability;
    }

}
