package me.earth.earthhack.impl.modules.player.fakeplayer;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.core.ducks.entity.IEntityPlayer;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.fakeplayer.util.Position;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

final class ListenerMotion extends
        ModuleListener<FakePlayer, MotionUpdateEvent>
{
    private boolean wasRecording;
    private int ticks;

    public ListenerMotion(FakePlayer module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        boolean record = module.record.getValue();
        if (!record && wasRecording)
        {
            wasRecording = false;
        }

        if (module.gapple.getValue()
                && module.timer.passed(module.gappleDelay.getValue()))
        {
            module.fakePlayer.setAbsorptionAmount(16.0f);
            module.fakePlayer.addPotionEffect(
                new PotionEffect(MobEffects.REGENERATION, 400, 1));
            module.fakePlayer.addPotionEffect(
                new PotionEffect(MobEffects.RESISTANCE, 6000, 0));
            module.fakePlayer.addPotionEffect(
                new PotionEffect(MobEffects.FIRE_RESISTANCE, 6000, 0));
            module.fakePlayer.addPotionEffect(
                new PotionEffect(MobEffects.ABSORPTION, 2400, 3));

            module.timer.reset();
        }

        if (event.getStage() == Stage.PRE && !record)
        {
            if (module.playRecording.getValue())
            {
                if (module.positions.isEmpty())
                {
                    ModuleUtil.sendMessage(module,
                            TextColor.RED
                                + "No recording was found for this world!");
                    module.playRecording.setValue(false);
                    return;
                }

                if (module.index >= module.positions.size())
                {
                    if (!module.loop.getValue())
                    {
                        module.playRecording.setValue(false);
                    }

                    module.index = 0;
                }

                if (ticks++ % 2 == 0)
                {
                    Position p = module.positions.get(module.index++);
                    module.fakePlayer.rotationYaw     = p.getYaw();
                    module.fakePlayer.rotationPitch   = p.getPitch();
                    module.fakePlayer.rotationYawHead = p.getHead();
                    module.fakePlayer.setPositionAndRotationDirect(
                        p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch(),
                        3, false);
                    module.fakePlayer.motionX = p.getMotionX();
                    module.fakePlayer.motionY = p.getMotionY();
                    module.fakePlayer.motionZ = p.getMotionZ();
                    ((IEntityPlayer) module.fakePlayer)
                        .setTicksWithoutMotionUpdate(0);
                }
            }
            else
            {
                module.index = 0;
                module.fakePlayer.motionX = 0.0;
                module.fakePlayer.motionY = 0.0;
                module.fakePlayer.motionZ = 0.0;
            }
        }
        else if (event.getStage() == Stage.POST && record)
        {
            module.playRecording.setValue(false);
            module.fakePlayer.motionX = 0.0;
            module.fakePlayer.motionY = 0.0;
            module.fakePlayer.motionZ = 0.0;

            if (!wasRecording)
            {
                ModuleUtil.sendMessage(module, "Recording...");
                module.positions.clear();
                wasRecording = true;
            }

            if (ticks++ % 2 == 0)
            {
                module.positions.add(new Position(mc.player));
            }
        }
    }

}
