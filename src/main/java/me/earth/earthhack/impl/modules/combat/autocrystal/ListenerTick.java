package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CRenderPacket;
import net.minecraft.util.math.BlockPos;

final class ListenerTick extends ModuleListener<AutoCrystal, TickEvent>
{
    public ListenerTick(AutoCrystal module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (event.isSafe())
        {
            module.checkExecutor();
            module.placed.values().removeIf(stamp ->
                System.currentTimeMillis() - stamp.getTimeStamp()
                        > module.removeTime.getValue());

            module.crystalRender.tick();
            if (!module.idHelper.isUpdated())
            {
                module.idHelper.update();
                module.idHelper.setUpdated(true);
            }

            module.weaknessHelper.updateWeakness();
            render();
        }
    }

    private void render()
    {
        BlockPos pos;
        if (module.render.getValue()
            && PingBypass.isConnected()
            && (pos = module.getRenderPos()) != null)
        {
            PingBypass.sendPacket(new S2CRenderPacket(
                pos, module.outLine.getValue(), module.boxColor.getValue()));
        }
    }

}
