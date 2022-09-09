package me.earth.earthhack.impl.core.mixins.entity.living.player;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.ducks.network.ICPacketPlayerDigging;
import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.event.events.misc.*;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.tpssync.TpsSync;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP implements IPlayerControllerMP
{
    private static final ModuleCache<TpsSync> TPS_SYNC =
        Caches.getModule(TpsSync.class);
    private static final SettingCache<Boolean, BooleanSetting, TpsSync> MINE =
        Caches.getSetting(TpsSync.class, BooleanSetting.class, "Mine", false);

    @Shadow
    private float curBlockDamageMP;

    @Shadow
    private int blockHitDelay;

    @Shadow
    protected abstract void syncCurrentPlayItem();

    @Override
    @Invoker(value = "syncCurrentPlayItem")
    public abstract void syncItem();

    @Override
    @Accessor(value = "currentPlayerItem")
    public abstract int getItem();

    @Override
    @Accessor(value = "blockHitDelay")
    public abstract void setBlockHitDelay(int delay);

    @Override
    @Accessor(value = "blockHitDelay")
    public abstract int getBlockHitDelay();

    @Override
    @Accessor(value = "curBlockDamageMP")
    public abstract float getCurBlockDamageMP();

    @Override
    @Accessor(value = "curBlockDamageMP")
    public abstract void setCurBlockDamageMP(float damage);

    @Override
    @Accessor(value = "isHittingBlock")
    public abstract boolean getIsHittingBlock();

    @Override
    @Accessor(value = "isHittingBlock")
    public abstract void setIsHittingBlock(boolean hitting);

    @Override
    @Accessor(value = "connection")
    public abstract NetHandlerPlayClient getConnection();

    @Inject(
        method = "clickBlock",
        at = @At(value = "HEAD"),
        cancellable = true)
    public void clickBlockHook(BlockPos pos,
                                EnumFacing facing,
                                CallbackInfoReturnable<Boolean> cir)
    {
        ClickBlockEvent event = new ClickBlockEvent(pos, facing);
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
        {
            cir.setReturnValue(true);
        }
    }

    @Inject(
        method = "resetBlockRemoving",
        at = @At("HEAD"),
        cancellable = true)
    public void resetBlockRemovingHook(CallbackInfo info)
    {
        ResetBlockEvent event = new ResetBlockEvent();
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
        {
            info.cancel();
        }
    }

    @Inject(method = "processRightClick", at = @At("HEAD"), cancellable = true)
    public void processClickHook(EntityPlayer player,
                                  World worldIn,
                                  EnumHand hand,
                                  CallbackInfoReturnable<EnumActionResult> cir)
    {
        RightClickItemEvent event =
                new RightClickItemEvent(player, worldIn, hand);

        Bus.EVENT_BUS.post(event);
        if (event.isCancelled())
        {
            cir.setReturnValue(EnumActionResult.PASS);
        }
    }

    @Inject(
            method = "processRightClickBlock",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void clickBlockHook(EntityPlayerSP player,
                                WorldClient worldIn,
                                BlockPos pos,
                                EnumFacing direction,
                                Vec3d vec,
                                EnumHand hand,
                                CallbackInfoReturnable<EnumActionResult> cir)
    {
        ClickBlockEvent.Right event = new ClickBlockEvent
                                            .Right(pos, direction, vec, hand);
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
        {
            cir.setReturnValue(EnumActionResult.PASS);
        }
    }

    @Inject(
        method = "onPlayerDamageBlock",
        at = @At("HEAD"),
        cancellable = true)
    public void onPlayerDamageBlock(BlockPos pos,
                                     EnumFacing facing,
                                     CallbackInfoReturnable<Boolean> cir)
    {
        DamageBlockEvent event = new DamageBlockEvent(pos,
                                                      facing,
                                                      this.curBlockDamageMP,
                                                      this.blockHitDelay);
        Bus.EVENT_BUS.post(event);

        this.curBlockDamageMP = event.getDamage();
        this.blockHitDelay = event.getDelay();

        if (event.isCancelled())
        {
            cir.setReturnValue(true);
        }
    }

    @Redirect(
        method = "onPlayerDamageBlock",
        at = @At(
            value = "NEW",
            target = "net/minecraft/network/play/client/CPacketPlayerDigging"))
    private CPacketPlayerDigging cPacketPlayerDiggingInitHook(
            CPacketPlayerDigging.Action actionIn,
            BlockPos posIn,
            EnumFacing facingIn)
    {
        CPacketPlayerDigging packet =
                new CPacketPlayerDigging(actionIn, posIn, facingIn);

        switch (actionIn)
        {
            case START_DESTROY_BLOCK:
            case STOP_DESTROY_BLOCK:
            case ABORT_DESTROY_BLOCK:
                ((ICPacketPlayerDigging) packet).setNormalDigging(true);
                break;
            default:
                break;
        }

        if (actionIn == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)
        {
            //noinspection ConstantConditions
            ((ICPacketPlayerDigging) packet).setClientSideBreaking(true);
        }

        return packet;
    }

    /**
     * target = {@link IBlockState#getPlayerRelativeBlockHardness(
     * EntityPlayer, World, BlockPos)}
     */
    @Redirect(
            method = "onPlayerDamageBlock",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/block/state/IBlockState;" +
                        "getPlayerRelativeBlockHardness(" +
                        "Lnet/minecraft/entity/player/EntityPlayer;" +
                        "Lnet/minecraft/world/World;" +
                        "Lnet/minecraft/util/math/BlockPos;)F"))
    public float getPlayerRelativeBlockHardnessHook(IBlockState state,
                                                    EntityPlayer player,
                                                    World worldIn,
                                                    BlockPos pos)
    {
        return state.getPlayerRelativeBlockHardness(player, worldIn, pos)
                * (TPS_SYNC.isEnabled() && MINE.getValue()
                        ? Managers.TPS.getFactor()
                        : 1);
    }

    @Redirect(
        method = "updateController",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;syncCurrentPlayItem()V"))
    public void syncCurrentPlayItemHook(PlayerControllerMP playerControllerMP)
    {
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, this::syncCurrentPlayItem);
    }

    @Inject(method = "windowClick", at = @At("HEAD"), cancellable = true)
    public void windowClickHook(int windowId, int slotId, int mouseButton,
                                 ClickType type, EntityPlayer player,
                                 CallbackInfoReturnable<ItemStack> cir)
    {
        WindowClickEvent event = new WindowClickEvent(windowId, slotId, mouseButton, type, player);
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled())
        {
            cir.setReturnValue(event.getStack());
        }
    }

    @Inject(
        method = "windowClick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/inventory/Container;slotClick(IILnet/minecraft/inventory/ClickType;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;",
            shift = At.Shift.BEFORE),
        locals = LocalCapture.CAPTURE_FAILHARD)
    private void postGetTransactionId(int windowId, int slotId, int mouseButton, ClickType type, EntityPlayer player, CallbackInfoReturnable<ItemStack> cir, short short1)
    {
        Bus.EVENT_BUS.post(new PreSlotClickEvent(windowId, slotId, mouseButton, type, player, short1));
    }

    // PingBypass CPacketPlayerDigging Redirects:
    private CPacketPlayerDigging initDigging(CPacketPlayerDigging.Action actionIn, BlockPos posIn, EnumFacing facingIn) {
        CPacketPlayerDigging digging = new CPacketPlayerDigging(actionIn, posIn, facingIn);
        ((ICPacketPlayerDigging) digging).setNormalDigging(true);
        return digging;
    }

    // documentation and recaf attach say otherwise
    @SuppressWarnings({"InvalidMemberReference", "MixinAnnotationTarget", "InvalidInjectorMethodSignature", "UnresolvedMixinReference"})
    @Redirect(
        method = "resetBlockRemoving",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/network/play/client/CPacketPlayerDigging$Action;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/network/play/client/CPacketPlayerDigging;"))
    private CPacketPlayerDigging resetBlockRemovingInitPacketHook(CPacketPlayerDigging.Action actionIn, BlockPos posIn, EnumFacing facingIn) {
        return initDigging(actionIn, posIn, facingIn);
    }

    // documentation and recaf attach say otherwise
    @SuppressWarnings({"InvalidMemberReference", "MixinAnnotationTarget", "InvalidInjectorMethodSignature", "UnresolvedMixinReference"})
    @Redirect(
        method = "onPlayerDamageBlock",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/network/play/client/CPacketPlayerDigging$Action;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/network/play/client/CPacketPlayerDigging;"))
    private CPacketPlayerDigging onPlayerDamageBlockInitPacketHook(CPacketPlayerDigging.Action actionIn, BlockPos posIn, EnumFacing facingIn) {
        return initDigging(actionIn, posIn, facingIn);
    }

    // documentation and recaf attach say otherwise
    @SuppressWarnings({"InvalidMemberReference", "MixinAnnotationTarget", "InvalidInjectorMethodSignature", "UnresolvedMixinReference"})
    @Redirect(
        method = "clickBlock",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/network/play/client/CPacketPlayerDigging$Action;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/network/play/client/CPacketPlayerDigging;"))
    private CPacketPlayerDigging clickBlockInitPacketHook(CPacketPlayerDigging.Action actionIn, BlockPos posIn, EnumFacing facingIn) {
        return initDigging(actionIn, posIn, facingIn);
    }

}
