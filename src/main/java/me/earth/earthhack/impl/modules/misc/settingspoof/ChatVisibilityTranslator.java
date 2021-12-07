package me.earth.earthhack.impl.modules.misc.settingspoof;

import net.minecraft.entity.player.EntityPlayer;

public enum ChatVisibilityTranslator
{
    Full(EntityPlayer.EnumChatVisibility.FULL),
    System(EntityPlayer.EnumChatVisibility.SYSTEM),
    Hidden(EntityPlayer.EnumChatVisibility.HIDDEN);

    private final EntityPlayer.EnumChatVisibility visibility;

    ChatVisibilityTranslator(EntityPlayer.EnumChatVisibility visibility)
    {
        this.visibility = visibility;
    }

    public EntityPlayer.EnumChatVisibility getVisibility()
    {
        return visibility;
    }
}
