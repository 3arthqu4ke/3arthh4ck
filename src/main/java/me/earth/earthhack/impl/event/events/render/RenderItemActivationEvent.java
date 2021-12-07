package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;

public class RenderItemActivationEvent extends Event
{

    private RenderItem renderItem;
    private final ItemStack stack;
    private final ItemCameraTransforms.TransformType type;

    public RenderItemActivationEvent(RenderItem renderItem, ItemStack stack, ItemCameraTransforms.TransformType type)
    {
        this.renderItem = renderItem;
        this.stack = stack;
        this.type = type;
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public ItemCameraTransforms.TransformType getType()
    {
        return type;
    }

    public RenderItem getRenderItem()
    {
        return renderItem;
    }

}
