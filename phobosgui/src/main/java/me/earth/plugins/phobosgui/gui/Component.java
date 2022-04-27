package me.earth.plugins.phobosgui.gui;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.plugins.phobosgui.PhobosColorManager;
import me.earth.plugins.phobosgui.PhobosColorModule;
import me.earth.plugins.phobosgui.PhobosGuiModule;
import me.earth.plugins.phobosgui.PhobosTextManager;
import me.earth.plugins.phobosgui.gui.components.Button;
import me.earth.plugins.phobosgui.gui.components.Item;
import me.earth.plugins.phobosgui.util.PhobosColorUtil;
import me.earth.plugins.phobosgui.util.PhobosRenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Component implements Globals
{
    private final String name;
    private int x;
    private int y;
    private int x2;
    private int y2;
    private int width;
    private int height;
    private boolean open;
    public boolean drag;
    private final List<Item> items = new ArrayList<>();
    private boolean hidden = false;

    public Component(String name, int x, int y, boolean open)
    {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = 88;
        this.height = 18;
        this.open = open;
        setupItems();
    }

    public void setupItems()
    {
        //For the child class
    }

    private void drag(int mouseX, int mouseY)
    {
        if (!drag)
        {
            return;
        }
        x = x2 + mouseX;
        y = y2 + mouseY;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drag(mouseX, mouseY);
        float totalItemHeight = open ? getTotalItemHeight() - 2F : 0F;
        int color = 0xFF888888;
        if(PhobosGuiModule.getInstance().devSettings.getValue())
        {
            color = PhobosGuiModule.getInstance().colorSync.getValue() ? PhobosColorModule.getInstance().getCurrentColorHex() : PhobosColorUtil.toARGB(PhobosGuiModule.getInstance().topRed.getValue(), PhobosGuiModule.getInstance().topGreen.getValue(), PhobosGuiModule.getInstance().topBlue.getValue(), PhobosGuiModule.getInstance().topAlpha.getValue());
        }

        if (PhobosGuiModule.getInstance().rainbowRolling.getValue() && PhobosGuiModule.getInstance().colorSync.getValue() && PhobosColorModule.getInstance().rainbow.getValue())
        {
            PhobosRenderUtil.drawGradientRect(x, (int) (y - 1.5f), width, height - 4, PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp(y, 0, PhobosTextManager.getInstance().scaledHeight)), PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp(y + height - 4, 0, PhobosTextManager.getInstance().scaledHeight)));
        }
        else
        {
            PhobosRenderUtil.drawRect(x, y - 1.5F, (x + width), y + height - 6, color);
        }

        if (open)
        {
            PhobosRenderUtil.drawRect(x, y + 12.5F, (x + width), y + height + totalItemHeight, 0x77000000);
            // TODO: make this use a list and GL11.glCallList
            if (PhobosGuiModule.getInstance().outline.getValue())
            {
                if (PhobosGuiModule.getInstance().rainbowRolling.getValue())
                {
                    GlStateManager.disableTexture2D();
                    GlStateManager.enableBlend();
                    GlStateManager.disableAlpha();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    GlStateManager.shadeModel(7425);
                    GL11.glBegin(GL11.GL_LINES);
                    Color currentColor = new Color(PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp(y, 0, PhobosTextManager.getInstance().scaledHeight)));
                    GL11.glColor4f(currentColor.getRed() / 255f, currentColor.getGreen() / 255f, currentColor.getBlue() / 255f, currentColor.getAlpha() / 255f);
                    GL11.glVertex3f(x + width, y - 1.5f, 0.0f);
                    GL11.glVertex3f(x, y - 1.5f, 0.0f);
                    GL11.glVertex3f(x, y - 1.5f, 0.0f);
                    float currentHeight = getHeight() - 1.5f;
                    for (Item item : getItems())
                    {
                        currentHeight += (item.getHeight() + 1.5f);
                        currentColor = new Color(PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp((int) (y + currentHeight), 0, PhobosTextManager.getInstance().scaledHeight)));
                        GL11.glColor4f(currentColor.getRed() / 255f, currentColor.getGreen() / 255f, currentColor.getBlue() / 255f, currentColor.getAlpha() / 255f);
                        GL11.glVertex3f(x, y + currentHeight, 0.0f);
                        GL11.glVertex3f(x, y + currentHeight, 0.0f);
                    }
                    currentColor = new Color(PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp((int) (y + height + totalItemHeight), 0, PhobosTextManager.getInstance().scaledHeight)));
                    GL11.glColor4f(currentColor.getRed() / 255f, currentColor.getGreen() / 255f, currentColor.getBlue() / 255f, currentColor.getAlpha() / 255f);
                    GL11.glVertex3f(x + width, y + height + totalItemHeight, 0.0f);
                    GL11.glVertex3f(x + width, y + height + totalItemHeight, 0.0f);
                    for (Item item : getItems())
                    {
                        currentHeight -= (item.getHeight() + 1.5f);
                        currentColor = new Color(PhobosGuiModule.getInstance().colorMap.get(MathUtil.clamp((int) (y + currentHeight), 0, PhobosTextManager.getInstance().scaledHeight)));
                        GL11.glColor4f(currentColor.getRed() / 255f, currentColor.getGreen() / 255f, currentColor.getBlue() / 255f, currentColor.getAlpha() / 255f);
                        GL11.glVertex3f(x + width, y + currentHeight, 0.0f);
                        GL11.glVertex3f(x + width, y + currentHeight, 0.0f);
                    }
                    GL11.glVertex3f(x + width, y, 0.0f);
                    GL11.glEnd();
                    GlStateManager.shadeModel(7424);
                    GlStateManager.disableBlend();
                    GlStateManager.enableAlpha();
                    GlStateManager.enableTexture2D();
                }
                else
                {
                    GlStateManager.disableTexture2D();
                    GlStateManager.enableBlend();
                    GlStateManager.disableAlpha();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    GlStateManager.shadeModel(7425);
                    GL11.glBegin(GL11.GL_LINE_LOOP);
                    Color outlineColor = PhobosGuiModule.getInstance().colorSync.getValue() ? new Color(PhobosColorModule.getInstance().getCurrentColorHex()) : new Color(PhobosColorManager.getInstance().getColorAsIntFullAlpha());
                    GL11.glColor4f(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), outlineColor.getAlpha());
                    GL11.glVertex3f(x, y - 1.5f, 0.0f);
                    GL11.glVertex3f(x + width, y - 1.5f, 0.0f);
                    GL11.glVertex3f(x + width, y + height + totalItemHeight, 0.0f);
                    GL11.glVertex3f(x, y + height + totalItemHeight, 0.0f);
                    GL11.glEnd();
                    GlStateManager.shadeModel(7424);
                    GlStateManager.disableBlend();
                    GlStateManager.enableAlpha();
                    GlStateManager.enableTexture2D();
                }
            }
        }

        PhobosTextManager.getInstance().drawStringWithShadow(name, x + 3F, y - 4F - PhobosGui.getInstance().getTextOffset(), 0xFFFFFFFF);

        if (open)
        {
            float y = getY() + getHeight() - 3F;
            for (Item item : getItems())
            {
                if (item.isHidden())
                {
                    item.setLocation(x + 2F, y);
                    item.setWidth(getWidth() - 4);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                    y += item.getHeight() + 1.5F;
                }
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0 && isHovering(mouseX, mouseY))
        {
            x2 = x - mouseX;
            y2 = y - mouseY;
            PhobosGui.getInstance().getComponents().forEach(component ->
            {
                if (component.drag)
                {
                    component.drag = false;
                }
            });
            drag = true;
            return;
        }
        if (mouseButton == 1 && isHovering(mouseX, mouseY))
        {
            open = !open;
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return;
        }

        if (!open)
        {
            return;
        }

        getItems().forEach(item -> {
            item.mouseClicked(mouseX, mouseY, mouseButton);
        });
    }

    public void mouseReleased(final int mouseX, int mouseY, int releaseButton)
    {
        if (releaseButton == 0)
        {
            drag = false;
        }

        if (!open)
        {
            return;
        }

        getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public void onKeyTyped(char typedChar, int keyCode)
    {
        if (!open)
        {
            return;
        }

        getItems().forEach(item -> item.onKeyTyped(typedChar, keyCode));
    }

    public void addButton(Button button)
    {
        items.add(button);
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }

    public boolean isHidden()
    {
        return this.hidden;
    }

    public boolean isOpen() {
        return open;
    }

    public final List<Item> getItems()
    {
        return items;
    }

    private boolean isHovering(int mouseX, int mouseY)
    {
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight() - (open ? 2 : 0);
    }

    private float getTotalItemHeight()
    {
        float height = 0;
        for (Item item : getItems())
        {
            height += item.getHeight() + 1.5F;
        }
        return height;
    }

    public String getName()
    {
        return name;
    }

}
