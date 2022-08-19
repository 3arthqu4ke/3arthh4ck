package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.misc.CollisionEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.init.Blocks;

final class ListenerCollision extends ModuleListener<Phase, CollisionEvent>
{
    public ListenerCollision(Phase module)
    {
        super(module, CollisionEvent.class);
    }

    @Override
    public void invoke(CollisionEvent event)
    {
        if (mc.player == null
            || mc.player.movementInput == null
            || !mc.player.equals(event.getEntity()))
        {
            return;
        }

        switch (module.mode.getValue())
        {
            case Constantiam:
                if (event.getBB() != null
                        && event.getBB().maxY > mc.player.getEntityBoundingBox().minY
                        && mc.world.getBlockState(PositionUtil.getPosition().up()).getBlock() != Blocks.AIR) // shit collision check, otherwise causes stack overflow
                {
                    // PacketUtil.sendAction(CPacketEntityAction.Action.START_SNEAKING);
                    event.setBB(null);
                    // PacketUtil.sendAction(CPacketEntityAction.Action.STOP_SNEAKING);
                }
                break;
            case ConstantiamNew:
                if (module.isPhasing()) {
                    event.setBB(null);
                }
                break;
            case Normal:
                if (module.onlyBlock.getValue()
                        && !module.isPhasing()
                    || module.autoClick.getValue()
                        && module.requireClick.getValue()
                        && module.clickBB.getValue()
                        && !module.clickTimer.passed(
                                module.clickDelay.getValue())
                    || module.forwardBB.getValue()
                        && !mc.gameSettings.keyBindForward.isKeyDown())
                {
                    return;
                }

                if (event.getBB() != null
                        && event.getBB().maxY >
                                mc.player.getEntityBoundingBox().minY
                        && mc.player.isSneaking())
                {
                    event.setBB(null);
                }

                break;
            case Sand:
                event.setBB(null);
                mc.player.noClip = true;
                break;
            case Climb:
                if (mc.player.collidedHorizontally)
                {
                    event.setBB(null);
                }

                if (mc.player.movementInput.sneak
                        || (mc.player.movementInput.jump
                            && event.getPos().getY() > mc.player.posY))
                {
                    event.setCancelled(true);
                }

                break;
            default:
        }
    }

}
