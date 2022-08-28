package me.earth.earthhack.impl.util.minecraft;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;

// TODO: THIS IS CHINESE
public class PlayerUtil implements Globals {
    public static final Map<Integer, EntityOtherPlayerMP> FAKE_PLAYERS =
            new HashMap<>();

    public static EntityOtherPlayerMP createFakePlayerAndAddToWorld(GameProfile profile) {
        return createFakePlayerAndAddToWorld(profile, EntityOtherPlayerMP::new);
    }

    public static EntityOtherPlayerMP createFakePlayerAndAddToWorld(GameProfile profile, BiFunction<World, GameProfile, EntityOtherPlayerMP> create) {
        EntityOtherPlayerMP fakePlayer = createFakePlayer(profile, create);
        int randomID = -1000;
        while (FAKE_PLAYERS.containsKey(randomID)
                || mc.world.getEntityByID(randomID) != null) {
            randomID = ThreadLocalRandom.current().nextInt(-100000, -100);
        }

        FAKE_PLAYERS.put(randomID, fakePlayer);
        mc.world.addEntityToWorld(randomID, fakePlayer);
        return fakePlayer;
    }

    public static EntityOtherPlayerMP createFakePlayer(GameProfile profile, BiFunction<World, GameProfile, EntityOtherPlayerMP> create)
    {
        EntityOtherPlayerMP fakePlayer = create.apply(mc.world, profile);

        fakePlayer.setPrimaryHand(mc.player.getPrimaryHand());
        fakePlayer.inventory = mc.player.inventory;
        fakePlayer.inventoryContainer = mc.player.inventoryContainer;
        fakePlayer.setPositionAndRotation(mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch);
        fakePlayer.rotationYawHead = mc.player.rotationYawHead;
        fakePlayer.onGround = mc.player.onGround;
        fakePlayer.setSneaking(mc.player.isSneaking());
        fakePlayer.setHealth(mc.player.getHealth());
        fakePlayer.setAbsorptionAmount(mc.player.getAbsorptionAmount());

        for (PotionEffect effect : mc.player.getActivePotionEffects())
        {
            fakePlayer.addPotionEffect(effect);
        }

        return fakePlayer;
    }

    public static EntityPlayer copyPlayer(EntityPlayer playerIn) {
        return copyPlayer(playerIn, true);
    }

    public static EntityPlayer copyPlayer(EntityPlayer playerIn, boolean animations) {
        int count = playerIn.getItemInUseCount();
        EntityPlayer copy = new EntityPlayer(mc.world, new GameProfile(UUID.randomUUID(), playerIn.getName())) {
            @Override public boolean isSpectator() {
                return false;
            }

            @Override public boolean isCreative() {
                return false;
            }

            @Override public int getItemInUseCount() { return count; }
        };
        if (animations) {
            copy.setSneaking(playerIn.isSneaking());
            copy.swingProgress = playerIn.swingProgress;
            copy.limbSwing = playerIn.limbSwing;
            copy.limbSwingAmount = playerIn.prevLimbSwingAmount;
            copy.inventory.copyInventory(playerIn.inventory);
        }
        copy.setPrimaryHand(playerIn.getPrimaryHand());
        copy.ticksExisted = playerIn.ticksExisted;
        copy.setEntityId(playerIn.getEntityId());
        copy.copyLocationAndAnglesFrom(playerIn);
        return copy;
    }

    /**
     * Removes the given fakeplayer.
     *
     * @param fakePlayer the fakeplayer to remove.
     */
    public static void removeFakePlayer(EntityOtherPlayerMP fakePlayer) {
        mc.addScheduledTask(() -> {
            FAKE_PLAYERS.remove(fakePlayer.getEntityId());
            fakePlayer.isDead = true; // setDead might be overridden
            if (mc.world != null) {
                mc.world.removeEntity(fakePlayer);
            }
        });
    }

    public static boolean isFakePlayer(Entity entity) {
        return entity != null && FAKE_PLAYERS.containsKey(entity.getEntityId());
    }

    public static boolean isOtherFakePlayer(Entity entity) {
        return entity != null && entity.getEntityId() < 0;
    }

    public static boolean isCreative(EntityPlayer player) {
        return player != null
                && (player.isCreative()
                    || player.capabilities.isCreativeMode);
    }

    public static BlockPos getBestPlace(BlockPos pos, EntityPlayer player) {
        final EnumFacing facing = getSide(player, pos);
        if (facing == EnumFacing.UP) {
            final Block block = mc.world.getBlockState(pos).getBlock();
            final Block block2 = mc.world.getBlockState(pos.offset(EnumFacing.UP)).getBlock();
            if (block2 instanceof BlockAir && (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK)) {
                return pos;
            }
        } else {
            BlockPos blockPos = pos.offset(facing);
            final Block block = mc.world.getBlockState(blockPos).getBlock();
            final BlockPos blockPos2 = blockPos.down();
            final Block block2 = mc.world.getBlockState(blockPos2).getBlock();
            if (block instanceof BlockAir && (block2 == Blocks.OBSIDIAN || block2 == Blocks.BEDROCK)) {
                return blockPos2;
            }
        }
        return null;
    }

    public static EnumFacing getSide(EntityPlayer player, BlockPos blockPos) {
        BlockPos playerPos = PositionUtil.getPosition(player);
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if (playerPos.offset(facing).equals(blockPos)) {
                return facing;
            }
        }
        if (playerPos.offset(EnumFacing.UP).offset(EnumFacing.UP).equals(blockPos)) {
            return EnumFacing.UP;
        }
        return EnumFacing.DOWN;
    }


    public static boolean isInHole(EntityPlayer player) {
        BlockPos position = PositionUtil.getPosition(player);
        int count = 0;
        for (EnumFacing face : EnumFacing.values()) {
            if (face == EnumFacing.UP || face == EnumFacing.DOWN) continue;
            if (!BlockUtil.isReplaceable(position.offset(face))) count++;
        }
        return count >= 3;
    }

    public static EnumFacing getOppositePlayerFaceBetter(EntityPlayer player, BlockPos pos) {
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            BlockPos off = pos.offset(face);
            BlockPos off1 = pos.offset(face).offset(face);
            BlockPos playerOff = PositionUtil.getPosition(player);
            if (new Vec3d(off).equals(new Vec3d(playerOff))
                    || new Vec3d(off1).equals(new Vec3d(off1))) return face.getOpposite();
        }
        return null;
    }

}
