package me.earth.earthhack.tweaker.launch;

public interface ArgumentManager
{
    void loadArguments();

    void addArgument(String name, Argument<?> argument);

    <T> Argument<T> getArgument(String name);

}
