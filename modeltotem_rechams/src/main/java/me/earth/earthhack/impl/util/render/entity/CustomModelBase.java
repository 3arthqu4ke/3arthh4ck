package me.earth.earthhack.impl.util.render.entity;

import net.minecraft.client.model.ModelBase;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomModelBase extends ModelBase
{

    private List<CustomModelRenderer> customRenderers = new ArrayList<>();

    public CustomModelBase(List<CustomModelRenderer> customRenderers)
    {
        this.customRenderers = customRenderers;
    }

}
