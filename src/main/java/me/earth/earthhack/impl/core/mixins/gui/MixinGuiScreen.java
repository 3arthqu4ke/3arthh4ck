package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.util.IClickEvent;
import me.earth.earthhack.impl.core.ducks.util.IHoverEvent;
import me.earth.earthhack.impl.core.ducks.util.IStyle;
import me.earth.earthhack.impl.event.events.render.ToolTipEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen extends Gui implements GuiYesNoCallback
{
    private static final ModuleCache<NoRender> NO_RENDER =
            Caches.getModule(NoRender.class);
    private static final ResourceLocation BLACK_LOC =
            new ResourceLocation("earthhack:textures/gui/black.png");

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    @Final
    private static Set<String> PROTOCOLS;

    @Shadow
    public Minecraft mc;

    @Shadow
    private URI clickedLinkURI;

    @Shadow
    public int width;

    @Shadow
    public int height;

    @Shadow
    protected abstract void openWebLink(URI url);

    @Shadow
    protected abstract void setText(String newChatText,
                                    boolean shouldOverwrite);

    @Shadow
    public abstract void sendChatMessage(String msg, boolean addToChat);

    @Shadow
    public static boolean isShiftKeyDown()
    {
        /* Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54); */
        throw new IllegalStateException("isShiftKeyDown was not shadowed!");
    }

    @Shadow
    protected FontRenderer fontRenderer;

    @Inject(
        method = "renderToolTip",
        at = @At("HEAD"),
        cancellable = true)
    public void renderToolTipHook(ItemStack stack,
                                  int x,
                                  int y,
                                  CallbackInfo info)
    {
        ToolTipEvent event = new ToolTipEvent(stack, x, y);
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
        {
            info.cancel();
        }
    }

    @Redirect(
        method = "drawBackground",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V"))
    public void bindTextureHook(TextureManager textureManager, ResourceLocation resource)
    {
        if (NO_RENDER.get().isEnabled()
                && NO_RENDER.get().defaultBackGround.getValue())
        {
            textureManager.bindTexture(BLACK_LOC);
            return;
        }

        textureManager.bindTexture(OPTIONS_BACKGROUND);
    }

    /**
     * target = {@link GuiScreen#sendChatMessage(String, boolean)}
     */
    @Inject(
            method = "handleComponentClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiScreen;" +
                            "sendChatMessage(Ljava/lang/String;Z)V",
                    shift = At.Shift.BEFORE),
            cancellable = true)
    public void handleComponentClick(ITextComponent component,
                                     CallbackInfoReturnable<Boolean> info)
    {
        IClickEvent event = (IClickEvent) component.getStyle().getClickEvent();
        if (event != null && event.getRunnable() != null)
        {
            event.getRunnable().run();
            info.setReturnValue(true);
        }
    }

    /**
     * Handles clicks for
     * {@link MixinGuiChat#mouseClickedHook(int, int, int, CallbackInfo)}.
     *
     * @param component the component to handle
     * @param button the mouse button.
     * @return <tt>true</tt> if handled successfully
     */
    protected boolean handleClick(ITextComponent component, int button)
    {
        if (component == null)
        {
            return false;
        }

        IStyle style = (IStyle) component.getStyle();
        ClickEvent event = null;

        if (button == 1)
        {
            event = style.getRightClickEvent();
        }
        else if (button == 2)
        {
            event = style.getMiddleClickEvent();
        }

        if (isShiftKeyDown())
        {
            String insertion = null;
            if (button == 1)
            {
                insertion = style.getRightInsertion();
            }
            else if (button == 2)
            {
                insertion = style.getMiddleInsertion();
            }

            if (insertion != null)
            {
                this.setText(insertion, false);
            }
        }
        else if (event != null)
        {
            if (event.getAction() == ClickEvent.Action.OPEN_URL)
            {
                if (!this.mc.gameSettings.chatLinks)
                {
                    return false;
                }

                try
                {
                    URI uri = new URI(event.getValue());
                    String s = uri.getScheme();

                    if (s == null)
                    {
                        throw new URISyntaxException(
                                event.getValue(), "Missing protocol");
                    }

                    if (!PROTOCOLS.contains(s.toLowerCase(Locale.ROOT)))
                    {
                        throw new URISyntaxException(
                                event.getValue(), "Unsupported protocol: "
                                + s.toLowerCase(Locale.ROOT));
                    }

                    if (this.mc.gameSettings.chatLinksPrompt)
                    {
                        this.clickedLinkURI = uri;
                        this.mc.displayGuiScreen(
                            new GuiConfirmOpenLink(this,
                                                   event.getValue(),
                                                   31102009,
                                                   false));
                    }
                    else
                    {
                        this.openWebLink(uri);
                    }
                }
                catch (URISyntaxException urisyntaxexception)
                {
                    LOGGER.error("Can't open url for {}",
                                event,
                                urisyntaxexception);
                }
            }
            else if (event.getAction() == ClickEvent.Action.OPEN_FILE)
            {
                URI uri1 = (new File(event.getValue())).toURI();
                this.openWebLink(uri1);
            }
            else if (event.getAction() == ClickEvent.Action.SUGGEST_COMMAND)
            {
                this.setText(event.getValue(), true);
            }
            else if (event.getAction() == ClickEvent.Action.RUN_COMMAND)
            {
                if (((IClickEvent) event).getRunnable() != null)
                {
                    ((IClickEvent) event).getRunnable().run();
                    return true;
                }

                this.sendChatMessage(event.getValue(), false);
            }
            else
            {
                LOGGER.error("Don't know how to handle {}", event);
            }

            return true;
        }

        return false;
    }

    @Inject(
        method = "handleComponentHover",
        at = @At(value = "HEAD"),
        cancellable = true)
    public void handleComponentHoverHook(ITextComponent component,
                                          int x,
                                          int y,
                                          CallbackInfo info)
    {
        if (component != null && component.getStyle().getHoverEvent() != null)
        {
            HoverEvent event = component.getStyle().getHoverEvent();
            if (event.getAction() == HoverEvent.Action.SHOW_TEXT
                    && !((IHoverEvent) event).hasOffset())
            {
                drawHoveringTextShadow(
                        fontRenderer.listFormattedStringToWidth(
                        event.getValue().getFormattedText(),
                        Math.max(this.width / 2, 200)),
                        x,
                        y,
                        width,
                        height,
                        -1,
                        fontRenderer);
                GlStateManager.disableLighting();
                info.cancel();
            }
        }
    }

    /** Forge method. */
    @SuppressWarnings("SameParameterValue")
    private void drawHoveringTextShadow(List<String> textLines,
                                        int mouseX,
                                        int mouseY,
                                        int screenWidth,
                                        int screenHeight,
                                        int maxTextWidth,
                                        FontRenderer font)
    {
        if (!textLines.isEmpty())
        {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int tttW = 0; // tooltipWidth

            for (String textLine : textLines)
            {
                int textLineWidth = font.getStringWidth(textLine);
                if (textLineWidth > tttW)
                {
                    tttW = textLineWidth;
                }
            }

            boolean needsWrap = false;

            int ttX = mouseX + 12; // tooltipX
            if (ttX + tttW + 4 > screenWidth)
            {
                ttX = mouseX - 16 - tttW;
                if (ttX < 4) // if the tooltip doesn't fit on the screen
                {
                    if (mouseX > screenWidth / 2)
                    {
                        tttW = mouseX - 12 - 8;
                    }
                    else
                    {
                        tttW = screenWidth - 16 - mouseX;
                    }
                    needsWrap = true;
                }
            }

            if (maxTextWidth > 0 && tttW > maxTextWidth)
            {
                tttW = maxTextWidth;
                needsWrap = true;
            }

            if (needsWrap)
            {
                int wrappedTooltipWidth = 0;
                List<String> wrappedTextLines = new ArrayList<>();
                for (String textLine : textLines)
                {
                    List<String> wrappedLine = font.listFormattedStringToWidth(
                            textLine, tttW);

                    for (String line : wrappedLine)
                    {
                        int lineWidth = font.getStringWidth(line);
                        if (lineWidth > wrappedTooltipWidth)
                        {
                            wrappedTooltipWidth = lineWidth;
                        }
                        wrappedTextLines.add(line);
                    }
                }
                tttW = wrappedTooltipWidth;
                textLines = wrappedTextLines;

                if (mouseX > screenWidth / 2)
                {
                    ttX = mouseX - 16 - tttW;
                }
                else
                {
                    ttX = mouseX + 12;
                }
            }

            int ttY = mouseY - 12; // tooltipY
            int ttH = 8;           // tooltipHeight

            if (textLines.size() > 1)
            {
                ttH += (textLines.size() - 1) * 10;
            }

            if (ttY < 4)
            {
                ttY = 4;
            }
            else if (ttY + ttH + 4 > screenHeight)
            {
                ttY = screenHeight - ttH - 4;
            }

            final int zLevel = 300;
            int bgc = 0xF0100010;  // backGroundColor
            int bgcs = 0x505000FF; // backGroundColorStart
            int bgce =             // backGroundColorEnd
                (bgcs & 0xFEFEFE) >> 1
                    | bgcs & 0xFF000000;

            drawGradientRectForge(
                zLevel, ttX - 3, ttY - 4, ttX + tttW + 3, ttY - 3,
                    bgc, bgc);
            drawGradientRectForge(
                zLevel, ttX - 3, ttY + ttH + 3, ttX + tttW + 3, ttY + ttH + 4,
                    bgc, bgc);
            drawGradientRectForge(
                zLevel, ttX - 3, ttY - 3, ttX + tttW + 3, ttY + ttH + 3,
                    bgc, bgc);
            drawGradientRectForge(
                zLevel, ttX - 4, ttY - 3, ttX - 3, ttY + ttH + 3,
                    bgc, bgc);
            drawGradientRectForge(
                zLevel, ttX + tttW + 3, ttY - 3, ttX + tttW + 4, ttY + ttH + 3,
                    bgc, bgc);
            drawGradientRectForge(
                zLevel, ttX - 3, ttY - 3 + 1, ttX - 3 + 1, ttY + ttH + 3 - 1,
                    bgcs, bgce);
            drawGradientRectForge(
                zLevel, ttX + tttW + 2, ttY - 3 + 1, ttX + tttW + 3,
                    ttY + ttH + 3 - 1, bgcs, bgce);
            drawGradientRectForge(
                zLevel, ttX - 3, ttY - 3, ttX + tttW + 3, ttY - 3 + 1,
                    bgcs, bgcs);
            drawGradientRectForge(
                zLevel, ttX - 3, ttY + ttH + 2, ttX + tttW + 3, ttY + ttH + 3,
                    bgce, bgce);

            for (String line : textLines)
            {
                font.drawStringWithShadow(line, (float) ttX, (float) ttY, -1);
                ttY += 10;
            }

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    /** Forge method. */
    @SuppressWarnings("SameParameterValue")
    private static void drawGradientRectForge(int zLevel,
                                              int left,
                                              int top,
                                              int right,
                                              int bottom,
                                              int startColor,
                                              int endColor)
    {
        float startAlpha = (float)(startColor >> 24 & 255) / 255.0F;
        float startRed   = (float)(startColor >> 16 & 255) / 255.0F;
        float startGreen = (float)(startColor >>  8 & 255) / 255.0F;
        float startBlue  = (float)(startColor       & 255) / 255.0F;
        float endAlpha   = (float)(endColor   >> 24 & 255) / 255.0F;
        float endRed     = (float)(endColor   >> 16 & 255) / 255.0F;
        float endGreen   = (float)(endColor   >>  8 & 255) / 255.0F;
        float endBlue    = (float)(endColor         & 255) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);

        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(right,    top, zLevel)
              .color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos( left,    top, zLevel)
              .color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos( left, bottom, zLevel)
              .color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        buffer.pos(right, bottom, zLevel)
              .color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }


}
