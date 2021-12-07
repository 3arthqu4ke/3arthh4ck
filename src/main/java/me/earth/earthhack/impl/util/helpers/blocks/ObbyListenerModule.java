package me.earth.earthhack.impl.util.helpers.blocks;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.helpers.blocks.data.ObbyListenerData;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class ObbyListenerModule<T extends ObbyListener<?>>
        extends ObbyModule
{
    public final Setting<Integer> confirm =
            register(new NumberSetting<>("Confirm", 100, 0, 1000));

    protected final T listener;

    protected ObbyListenerModule(String name, Category category)
    {
        super(name, category);
        listener = createListener();
        this.listeners.add(listener);
        this.setData(new ObbyListenerData<>(this));
    }

    @Override
    protected void onEnable()
    {
        super.onEnable();
        listener.onModuleToggle();
        this.checkNull();
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        listener.onModuleToggle();
        this.checkNull();
    }

    @Override
    public void placeBlock(BlockPos on,
                           EnumFacing facing,
                           float[] helpingRotations,
                           Vec3d hitVec)
    {
        super.placeBlock(on, facing, helpingRotations, hitVec);
        listener.addCallback(on.offset(facing));
    }

    protected abstract T createListener();

}
