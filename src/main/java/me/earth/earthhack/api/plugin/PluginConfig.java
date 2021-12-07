package me.earth.earthhack.api.plugin;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public final class PluginConfig
{
    @SerializedName("name")
    private String name;

    @SerializedName("mainClass")
    private String mainClass;

    @SerializedName("mixinConfig")
    private String mixinConfig;

    public String getName()
    {
        return name;
    }

    public String getMainClass()
    {
        return mainClass;
    }

    public String getMixinConfig()
    {
        return mixinConfig;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        else if (o instanceof PluginConfig)
        {
            return this.name != null
                    && this.name.equals(((PluginConfig) o).name);
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return this.name.hashCode();
    }

}
