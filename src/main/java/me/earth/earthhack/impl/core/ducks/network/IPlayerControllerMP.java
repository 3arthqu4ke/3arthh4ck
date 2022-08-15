package me.earth.earthhack.impl.core.ducks.network;

import net.minecraft.client.network.NetHandlerPlayClient;

/**
 * Duck interface for
 * {@link net.minecraft.client.multiplayer.PlayerControllerMP}.
 */
public interface IPlayerControllerMP
{
    /**
     * Accessor for syncCurrentPlayItem.
     */
    void syncItem();

    /**
     * Accessor for currentPlayerItem.
     *
     * @return currentPlayerItem.
     */
    int getItem();

    /**
     * Accessor for blockHitDelay.
     *
     * @param delay set the delay.
     */
    void setBlockHitDelay(int delay);

    /**
     * Accessor for blockHitDelay.
     *
     * @return blockHitDelay.
     */
    int getBlockHitDelay();

    /**
     * Accessor for curBlockDamageMP.
     *
     * @return curBlockDamageMP.
     */
    float getCurBlockDamageMP();

    /**
     * Accessor for curBlockDamageMP.
     *
     * @param damage set curBlockDamageMP.
     */
    void setCurBlockDamageMP(float damage);

    /**
     * Accessor for isHittingBlock.
     *
     * @param hitting set isHittingBlock.
     */
    void setIsHittingBlock(boolean hitting);

    /**
     * Accessor for isHittingBlock.
     *
     * @return isHittingBlock.
     */
    boolean getIsHittingBlock();

    NetHandlerPlayClient getConnection();

}
