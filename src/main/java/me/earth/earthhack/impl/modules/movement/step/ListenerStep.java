package me.earth.earthhack.impl.modules.movement.step;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.movement.StepEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketPlayer;


final class ListenerStep extends ModuleListener<Step, StepEvent>
{
    public ListenerStep(Step module)
    {
        super(module, StepEvent.class);
    }

    @Override
    public void invoke(StepEvent event)
    {
        if (!Managers.NCP.passed(module.lagTime.getValue()))
        {
            module.stepping = false;
            return;
        }

        if (event.getStage() == Stage.PRE)
        {
            if (mc.player.getRidingEntity() != null)
            {
                mc.player.getRidingEntity().stepHeight =
                        module.entityStep.getValue()
                                ? 256.0f
                                : 1.0f;
            }

            module.y = event.getBB().minY;
            if (module.stepping = module.canStep())
            {
                event.setHeight(module.height.getValue());
            }
        }
        else
        {
            if (!module.vanilla.getValue())
            {
                double height = event.getBB().minY - module.y;
                if (module.stepping && height > event.getHeight())
                {
                    double[] offsets = new double[6];
                    offsets[0] = 0.42;
                    offsets[1] = height < 1.0 && height > 0.8 ? 0.753 : 0.75;
                    offsets[2] = 1.0;
                    offsets[3] = 1.16;
                    offsets[4] = 1.23;
                    offsets[5] = 1.2;
                    if (height >= 2.0)
                    {
                        offsets = new double[8];
                        offsets[0] = 0.42;
                        offsets[1] = 0.78;
                        offsets[2] = 0.63;
                        offsets[3] = 0.51;
                        offsets[4] = 0.9;
                        offsets[5] = 1.21;
                        offsets[6] = 1.45;
                        offsets[7] = 1.43;
                    }

                    for (int i = 0;
                         i < (height > 1.0 ? offsets.length : 2);
                         i++)
                    {
                        mc.player.connection.sendPacket(
                                new CPacketPlayer.Position(
                                        mc.player.posX,
                                        mc.player.posY + offsets[i],
                                        mc.player.posZ,
                                        true));
                    }

                    if (module.autoOff.getValue())
                    {
                        module.disable();
                    }
                }
            }

            if (module.gapple.getValue()
                && module.stepping
                && !module.breakTimer.passed(60)
                && InventoryUtil.isHolding(ItemPickaxe.class)
                && !InventoryUtil.isHolding(ItemAppleGold.class))
            {
                Entity closest = EntityUtil.getClosestEnemy();
                if (closest != null && closest.getDistanceSq(mc.player) < 144)
                {
                    int slot = InventoryUtil.findHotbarItem(Items.GOLDEN_APPLE);
                    if (slot != -1)
                    {
                        Locks.acquire(Locks.PLACE_SWITCH_LOCK,
                                      () -> InventoryUtil.switchTo(slot));
                    }
                }
            }
        }
    }

}
