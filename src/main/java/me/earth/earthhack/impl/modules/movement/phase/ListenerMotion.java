package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

final class ListenerMotion extends ModuleListener<Phase, MotionUpdateEvent>
{
    private static final double[] off =
        {
            -0.02500000037252903,
            -0.028571428997176036,
            -0.033333333830038704,
            -0.04000000059604645,
            -0.05000000074505806,
            -0.06666666766007741,
            -0.10000000149011612,
            -0.20000000298023224,
            -0.04000000059604645,
            -0.033333333830038704,
            -0.028571428997176036,
            -0.02500000037252903
        };

    private final StopWatch timer = new StopWatch();

    public ListenerMotion(Phase module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        double xSpeed =
                mc.player.getHorizontalFacing().getDirectionVec().getX() * 0.1;
        double zSpeed =
                mc.player.getHorizontalFacing().getDirectionVec().getZ() * 0.1;

        switch (module.mode.getValue())
        {
            case Constantiam:
                if (event.getStage() == Stage.PRE
                        && mc.player.collidedHorizontally
                        && !module.isPhasing())
                {
                    event.setY(event.getY() - 0.032);
                } /*else if (event.getStage() == Stage.PRE
                        && mc.world.getBlockState(PositionUtil.getPosition()).getBlock() != Blocks.AIR)
                {
                    mc.player.noClip = true;
                }*/
                if (event.getStage() == Stage.PRE
                        && module.isPhasing()
                        && mc.world.getBlockState(PositionUtil.getPosition().up()).getBlock() == Blocks.AIR)
                {
                    event.setY(event.getY() - 0.032);
                }
            case Normal:
                if (event.getStage() == Stage.PRE)
                {
                    if (mc.player.isSneaking()
                            && module.isPhasing()
                            && (!module.requireForward.getValue()
                                || mc.gameSettings.keyBindForward.isKeyDown()))
                    {
                        if (checkAutoClick())
                        {
                            return;
                        }

                        float yaw = mc.player.rotationYaw;
                        mc.player.setEntityBoundingBox(
                            mc.player
                              .getEntityBoundingBox()
                              .offset(
                                module.distance.getValue()
                                    * Math.cos(Math.toRadians(yaw + 90.0f)),
                                0.0,
                                module.distance.getValue()
                                    * Math.sin(Math.toRadians(yaw + 90.0f))));
                    }
                }

                break;
            case Sand:
                mc.player.motionY = 0.0;
                if (mc.inGameHasFocus)
                {
                    if (mc.player.movementInput.jump)
                    {
                        mc.player.motionY += 0.3;
                    }

                    if (mc.player.movementInput.sneak)
                    {
                        mc.player.motionY -= 0.3;
                    }
                }

                mc.player.noClip = true;
                break;
            case Packet:
                if (mc.player.collidedHorizontally && module.timer.passed(200))
                {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY + 0.05,
                            mc.player.posZ,
                            true));

                    mc.player.connection.sendPacket(new CPacketPlayer.Position(
                            mc.player.posX + xSpeed * module.speed.getValue(),
                            mc.player.posY,
                            mc.player.posZ + zSpeed * module.speed.getValue(),
                            true));

                    mc.player.connection.sendPacket(new CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY,
                            mc.player.posZ,
                            true));

                    module.timer.reset();
                }
                break;
            case Skip:
                if (event.getStage() == Stage.PRE
                        && mc.player.collidedHorizontally)
                {
                    if (!timer.passed(module.skipTime.getValue()))
                    {
                        if (module.cancel.getValue())
                        {
                            event.setCancelled(true);
                        }

                        return;
                    }

                    float direction = mc.player.rotationYaw;
                    if (mc.player.moveForward < 0F)
                    {
                        direction += 180F;
                    }

                    if (mc.player.moveStrafing > 0F)
                    {
                        direction -= 90F * (mc.player.moveForward < 0F
                                ? -0.5F
                                : mc.player.moveForward > 0F ? 0.5F : 1F);
                    }

                    if (mc.player.moveStrafing < 0F)
                    {
                        direction += 90F * (mc.player.moveForward < 0F
                                ? -0.5F
                                : mc.player.moveForward > 0F ? 0.5F : 1F);
                    }

                    double x = Math.cos(Math.toRadians(direction + 90)) * 0.2D;
                    double z = Math.sin(Math.toRadians(direction + 90)) * 0.2D;

                    if (module.limit.getValue())
                    {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.onGround));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + x * 0.001f, mc.player.posY + 0.1f, mc.player.posZ + z * 0.001f, mc.player.onGround));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + x * 0.03f, 0, mc.player.posZ + z * 0.03f, mc.player.onGround));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + x * 0.06f, mc.player.posY, mc.player.posZ + z * 0.06f, mc.player.onGround));
                        event.setCancelled(true);
                        timer.reset();
                        return;
                    }

                    for (int index = 0; index < off.length; index++)
                    {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + off[index], mc.player.posZ, mc.player.onGround));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + (x * index), mc.player.posY, mc.player.posZ + (z * index), mc.player.onGround));
                    }

                    event.setCancelled(true);
                    timer.reset();
                }
                break;
            default:
        }
    }

    private boolean checkAutoClick()
    {
        if (!module.autoClick.getValue())
        {
            return false;
        }

        if (module.clickTimer.passed(module.clickDelay.getValue()))
        {
            RayTraceResult result = mc.objectMouseOver;
            if (module.smartClick.getValue())
            {
                EnumFacing facing = mc.player.getHorizontalFacing();
                BlockPos pos = PositionUtil.getPosition().offset(facing);
                if (!mc.player.getEntityBoundingBox()
                              .intersects(new AxisAlignedBB(pos)))
                {
                    pos = PositionUtil.getPosition();
                }

                if (mc.objectMouseOver != null
                        && pos.equals(mc.objectMouseOver.getBlockPos())
                    || pos.up().equals(mc.objectMouseOver.getBlockPos()))
                {
                    result = mc.objectMouseOver;
                }
                else
                {
                    BlockPos target = pos.up();
                    if (mc.world.getBlockState(target)
                                .getMaterial() == Material.AIR)
                    {
                        target = pos;
                    }

                    result = new RayTraceResult(
                            new Vec3d(0.0, 0.5, 0.0), // TODO: proper hitVec!
                            facing.getOpposite(),
                            target);
                }
            }
            //noinspection ConstantConditions
            if (result != null && result.getBlockPos() != null)
            {
                float[] r = RayTraceUtil.hitVecToPlaceVec(
                        result.getBlockPos(), result.hitVec);

                EnumHand hand =
                    mc.player.getHeldItemOffhand()
                             .getItem() instanceof ItemFood
                        || mc.player.getHeldItemOffhand()
                                    .getItem()
                                == Items.TOTEM_OF_UNDYING
                            ? EnumHand.OFF_HAND
                            : EnumHand.MAIN_HAND;

                Packet<?> packet = new CPacketPlayerTryUseItemOnBlock(
                        result.getBlockPos(),
                        result.sideHit,
                        hand,
                        r[0],
                        r[1],
                        r[2]);

                NetworkUtil.sendPacketNoEvent(packet);
                module.pos = result.getBlockPos();
                module.clickTimer.reset();
            }
            else
            {
                return module.requireClick.getValue();
            }
        }
        else
        {
            return module.requireClick.getValue();
        }

        return false;
    }

}
