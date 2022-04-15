package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.UUID;

public class ShieldPlayer extends EntityPlayer {
    public ShieldPlayer(World worldIn) {
        super(worldIn, new GameProfile(UUID.randomUUID(), "Shield"));
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

}
