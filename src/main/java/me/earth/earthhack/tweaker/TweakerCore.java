package me.earth.earthhack.tweaker;

public interface TweakerCore
{
    void init(ClassLoader pluginClassLoader);

    String[] getTransformers();

}
