package me.earth.earthhack.impl.modules.misc.mcf;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;

public class MCF extends Module
{
    public MCF()
    {
        super("MCF", Category.Misc);
        this.listeners.add(new MiddleClickListener(this));
        this.setData(new MCFData(this));
    }

    protected void onMiddleClick()
    {
        if (this.isEnabled()
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
