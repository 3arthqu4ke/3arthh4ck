package me.earth.earthhack.impl.modules.misc.mobowner;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobOwner extends Module
{
    protected final Map<UUID, String> cache = new HashMap<>();

    public MobOwner()
    {
        super("MobOwner", Category.Misc);
        this.listeners.add(new ListenerTick(this));
        this.setData(new SimpleData(this,
                "Displays the name of their owners above mobs."));
    }

    @Override
    protected void onDisable()
    {
        if (mc.world != null)
        {
            for (Entity entity : mc.world.loadedEntityList)
            {
                if (entity instanceof EntityTameable
                        || entity instanceof AbstractHorse)
                {
                    try
                    {
                        entity.setAlwaysRenderNameTag(false);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
