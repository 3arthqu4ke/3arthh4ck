package me.earth.earthhack.impl.modules.render.nametags;

import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.render.ColorHelper;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StackRenderer implements Globals
{
    private final ItemStack stack;
    private final List<String> enchantTexts;
    private final boolean damageable;
    private final int color;
    private final float percent;

    public StackRenderer(ItemStack stack, boolean damageable)
    {
        this.damageable = damageable;
        if (stack.isItemStackDamageable())
        {
            percent = DamageUtil.getPercent(stack) / 100.0f;
            color = ColorHelper.toColor(percent * 120.0f, 100.0f, 50.0f, 1.0f)
                               .getRGB();
        }
        else
        {
            percent = 100.0f;
            color = 0xffffffff;
        }

        this.stack = stack;
        Set<Enchantment> e = EnchantmentHelper.getEnchantments(stack).keySet();
        this.enchantTexts = new ArrayList<>(e.size());
        for (Enchantment enchantment : e)
        {
            enchantTexts.add(getEnchantText(
                    enchantment,
                    EnchantmentHelper.getEnchantmentLevel(enchantment, stack)));
        }
    }

    public boolean isDamageable()
    {
        return damageable;
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public void renderStack(int x, int y, int enchHeight)
    {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        int height = enchHeight > 4 ? (enchHeight - 4) * 8 / 2 : 0;
        mc.getRenderItem()
          .renderItemAndEffectIntoGUI(stack, x, y + height);
        mc.getRenderItem()
          .renderItemOverlays(mc.fontRenderer, stack, x, y + height);
        mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.disableDepth();
        this.renderEnchants(stack, x, y - 24);
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.popMatrix();
    }

    public void renderStack2D(int x, int y, int enchHeight)
    {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        int height = enchHeight > 4 ? (enchHeight - 4) * 8 / 2 : 0;
        mc.getRenderItem()
                .renderItemAndEffectIntoGUI(stack, x, y + height);
        mc.getRenderItem()
                .renderItemOverlays(mc.fontRenderer, stack, x, y + height);
        mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.disableDepth();
        this.renderEnchants(stack, x, (y*2) + 5);
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.popMatrix();
    }

    public void renderDurability(float x, float y)
    {
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.disableDepth();
        Managers.TEXT.drawStringWithShadow(
                ((int) (percent * 100.0f)) + "%", x * 2, y, color);
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
    }


    public void renderText(int y)
    {
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.disableDepth();
        String name = stack.getDisplayName();

        Managers.TEXT.drawStringWithShadow(
                name,
                (float) (-Managers.TEXT.getStringWidth(name) / 2),
                y,
                0xffffffff);

        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
    }

    public void renderText(float x, float y)
    {
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.disableDepth();
        String name = stack.getDisplayName();

        Managers.TEXT.drawStringWithShadow(
                name,
                x + (float) (-Managers.TEXT.getStringWidth(name) / 2),
                y,
                0xffffffff);

        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
    }

    private void renderEnchants(ItemStack stack, int xOffset, int yOffset)
    {
        for (String enchantment : enchantTexts)
        {
            if (enchantment != null)
            {
                Managers.TEXT.drawStringWithShadow(enchantment,
                                                   xOffset * 2.0f,
                                                   yOffset,
                                                   0xffffffff);
                yOffset += 8;
            }
        }

        if (stack.getItem().equals(Items.GOLDEN_APPLE) && stack.hasEffect())
        {
            Managers.TEXT.drawStringWithShadow(TextColor.RED + "God",
                                               xOffset * 2.0f,
                                               yOffset,
                                               0xffc34d41);
        }
    }

    private String getEnchantText(Enchantment ench, int lvl)
    {
        ResourceLocation resource =
                Enchantment.REGISTRY.getNameForObject(ench);

        String name = resource == null ? ench.getName() : resource.toString();
        int lvlOffset = lvl > 1 ? 12 : 13;

        if (name.length() > lvlOffset)
        {
            name = name.substring(10, lvlOffset);
        }

        if (lvl > 1)
        {
            name += lvl;
        }

        return name.length() < 2 ? name : TextUtil.capitalize(name);
    }

}
