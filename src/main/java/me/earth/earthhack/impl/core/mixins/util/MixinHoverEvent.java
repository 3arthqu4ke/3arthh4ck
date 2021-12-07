package me.earth.earthhack.impl.core.mixins.util;

import me.earth.earthhack.impl.core.ducks.util.IHoverEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HoverEvent.class)
public abstract class MixinHoverEvent implements IHoverEvent
{
    private boolean offset = true;

    @Override
    public HoverEvent setOffset(boolean offset)
    {
        this.offset = offset;
        return HoverEvent.class.cast(this);
    }

    @Override
    public boolean hasOffset()
    {
        return offset;
    }

}
