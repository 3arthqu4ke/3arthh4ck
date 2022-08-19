package me.earth.earthhack.impl.core.mixins.network.server;

import me.earth.earthhack.impl.core.ducks.network.ISPacketChunkData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChunkData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(SPacketChunkData.class)
public abstract class MixinSPacketChunkData implements ISPacketChunkData {
    @Override
    public SPacketChunkData copy() {
        SPacketChunkData copy = new SPacketChunkData();
        ISPacketChunkData access = (ISPacketChunkData) copy;
        access.setChunkX(this.getChunkX());
        access.setChunkZ(this.getChunkZ());
        access.setAvailableSections(this.getAvailableSections());
        byte[] buffer = this.getBuffer();
        byte[] bufferCopy = Arrays.copyOf(buffer, buffer.length);
        access.setBuffer(bufferCopy);
        List<NBTTagCompound> tags = this.getTileEntityTags()
                                        .stream()
                                        .map(NBTTagCompound::copy)
                                        .collect(Collectors.toList());
        access.setTileEntityTags(tags);
        access.setFullChunk(this.isFullChunk());
        return copy;
    }

    @Override
    @Accessor("chunkX")
    public abstract int getChunkX();

    @Override
    @Accessor("chunkX")
    public abstract void setChunkX(int chunkX);

    @Override
    @Accessor("chunkZ")
    public abstract int getChunkZ();

    @Override
    @Accessor("chunkZ")
    public abstract void setChunkZ(int chunkZ);

    @Override
    @Accessor("availableSections")
    public abstract int getAvailableSections();

    @Override
    @Accessor("availableSections")
    public abstract void setAvailableSections(int availableSections);

    @Override
    @Accessor("buffer")
    public abstract byte[] getBuffer();

    @Override
    @Accessor("buffer")
    public abstract void setBuffer(byte[] buffer);

    @Override
    @Accessor("tileEntityTags")
    public abstract List<NBTTagCompound> getTileEntityTags();

    @Override
    @Accessor("tileEntityTags")
    public abstract void setTileEntityTags(List<NBTTagCompound> tileEntityTags);

    @Override
    @Accessor("fullChunk")
    public abstract boolean isFullChunk();

    @Override
    @Accessor("fullChunk")
    public abstract void setFullChunk(boolean fullChunk);

}
