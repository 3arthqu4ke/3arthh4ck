package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

/**
 * This class is for items rendered in FIRST PERSON ONLY!
 * Maybe pass baked model in constructor? Idk why but could be useful
 * Also maybe a different event for items with built in renderers?
 */
public class RenderHeldItemEvent extends Event
{

    private final ItemStack stack;

    private RenderHeldItemEvent(ItemStack stack)
    {
        this.stack = stack;
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public static class NonBuiltInRenderer extends RenderHeldItemEvent
    {

        private final IBakedModel model;
        private final RenderItem renderItem;

        private NonBuiltInRenderer(ItemStack stack, IBakedModel model, RenderItem renderItem)
        {
            super(stack);
            this.model = model;
            this.renderItem = renderItem;
        }

        public IBakedModel getModel()
        {
            return model;
        }

        public RenderItem getRenderItem()
        {
            return renderItem;
        }

        public static class Pre extends NonBuiltInRenderer
        {

            public Pre(ItemStack stack, IBakedModel model, RenderItem renderItem)
            {
                super(stack, model, renderItem);
            }

        }

        public static class Post extends NonBuiltInRenderer
        {

            public Post(ItemStack stack, IBakedModel model, RenderItem renderItem)
            {
                super(stack, model, renderItem);
            }

        }

    }

    public static class BuiltInRenderer extends RenderHeldItemEvent
    {

        private final TileEntityItemStackRenderer renderer;

        private BuiltInRenderer(ItemStack stack, TileEntityItemStackRenderer renderer)
        {
            super(stack);
            this.renderer = renderer;
        }

        public TileEntityItemStackRenderer getRenderer()
        {
            return renderer;
        }

        public static class Pre extends BuiltInRenderer
        {

            public Pre(ItemStack stack, TileEntityItemStackRenderer renderer)
            {
                super(stack, renderer);
            }

        }

        public static class Post extends BuiltInRenderer
        {

            public Post(ItemStack stack, TileEntityItemStackRenderer renderer)
            {
                super(stack, renderer);
            }

        }

    }

}
