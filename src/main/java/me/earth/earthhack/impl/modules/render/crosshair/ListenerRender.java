package me.earth.earthhack.impl.modules.render.crosshair;

import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.crosshair.mode.GapMode;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;

/**
 * @author Gerald
 * @since 6/17/2021
 **/

final class ListenerRender extends ModuleListener<Crosshair, Render2DEvent> {

    public ListenerRender(Crosshair module) {
        super(module, Render2DEvent.class);
    }

    @Override
    public void invoke(Render2DEvent event) {
        final int screenMiddleX = event.getResolution().getScaledWidth() / 2;
        final int screenMiddleY = event.getResolution().getScaledHeight() / 2;
        final float width = module.width.getValue();

        if (module.gapMode.getValue() == GapMode.NONE) {
            
        } else {
            // Top Box
            Render2DUtil.drawBorderedRect(screenMiddleX - width, screenMiddleY - (module.gapSize.getValue() + module.length.getValue()) - ((MovementUtil.isMoving() && module.gapMode.getValue() == GapMode.DYNAMIC) ? module.gapSize.getValue(): 0), screenMiddleX + (module.width.getValue()), screenMiddleY - (module.gapSize.getValue()) - ((MovementUtil.isMoving() && module.gapMode.getValue() == GapMode.DYNAMIC) ? module.gapSize.getValue(): 0), 0.5f, module.color.getValue().getRGB(), module.outlineColor.getValue().getRGB());
            // Bottom Box
            Render2DUtil.drawBorderedRect(screenMiddleX - width, screenMiddleY + (module.gapSize.getValue()) + ((MovementUtil.isMoving() && module.gapMode.getValue() == GapMode.DYNAMIC) ? module.gapSize.getValue() : 0), screenMiddleX + (module.width.getValue()), screenMiddleY + (module.gapSize.getValue() + module.length.getValue()) + ((MovementUtil.isMoving() && module.gapMode.getValue() == GapMode.DYNAMIC) ? module.gapSize.getValue(): 0), 0.5f, module.color.getValue().getRGB(), module.outlineColor.getValue().getRGB());
            // Left Box
            Render2DUtil.drawBorderedRect(screenMiddleX - (module.gapSize.getValue() + module.length.getValue()) - ((MovementUtil.isMoving() && module.gapMode.getValue() == GapMode.DYNAMIC) ? module.gapSize.getValue(): 0), screenMiddleY - (module.width.getValue()), screenMiddleX - (module.gapSize.getValue()) - ((MovementUtil.isMoving() && module.gapMode.getValue() == GapMode.DYNAMIC) ? module.gapSize.getValue(): 0), screenMiddleY + (module.width.getValue()), 0.5f, module.color.getValue().getRGB(), module.outlineColor.getValue().getRGB());
            // Right Box
            Render2DUtil.drawBorderedRect(screenMiddleX + (module.gapSize.getValue()) + ((MovementUtil.isMoving() && module.gapMode.getValue() == GapMode.DYNAMIC) ? module.gapSize.getValue(): 0), screenMiddleY - (module.width.getValue()), screenMiddleX + (module.gapSize.getValue() + module.length.getValue()) + ((MovementUtil.isMoving() && module.gapMode.getValue() == GapMode.DYNAMIC) ? module.gapSize.getValue(): 0), screenMiddleY + (module.width.getValue()), 0.5f, module.color.getValue().getRGB(), module.outlineColor.getValue().getRGB());
        }
        if (module.indicator.getValue()) {
            float f = this.mc.player.getCooledAttackStrength(0.0F);
            float indWidthInc = ((screenMiddleX + (module.gapSize.getValue() + module.length.getValue()) + ((MovementUtil.isMoving() && module.gapMode.getValue() == GapMode.DYNAMIC) ? module.gapSize.getValue(): 0)) - (screenMiddleX - (module.gapSize.getValue() + module.length.getValue()) - ((MovementUtil.isMoving() && module.gapMode.getValue() == GapMode.DYNAMIC) ? module.gapSize.getValue(): 0))) / 17.f;
            if (f < 1.0f) {
                final float finWidth = (indWidthInc * (f * 17.f));
                Render2DUtil.drawBorderedRect(screenMiddleX - (module.gapSize.getValue() + module.length.getValue()) - ((MovementUtil.isMoving() && module.gapMode.getValue() == GapMode.DYNAMIC) ? module.gapSize.getValue(): 0),
                        (screenMiddleY + (module.gapSize.getValue() + module.length.getValue()) + ((MovementUtil.isMoving() && module.gapMode.getValue() == GapMode.DYNAMIC) ? module.gapSize.getValue(): 0)) + 2,
                        screenMiddleX - (module.gapSize.getValue() + module.length.getValue()) - ((MovementUtil.isMoving() && module.gapMode.getValue() == GapMode.DYNAMIC) ? module.gapSize.getValue(): 0) + finWidth,
                        (screenMiddleY + (module.gapSize.getValue() + module.length.getValue()) + ((MovementUtil.isMoving() && module.gapMode.getValue() == GapMode.DYNAMIC) ? module.gapSize.getValue(): 0)) + 2 + (module.width.getValue() * 2),
                        0.5f, module.color.getValue().getRGB(), module.outlineColor.getValue().getRGB());

            }
        }
    }
}
