package me.earth.earthhack.impl.modules.player.mcp;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Event;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.event.events.keyboard.ClickMiddleEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.mcf.MCF;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.math.RayTraceResult;

public class MiddleClickPearl extends Module
{
    private static final ModuleCache<MCF> MCFRIENDS =
            Caches.getModule(MCF.class);

    protected final Setting<Boolean> preferMCF   =
            register(new BooleanSetting("PrioMCF", false));
    protected final Setting<Boolean> cancelMCF   =
            register(new BooleanSetting("CancelMCF", true));
    protected final Setting<Boolean> cancelBlock =
            register(new BooleanSetting("CancelBlock", false));
    protected final Setting<Boolean> pickBlock =
            register(new BooleanSetting("PickBlock", false));

    protected Runnable runnable;

    public MiddleClickPearl()
    {
        super("MCP", Category.Player);
        this.listeners.add(new ListenerPickBlock(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerMiddleClick(this));
        SimpleData data = new SimpleData(
            this, "Middle click to throw an Ender Pearl.");
        this.setData(data);
    }

    @Override
    public void onEnable()
    {
        runnable = null;
    }

    @Override
    protected void onDisable()
    {
        runnable = null;
    }

    protected boolean prioritizeMCF()
    {
        return preferMCF.getValue() && MCFRIENDS.isEnabled()
                && mc.objectMouseOver != null
                && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY
                && mc.objectMouseOver.entityHit instanceof EntityPlayer;
    }

    public void onClick(Event event)
    {
        if (mc.player == null || mc.world == null)
        {
            return;
        }

        if (InventoryUtil.findHotbarItem(Items.ENDER_PEARL) == -1)
        {
            return;
        }

        if (!this.prioritizeMCF())
        {
            if (this.cancelBlock.getValue())
            {
                event.setCancelled(true);
            }
        }
        else
        {
            if (this.cancelMCF.getValue())
            {
                if (event instanceof ClickMiddleEvent)
                {
                    ((ClickMiddleEvent) event).setModuleCancelled(true);
                }
                else
                {
                    event.setCancelled(true);
                }
            }
            else
            {
                return;
            }
        }

        this.runnable = () ->
        {
            int slot = InventoryUtil.findHotbarItem(Items.ENDER_PEARL);
            if (slot == -1)
            {
                return;
            }

            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
            {
                int lastSlot = mc.player.inventory.currentItem;
                InventoryUtil.switchTo(slot);

                mc.playerController.processRightClick(
                    mc.player, mc.world, InventoryUtil.getHand(slot));

                InventoryUtil.switchTo(lastSlot);
            });
        };

        if (Managers.ROTATION.getServerPitch() == mc.player.rotationPitch
            && Managers.ROTATION.getServerYaw() == mc.player.rotationYaw)
        {
            this.runnable.run();
            this.runnable = null;
        }
    }

}
