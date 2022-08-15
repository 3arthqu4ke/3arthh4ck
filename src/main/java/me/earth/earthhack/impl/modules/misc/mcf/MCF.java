package me.earth.earthhack.impl.modules.misc.mcf;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;

public class MCF extends Module
{
    protected Setting<Boolean> pickBlock =
        register(new BooleanSetting("PickBlock", false));

    public MCF()
    {
        super("MCF", Category.Misc);
        this.listeners.add(new PickBlockListener(this));
        this.listeners.add(new MouseListener(this));
        this.setData(new MCFData(this));
    }

    protected void onMiddleClick()
    {
        if (this.isEnabled()
                && !PingBypass.isConnected()
                && mc.objectMouseOver != null
                && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY)
        {
            Entity entity = mc.objectMouseOver.entityHit;
            if (entity instanceof EntityPlayer)
            {
                if (Managers.FRIENDS.contains((EntityPlayer) entity))
                {
                    Managers.FRIENDS.remove(entity);
                    Managers.CHAT.sendDeleteMessage(
                            TextColor.RED + entity.getName()
                                    + " unfriended.",
                            entity.getName(), ChatIDs.FRIEND);
                }
                else
                {
                    GameProfile profile =
                            ((EntityPlayer) entity).getGameProfile();
                    Managers.FRIENDS.add(
                            profile.getName(), profile.getId());

                    Managers.CHAT.sendDeleteMessage(
                            TextColor.AQUA + entity.getName()
                                    + " friended.",
                            entity.getName(), ChatIDs.FRIEND);
                }
            }
        }
    }

}
