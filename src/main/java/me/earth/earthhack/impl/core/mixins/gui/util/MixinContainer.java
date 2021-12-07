package me.earth.earthhack.impl.core.mixins.gui.util;

import me.earth.earthhack.impl.core.ducks.util.IContainer;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Container.class)
public abstract class MixinContainer implements IContainer
{
    @Override
    @Accessor(value = "transactionID")
    public abstract void setTransactionID(short id);

    @Override
    @Accessor(value = "transactionID")
    public abstract short getTransactionID();

}
