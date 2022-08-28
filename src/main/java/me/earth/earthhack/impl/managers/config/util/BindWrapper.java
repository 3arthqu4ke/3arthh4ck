package me.earth.earthhack.impl.managers.config.util;

import com.google.gson.annotations.SerializedName;

public class BindWrapper
{
    @SerializedName("name")
    private String name;

    @SerializedName("module")
    private String module;

    @SerializedName("value")
    private String value;

    public BindWrapper(String name, String module, String value)
    {
        this.name   = name;
        this.module = module;
        this.value  = value;
    }

    public String getName()
    {
        return name;
    }

    public String getModule()
    {
        return module;
    }

    public String getValue()
    {
        return value;
    }

}
