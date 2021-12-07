package me.earth.earthhack.impl.commands.gui;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class CommandGui extends GuiScreen
{
    private static final SettingCache<Boolean, BooleanSetting, Commands> BACK =
     Caches.getSetting(Commands.class, BooleanSetting.class, "BackgroundGui", false);
    private static final ResourceLocation BLACK_PNG =
            new ResourceLocation("earthhack:textures/gui/black.png");

    private final CommandChatGui chat = new CommandChatGui();
    private final GuiScreen parent;
    private final int id;

    public CommandGui(GuiScreen parent, int id)
    {
        this.parent = parent;
        this.id = id;
    }

    public void setText(String text)
    {
        this.chat.setText(text);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            super.keyTyped(typedChar, keyCode);
            return;
        }

        this.chat.keyTyped(typedChar, keyCode);
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.chat.handleMouseInput();
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);
        this.buttonList.clear();
        this.buttonList.add(new ExitButton(0, this.width - 24, 5));
        this.chat.setWorldAndResolution(mc, width, height);
        this.chat.setFocused(true);
        this.chat.setText(Commands.getPrefix());
    }

    @Override
    public void onGuiClosed()
    {
        this.chat.onGuiClosed();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        ScaledResolution res = new ScaledResolution(mc);

        if (BACK.getValue())
        {
            this.drawDefaultBackground();
        }
        else
        {
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            this.mc.getTextureManager().bindTexture(BLACK_PNG);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos(0.0D, this.height, 0.0D).tex(0.0D, (float)this.height / 32.0F + (float)0).color(64, 64, 64, 255).endVertex();
            bufferbuilder.pos(this.width, this.height, 0.0D).tex((float)this.width / 32.0F, (float)this.height / 32.0F + (float)0).color(64, 64, 64, 255).endVertex();
            bufferbuilder.pos(this.width, 0.0D, 0.0D).tex((float)this.width / 32.0F, 0).color(64, 64, 64, 255).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0).color(64, 64, 64, 255).endVertex();
            tessellator.draw();
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, res.getScaledHeight() - 48, 0.0f);
        mc.ingameGUI.getChatGUI().drawChat(mc.ingameGUI.getUpdateCounter());
        GlStateManager.popMatrix();
        this.chat.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 0)
        {
            this.parent.confirmClicked(false, id);
        }
    }

}
