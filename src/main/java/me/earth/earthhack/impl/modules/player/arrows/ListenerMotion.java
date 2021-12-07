package me.earth.earthhack.impl.modules.player.arrows;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSpectralArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

final class ListenerMotion extends ModuleListener<Arrows, MotionUpdateEvent>
{
    private PotionType lastType;
    private long lastDown;

    public ListenerMotion(Arrows module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        ItemStack arrow;
        EnumHand hand = InventoryUtil.getHand(Items.BOW);
        if (!module.shoot.getValue()
            || mc.player.isCreative()
            || mc.currentScreen != null
            || hand == null
            || (arrow = module.findArrow()).isEmpty()
            || blocked())
        {
            return;
        }

        boolean cycle = module.cycle.getValue();
        if (module.badStack(arrow) || module.fast && cycle)
        {
            if (!cycle)
            {
                return;
            }

            module.cycle(false, true);
            module.fast = false;
            arrow = module.findArrow();
            if (module.badStack(arrow))
            {
                return;
            }
        }

        if (event.getStage() == Stage.PRE)
        {
            if (mc.gameSettings.keyBindUseItem.isKeyDown())
            {
                lastDown = System.currentTimeMillis();
            }
            else if (System.currentTimeMillis() - lastDown > 100)
            {
                return;
            }

            EntityPlayer player = RotationUtil.getRotationPlayer();
            if (player.motionX != 0 || player.motionZ != 0)
            {
                //event.setPitch(-10.0f);
                Vec3d vec3d = player.getPositionVector().add(
                    player.motionX,
                    player.motionY + player.getEyeHeight(),
                    player.motionZ);
                float[] rotations = RotationUtil.getRotations(vec3d);
                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
            }
            else
            {
                event.setPitch(-90.0f);
            }
        }
        else if (module.autoRelease.getValue()
                && !mc.player.getActiveItemStack().isEmpty())
        {
            PotionType type = PotionUtils.getPotionFromItem(arrow);
            if (arrow.getItem() instanceof ItemSpectralArrow)
            {
                type = Arrows.SPECTRAL;
            }

            if (lastType == type
                && !module.timer.passed(module.shootDelay.getValue()))
            {
                return;
            }

            lastType = type;
            float ticks = mc.player.getHeldItem(hand).getMaxItemUseDuration()
                    - mc.player.getItemInUseCount()
                    - (module.tpsSync.getValue() ? 20.0f - Managers.TPS.getTps()
                                                 : 0.0f);
            if (ticks >= module.releaseTicks.getValue()
                    && ticks <= module.maxTicks.getValue())
            {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                    mc.playerController.onStoppedUsingItem(mc.player));
                module.fast = module.preCycle.getValue() && cycle;
                module.timer.reset();
            }
        }
    }

    private boolean blocked()
    {
        BlockPos pos = PositionUtil.getPosition();
        return mc.world.getBlockState(pos.up())
                       .getMaterial()
                       .blocksMovement()
                || mc.world.getBlockState(pos.up(2))
                           .getMaterial()
                           .blocksMovement();
    }

}
