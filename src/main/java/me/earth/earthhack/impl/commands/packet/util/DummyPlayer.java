package me.earth.earthhack.impl.commands.packet.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.UUID;

public class DummyPlayer extends EntityPlayer implements Dummy
{
    public DummyPlayer(World worldIn)
    {
        super(worldIn, new GameProfile(UUID.randomUUID(), "Dummy-Player"));
    }

    @Override
    public boolean isSpectator()
    {
        return false;
    }

    @Override
    public boolean isCreative()
    {
        return false;
    }

}
