package me.earth.earthhack.impl.modules.misc.noafk;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.util.math.BlockPos;

final class ListenerTick extends ModuleListener<NoAFK, TickEvent> {
    public static final BlockPos QUEUE_POS = new BlockPos(0, 240, 0);
    private boolean toggle;

    public ListenerTick(NoAFK module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (PingBypass.isConnected()
            || !event.isSafe()
            || mc.player.getPosition().equals(QUEUE_POS)
            || !module.baritone.getValue()) {
            module.baritoneTimer.setTime(0);
            module.stage = NoAFK.Stage.BACK;
            module.startPos = null;
            module.target = null;
            return;
        }

        if (module.startPos == null) {
            module.startPos = mc.player.getPosition();
        }

        if (mc.player.getPosition().equals(module.target)
            || module.baritoneTimer.passed(module.baritoneDelay.getValue() * 1000)) {
            module.baritoneTimer.reset();
            module.stage = module.stage == NoAFK.Stage.GO
                ? NoAFK.Stage.BACK
                : NoAFK.Stage.GO;

            switch (module.stage) {
                case GO:
                    toggle = !toggle;
                    // int x = module.startPos.getX() + random.nextInt(40) - 20;
                    // int y = module.startPos.getY() + random.nextInt(5) - 5;
                    // int z = module.startPos.getZ() + random.nextInt(40) - 20;
                    int x = module.startPos.getX() + (toggle ? 200 : 0);
                    int y = module.startPos.getY();
                    int z = module.startPos.getZ() + (!toggle ? 200 : 0);

                    module.target = new BlockPos(x, y, z);
                    mc.player.sendChatMessage(module.baritonePrefix.getValue()
                                            + "goto " + x + " " + y + " " + z);
                    break;
                case BACK:
                    x = module.startPos.getX();
                    y = module.startPos.getX();
                    z = module.startPos.getX();
                    mc.player.sendChatMessage(module.baritonePrefix.getValue()
                                            + "goto " + x + " " + y + " " + z);
                    break;
                default:
            }
        }
    }

}
