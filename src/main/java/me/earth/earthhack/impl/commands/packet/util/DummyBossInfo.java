package me.earth.earthhack.impl.commands.packet.util;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfo;

import java.util.UUID;

public class DummyBossInfo extends BossInfo implements Dummy
{
    public DummyBossInfo()
    {
        super(UUID.randomUUID(),
                new TextComponentString("Dummy-Boss"),
                BossInfo.Color.RED,
                Overlay.NOTCHED_20);
    }

}
