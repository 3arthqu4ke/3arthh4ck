package me.earth.earthhack.impl.modules.misc.settingspoof;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.util.EnumHandSide;

public class SettingSpoof extends Module
{
    protected final Setting<Boolean> spoofLanguage =
        register(new BooleanSetting("Spoof-Language", false));
    protected final Setting<String> language =
        register(new StringSetting("Language", "en_us"));
    protected final Setting<Boolean> spoofRender =
        register(new BooleanSetting("Spoof-RenderDistance", false));
    protected final Setting<Integer> renderDist =
        register(new NumberSetting<>("RenderDistance", 32, -1, 128));
    protected final Setting<Boolean> spoofColors =
        register(new BooleanSetting("Spoof-ChatColors", false));
    protected final Setting<Boolean> chatColors =
        register(new BooleanSetting("ChatColors", true));
    protected final Setting<Boolean> spoofChat =
        register(new BooleanSetting("Spoof-Chat", false));
    protected final Setting<ChatVisibilityTranslator> chat =
        register(new EnumSetting<>("Chat", ChatVisibilityTranslator.Full));
    protected final Setting<Boolean> spoofModel =
        register(new BooleanSetting("Spoof-Model", false));
    protected final Setting<Integer> model =
        register(new NumberSetting<>("Model", 0, 0, 64));
    protected final Setting<Boolean> spoofHand =
        register(new BooleanSetting("Spoof-Hand", false));
    protected final Setting<HandTranslator> hand =
        register(new EnumSetting<>("Hand", HandTranslator.Right));
    protected final Setting<Boolean> send =
        register(new BooleanSetting("Send", true));

    public SettingSpoof()
    {
        super("SettingSpoof", Category.Misc);
        this.listeners.add(new ListenerSettings(this));
        this.send.addObserver(e ->
        {
            e.setValue(true);
            sendPacket();
        });
    }

    public void sendPacket()
    {
        if (mc.player != null)
        {
            String lang =
                    getLanguage(mc.gameSettings.language);
            int render  =
                    getRenderDistance(mc.gameSettings.renderDistanceChunks);
            EntityPlayer.EnumChatVisibility vis =
                    getVisibility(mc.gameSettings.chatVisibility);
            boolean chatColors =
                    getChatColors(mc.gameSettings.chatColours);

            int mask = 0;

            for (EnumPlayerModelParts enumplayermodelparts
                    : mc.gameSettings.getModelParts())
            {
                mask |= enumplayermodelparts.getPartMask();
            }

            int modelParts =
                    getModelParts(mask);
            EnumHandSide handSide =
                    getHandSide(mc.gameSettings.mainHand);

            NetworkUtil.sendPacketNoEvent(
                new CPacketClientSettings(
                    lang, render, vis, chatColors, modelParts, handSide));
        }
    }

    public String getLanguage(String languageIn)
    {
        return spoofLanguage.getValue()
                ? language.getValue()
                : languageIn;
    }

    public int getRenderDistance(int renderDistIn)
    {
        return spoofRender.getValue()
                ? renderDist.getValue()
                : renderDistIn;
    }

    public EntityPlayer.EnumChatVisibility getVisibility(
            EntityPlayer.EnumChatVisibility enumChatVisibilityIn)
    {
        return spoofChat.getValue()
                ? chat.getValue().getVisibility()
                : enumChatVisibilityIn;
    }

    public boolean getChatColors(boolean chatColorsIn)
    {
        return spoofChat.getValue()
                ? chatColors.getValue()
                : chatColorsIn;
    }

    public int getModelParts(int modelPartsIn)
    {
        return spoofModel.getValue()
                ? model.getValue()
                : modelPartsIn;
    }

    public EnumHandSide getHandSide(EnumHandSide enumHandSideIn)
    {
        return spoofHand.getValue()
                ? hand.getValue().getHandSide()
                : enumHandSideIn;
    }

}
