package me.earth.earthhack.impl.core.ducks.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChunkData;

import java.util.List;

public interface ISPacketChunkData {
    SPacketChunkData copy();

    int getChunkX();

    void setChunkX(int chunkX);

    int getChunkZ();

    void setChunkZ(int chunkZ);

    int getAvailableSections();

    void setAvailableSections(int availableSections);

    byte[] getBuffer();

    void setBuffer(byte[] buffer);

    List<NBTTagCompound> getTileEntityTags();

    void setTileEntityTags(List<NBTTagCompound> tileEntityTags);

    boolean isFullChunk();

    void setFullChunk(boolean fullChunk);

}
