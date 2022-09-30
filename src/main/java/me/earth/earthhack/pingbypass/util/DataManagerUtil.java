package me.earth.earthhack.pingbypass.util;

import io.netty.handler.codec.EncoderException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

import java.util.List;

// TODO: this is from the old PingBypass, check that this is not bullshit, maybe it can be done better?
public class DataManagerUtil
{
    public static void writeDataEntries(PacketBuffer buffer, List<EntityDataManager.DataEntry<?>> entries)
    {
        if (entries != null)
        {
            for (EntityDataManager.DataEntry<?> dataentry : entries)
            {
                writeEntry(buffer, dataentry);
            }
        }

        buffer.writeByte(255);
    }

    private static <T> void writeEntry(PacketBuffer buf, EntityDataManager.DataEntry<T> entry)
    {
        DataParameter<T> dataparameter = entry.getKey();
        int i = DataSerializers.getSerializerId(dataparameter.getSerializer());

        if (i < 0)
        {
            throw new EncoderException("Unknown serializer type " + dataparameter.getSerializer());
        }
        else
        {
            buf.writeByte(dataparameter.getId());
            buf.writeVarInt(i);
            dataparameter.getSerializer().write(buf, entry.getValue());
        }
    }

}
