package me.earth.earthhack.impl.modules.render.newchunks;

import io.netty.util.internal.ConcurrentSet;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.render.newchunks.util.ChunkData;

import java.util.Set;

public class NewChunks extends Module
{
    //TODO: THIS!
    final Setting<Boolean> unload =
            register(new BooleanSetting("Unload", false));

    final Set<ChunkData> data = new ConcurrentSet<>();

    public NewChunks()
    {
        super("NewChunks", Category.Render);
        this.listeners.add(new ListenerChunkData(this));
        this.listeners.add(new ListenerRender(this));
    }

    @Override
    protected void onDisable()
    {
        data.clear();
    }

}
