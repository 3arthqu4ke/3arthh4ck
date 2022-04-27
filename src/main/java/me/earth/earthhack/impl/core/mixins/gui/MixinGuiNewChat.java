package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.gui.CommandGui;
import me.earth.earthhack.impl.core.ducks.gui.IChatLine;
import me.earth.earthhack.impl.core.ducks.gui.IGuiNewChat;
import me.earth.earthhack.impl.core.ducks.util.IHoverable;
import me.earth.earthhack.impl.event.events.render.ChatEvent;
import me.earth.earthhack.impl.gui.chat.AbstractTextComponent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.modules.misc.chat.Chat;
import me.earth.earthhack.impl.util.animation.AnimationMode;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import me.earth.earthhack.impl.util.misc.collections.ConvenientStack;
import me.earth.earthhack.impl.util.text.ChatIDs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentKeybind;
import net.minecraft.util.text.TextComponentScore;
import net.minecraft.util.text.TextComponentString;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat implements IGuiNewChat
{
    private static final ModuleCache<Chat> CHAT
     = Caches.getModule(Chat.class);
    private static final ModuleCache<Media> MEDIA
     = Caches.getModule(Media.class);
    private static final SettingCache<Boolean, BooleanSetting, Chat> TIME_STAMPS
     = Caches.getSetting(Chat.class, BooleanSetting.class, "TimeStamps", false);
    private static final SettingCache<Boolean, BooleanSetting, Chat> RAINBOW
     = Caches.getSetting(Chat.class, BooleanSetting.class, "RainbowTimeStamps", false);
    private static final SettingCache<Boolean, BooleanSetting, Chat> CLEAN
     = Caches.getSetting(Chat.class, BooleanSetting.class, "Clean", false);
    private static final SettingCache<Boolean, BooleanSetting, Chat> INFINITE
     = Caches.getSetting(Chat.class, BooleanSetting.class, "Infinite", false);

    private static final ITextComponent INSTEAD = new TextComponentKeybind("");
    
    private ChatLine currentLine  = null;
    private ChatLine currentHover = null;
    private int deleteChatLineID;

    @Shadow
    @Final
    private List<ChatLine> chatLines;

    @Shadow
    @Final
    private List<ChatLine> drawnChatLines;

    @Shadow
    @Final
    private List<String> sentMessages;

    @Shadow
    private int scrollPos;

    @Shadow
    private boolean isScrolled;

    @Final
    @Shadow
    private Minecraft mc;

    @Shadow
    protected abstract void setChatLine(ITextComponent chatComponent,
                                        int chatLineId,
                                        int updateCounter,
                                        boolean displayOnly);
    @Shadow
    public abstract void deleteChatLine(int id);

    @Shadow
    public abstract int getChatWidth();

    @Shadow
    public abstract boolean getChatOpen();

    @Shadow
    public abstract float getChatScale();

    private boolean first = true;

    @Override
    @Accessor(value = "scrollPos")
    public abstract int getScrollPos();

    @Override
    @Accessor(value = "scrollPos")
    public abstract void setScrollPos(int pos);

    @Override
    @Accessor(value = "isScrolled")
    public abstract boolean getScrolled();

    @Override
    @Accessor(value = "isScrolled")
    public abstract void setScrolled(boolean scrolled);

    @Override
    public void invokeSetChatLine(ITextComponent chatComponent,
                                  int chatLineId,
                                  int updateCounter,
                                  boolean displayOnly)
    {
        this.setChatLine(chatComponent, chatLineId, updateCounter, displayOnly);
    }

    @Override
    public void invokeClearChat(boolean sent)
    {
        this.drawnChatLines.clear();
        this.chatLines.clear();

        if (sent)
        {
            this.sentMessages.clear();
        }
    }

    @Override
    public boolean replace(ITextComponent component,
                           int id,
                           boolean wrap,
                           boolean returnFirst)
    {
        boolean set;
        set = setLine(component, id, chatLines, wrap, returnFirst);
        set = setLine(component, id, drawnChatLines, wrap, returnFirst) || set;
        return set;
    }

    boolean setLine(ITextComponent component,
                            int id,
                            List<ChatLine> list,
                            boolean wrap,
                            boolean returnFirst)
    {
        Stack<ITextComponent> wrapped = null;
        if (wrap)
        {
            int max = MathHelper.floor(getChatWidth() / getChatScale());
            wrapped = new ConvenientStack<>(GuiUtilRenderComponents
                    .splitText(component, max, mc.fontRenderer, false, false));
        }

        int last = 0;
        List<Integer> toRemove = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
        {
            ChatLine line = list.get(i);
            if (line.getChatLineID() == id)
            {
                if (wrap)
                {
                    ITextComponent itc = wrapped.pop();
                    if (itc != null)
                    {
                        ((IChatLine) line).setComponent(itc);
                        last = i + 1;
                    }
                    else
                    {
                        toRemove.add(i);
                    }
                }
                else
                {
                    ((IChatLine) line).setComponent(component);
                    if (returnFirst)
                    {
                        return true;
                    }
                }
            }
        }

        if (toRemove.isEmpty())
        {
            boolean infinite = INFINITE.getValue();
            while (infinite && wrap && !wrapped.empty())
            {
                ITextComponent itc = wrapped.pop();
                if (itc != null)
                {
                    ChatLine newLine = new ChatLine(this.mc.ingameGUI.getUpdateCounter(), itc, id);
                    CHAT.get().animationMap.put(newLine, new TimeAnimation(CHAT.get().time.getValue(), -(Minecraft.getMinecraft().fontRenderer.getStringWidth(newLine.getChatComponent().getFormattedText())), 0, false, AnimationMode.LINEAR));
                    list.add(last,
                        newLine);
                    last++;
                }
            }
        }
        else
        {
            toRemove.forEach(i -> list.set(i, null));
            list.removeIf(Objects::isNull);
        }

        return false;
    }
    
    // moderately chinese
    @Inject(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ChatLine;getUpdatedCounter()I"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void drawChatHook(int updateCounter, CallbackInfo ci, int i, int j, float f, boolean flag, float f1, int k, int l, int il, ChatLine chatLine) {
        currentLine = chatLine;
    }

    /**
     * target = {@link FontRenderer#drawStringWithShadow(String,
     * float, float, int)}
     */
    @Redirect(
        method = "drawChat",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;" +
                     "drawStringWithShadow(Ljava/lang/String;FFI)I"))
    public int drawStringWithShadowHook(FontRenderer renderer,
                                        String text,
                                        float x,
                                        float y,
                                        int color)
    {
        TimeAnimation animation = null;
        if (currentLine != null)
        {
            if (CHAT.get().animationMap.containsKey(currentLine))
            {
                animation = CHAT.get().animationMap.get(currentLine);
            }

            if (animation != null)
            {
                animation.add(mc.getRenderPartialTicks());
            }
        }

        String s = MEDIA.returnIfPresent(m -> m.convert(text), text);
        if (CHAT.isEnabled() && TIME_STAMPS.getValue() && currentLine != null)
        {
            String t = ((IChatLine) currentLine).getTimeStamp() + s;
            return renderer.drawStringWithShadow(t, (float) (x + ((animation != null && CHAT.isEnabled() && CHAT.get().animated.getValue()) ? animation.getCurrent() : 0)), y, color);
        }

        return renderer.drawStringWithShadow(s, (float) (x + ((animation != null && CHAT.isEnabled() && CHAT.get().animated.getValue()) ? animation.getCurrent() : 0)), y, color);
    }

    @Inject(method = "getChatOpen", at = @At("HEAD"), cancellable = true)
    public void getChatOpenHook(CallbackInfoReturnable<Boolean> info)
    {
        if (mc.currentScreen instanceof CommandGui)
        {
            info.setReturnValue(true);
        }
    }

    @Inject(
        method = "clearChatMessages",
        at = @At("HEAD"),
        cancellable = true)
    public void clearChatMessagesHook(boolean sent, CallbackInfo info)
    {
        ChatEvent.Clear event = new ChatEvent.Clear(this, sent);
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
        {
            info.cancel();
        }
    }

    @Redirect(
        method = "drawChat",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V",
            ordinal = 0))
    private void drawRectHook(int left, int top, int right, int bottom, int c)
    {
        if (CHAT.isEnabled() && CLEAN.getValue())
        {
            return;
        }

        Gui.drawRect(left,
                     top,
                     right + (CHAT.isEnabled() && TIME_STAMPS.getValue()
                                ? 40
                                : 0),
                     bottom,
                     c);
    }

    @Redirect(
        method = "setChatLine",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;size()I",
            ordinal = 0,
            remap = false))
    public int drawnChatLinesSize(List<ChatLine> list)
    {
        return getChatSize(list);
    }

    @Inject(method = "getChatHeight", at = @At("HEAD"), cancellable = true)
    private void getChatHeightHook(CallbackInfoReturnable<Integer> info)
    {
        if (mc.currentScreen instanceof CommandGui)
        {
            info.setReturnValue(500);
        }
    }

    @Redirect(
        method = "setChatLine",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;size()I",
            ordinal = 2,
            remap = false))
    public int chatLinesSize(List<ChatLine> list)
    {
        return getChatSize(list);
    }

    @Inject(
        method = "printChatMessageWithOptionalDeletion",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiNewChat;setChatLine(Lnet/minecraft/util/text/ITextComponent;IIZ)V",
            shift = At.Shift.AFTER),
        cancellable = true)
    private void loggerHook(ITextComponent chatComponent,
                            int chatLineId,
                            CallbackInfo ci)
    {
        ChatEvent.Log event = new ChatEvent.Log(this);
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled())
        {
            ci.cancel();
        }
    }

    /**
     * target =
     *
     */
    @Redirect(
        method = "printChatMessageWithOptionalDeletion",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiNewChat;" +
                    "setChatLine(" +
                    "Lnet/minecraft/util/text/ITextComponent;IIZ)V"))
    public void setChatLineHook(GuiNewChat gui,
                                ITextComponent chatComponent,
                                int chatLineId,
                                int updateCounter,
                                boolean displayOnly)
    {
        ChatEvent.Send event = new ChatEvent.Send(this,
                                                  chatComponent,
                                                  chatLineId,
                                                  updateCounter,
                                                  displayOnly);
        Bus.EVENT_BUS.post(event);

        if (!event.isCancelled())
        {
            this.setChatLine(event.getChatComponent(),
                             event.getChatLineId(),
                             event.getUpdateCounter(),
                             event.isDisplayOnly());
        }
    }

    @Inject(
        method = "getChatComponent",
        at = @At("HEAD"))
    public void getChatComponentHook(
                                 int mouseX,
                                 int mouseY,
                                 CallbackInfoReturnable<ITextComponent> info)
    {
        first = true;
    }

    @Redirect(
        method = "getChatComponent",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/ChatLine;getChatComponent()Lnet/minecraft/util/text/ITextComponent;"))
    private ITextComponent getHook(ChatLine chatLine)
    {
        currentHover = chatLine;
        return chatLine.getChatComponent();
    }

    @Redirect(
        method = "getChatComponent",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiNewChat;getChatWidth()I"))
    private int getChatComponentChatWidthHook(GuiNewChat gnc)
    {
        return CHAT.isEnabled() && TIME_STAMPS.getValue()
                ? gnc.getChatWidth() + 40
                : gnc.getChatWidth();
    }

    @Redirect(
        method = "getChatComponent",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Iterator;next()Ljava/lang/Object;",
            remap = false))
    private Object getChatComponentInstanceOfHook(
            Iterator<ITextComponent> iterator)
    {
        ITextComponent component = iterator.next();
        if (component instanceof IHoverable
                && !(((IHoverable) component).canBeHovered()))
        {
            // return TextComponent that is not
            // a TextComponentString to pass the
            // instanceof TextComponentString
            return INSTEAD;
        }

        return component;
    }

    /**
     * target = {@link FontRenderer#getStringWidth(String)}
     */
    @Redirect(
        method = "getChatComponent",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;" +
                     "getStringWidth(Ljava/lang/String;)I"))
    public int getStringWidthHook(FontRenderer renderer, String text)
    {
        String s = MEDIA.returnIfPresent(m -> m.convert(text), text);
        // Not sure anymore what the first is good for
        if (CHAT.isEnabled()
                && TIME_STAMPS.getValue()
                && first
                && currentHover != null)
        {
            String t = ((IChatLine) currentHover).getTimeStamp() + s;
            first = false;
            return renderer.getStringWidth(t);
        }

        return renderer.getStringWidth(text);
    }

    @Redirect(
        method = "setChatLine",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiNewChat;scroll(I)V"))
    public void scrollHook(GuiNewChat gui, int amount)
    {
        // TODO see ListenerChat in chat.
        //this.scroll(amount);
    }

    @Inject(
        method = "setChatLine",
        at = @At("HEAD"),
        cancellable = true)
    public void setChatLineHookHead(ITextComponent chatComponent,
                                    int id,
                                    int updateCounter,
                                    boolean displayOnly,
                                    CallbackInfo info)
    {
        if (chatComponent instanceof AbstractTextComponent)
        {
            if (id != 0)
            {
                this.deleteChatLine(id);
            }
            else
            {
                id = ChatIDs.NONE;
            }

            AbstractTextComponent component = (AbstractTextComponent) chatComponent;
            if (component.isWrapping())
            {
                int max = MathHelper.floor(getChatWidth() / getChatScale());
                List<ITextComponent> list = GuiUtilRenderComponents
                    .splitText(component, max, mc.fontRenderer, false, false);
                boolean chatOpen = getChatOpen();

                ChatLine[] references = new ChatLine[list.size()];
                for (int i = 0; i < list.size(); i++)
                {
                    ITextComponent itc = list.get(i);
                    if (chatOpen && this.scrollPos > 0)
                    {
                        this.isScrolled = true;
                        //this.scroll(1);
                    }

                    ChatLine line = new ChatLine(updateCounter, itc, id);
                    CHAT.get().animationMap.put(line, new TimeAnimation(CHAT.get().time.getValue(), -(Minecraft.getMinecraft().fontRenderer.getStringWidth(line.getChatComponent().getFormattedText())), 0, false, AnimationMode.LINEAR));
                    this.drawnChatLines.add(0, line);
                    references[i] = line;
                }

                Managers.WRAP.registerComponent(component, references);
            }
            else
            {
                ChatLine newLine = new ChatLine(updateCounter, chatComponent, id);
                CHAT.get().animationMap.put(newLine, new TimeAnimation(CHAT.get().time.getValue(), -(Minecraft.getMinecraft().fontRenderer.getStringWidth(newLine.getChatComponent().getFormattedText())), 0, false, AnimationMode.LINEAR));
                this.drawnChatLines.add(0,
                         newLine);
            }

            info.cancel();
        }
    }

    private int getChatSize(List<ChatLine> list)
    {
        return CHAT.isEnabled() && INFINITE.getValue()
                || mc.currentScreen instanceof CommandGui
                    ? -2147483647
                    : list.size();
    }

    // The following 4 methods try to patch a rare crash that Grin had.
    // Not sure where exactly but it was a NullPointerException
    // at net.minecraft.client.gui.GuiNewChat.func_146242_c(SourceFile:258)
    @Inject(method = "deleteChatLine", at = @At("HEAD"))
    private void deleteChatLineHook(int id, CallbackInfo info)
    {
        deleteChatLineID = id;
    }

    @Redirect(
        method = "deleteChatLine",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;iterator()Ljava/util/Iterator;",
            remap = false))
    private Iterator<ChatLine> iteratorHook(List<ChatLine> list)
    {
        if (list == null)
        {
            return null;
        }

        return list.iterator();
    }

    @Redirect(
        method = "deleteChatLine",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/ChatLine;getChatLineID()I"))
    private int getChatLineIDHook(ChatLine chatLine)
    {
        if (chatLine == null)
        {
            return deleteChatLineID + 1;
        }

        return chatLine.getChatLineID();
    }

    @Redirect(
        method = "deleteChatLine",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Iterator;hasNext()Z",
            remap = false))
    private boolean hasNextHook(Iterator<ChatLine> iterator)
    {
        if (iterator == null)
        {
            return false;
        }

        return iterator.hasNext();
    }

    //--------------- Rare Crash Patch End ---------------

}
