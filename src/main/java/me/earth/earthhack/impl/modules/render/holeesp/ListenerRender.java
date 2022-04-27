package me.earth.earthhack.impl.modules.render.holeesp;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

final class ListenerRender extends ModuleListener<HoleESP, Render3DEvent>
{
    public ListenerRender(HoleESP module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        if (module.mode.getValue() == HoleESP.CalcMode.Invalidation)
        {
            module.renderListNew(module.invalidationHoleManager.get1x1Unsafe(),
                    module.unsafeColor.getValue(),
                    module.unsafeHeight.getValue(),
                    module.holes.getValue());

            module.renderListNew(module.invalidationHoleManager.get1x1(),
                    module.safeColor.getValue(),
                    module.height.getValue(),
                    module.safeHole.getValue());

            module.renderListNew(module.invalidationHoleManager.get2x1(),
                    module.wideColor.getValue(),
                    module.wideHeight.getValue(),
                    module.wide.getValue());

            module.renderListNew(module.invalidationHoleManager.get2x2(),
                    module.bigColor.getValue(),
                    module.bigHeight.getValue(),
                    module.big.getValue());
        }
        else
        {
            module.renderListOld(Managers.HOLES.getUnsafe(),
                    module.unsafeColor.getValue(),
                    module.unsafeHeight.getValue(),
                    module.holes.getValue());

            module.renderListOld(Managers.HOLES.getSafe(),
                    module.safeColor.getValue(),
                    module.height.getValue(),
                    module.safeHole.getValue());

            module.renderListOld(Managers.HOLES.getLongHoles(),
                    module.wideColor.getValue(),
                    module.wideHeight.getValue(),
                    module.wide.getValue());

            BlockPos playerPos = new BlockPos(mc.player);
            if (module.big.getValue() != 0 && !Managers.HOLES.getBigHoles().isEmpty())
            {
                int i = 1;
                for (BlockPos pos : Managers.HOLES.getBigHoles())
                {
                    if (i > module.big.getValue())
                    {
                        return;
                    }

                    if (module.checkPos(pos, playerPos))
                    {
                        Color bC = module.bigColor.getValue();
                        float bH = module.bigHeight.getValue();

                        if (module.fade.getValue())
                        {
                            double distance = mc.player.getDistanceSq(
                                    pos.getX() + 1, pos.getY(), pos.getZ() + 1);
                            double alpha = (MathUtil.square(module.fadeRange.getValue())
                                    + MathUtil.square(module.minFade.getValue())
                                    - distance)
                                    / MathUtil.square(module.fadeRange.getValue());

                            if (alpha > 0 && alpha < 1)
                            {
                                int alphaInt = MathUtil.clamp((int) (alpha * 255), 0, 255);
                                Color bC1 = new Color(bC.getRed(),
                                        bC.getGreen(),
                                        bC.getBlue(),
                                        alphaInt);

                                int boxInt = (int) (alphaInt * module.alphaFactor.getValue());
                                RenderUtil.renderBox(pos, bC1, bH, boxInt);
                                module.mPos.setPos(pos.getX(), pos.getY(), pos.getZ() + 1);
                                RenderUtil.renderBox(module.mPos, bC1, bH, boxInt);
                                module.mPos.setPos(pos.getX() + 1, pos.getY(), pos.getZ());
                                RenderUtil.renderBox(module.mPos, bC1, bH, boxInt);
                                module.mPos.setPos(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
                                RenderUtil.renderBox(module.mPos, bC1, bH, boxInt);
                            }
                            else if (alpha < 0)
                            {
                                continue;
                            }
                        }

                        RenderUtil.renderBox(pos, bC, bH);
                        module.mPos.setPos(pos.getX(), pos.getY(), pos.getZ() + 1);
                        RenderUtil.renderBox(module.mPos, bC, bH);
                        module.mPos.setPos(pos.getX() + 1, pos.getY(), pos.getZ());
                        RenderUtil.renderBox(module.mPos, bC, bH);
                        module.mPos.setPos(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
                        RenderUtil.renderBox(module.mPos, bC, bH);
                        i++;
                    }
                }
            }
        }
    }

}
