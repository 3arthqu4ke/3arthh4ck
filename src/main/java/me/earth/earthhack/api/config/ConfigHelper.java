package me.earth.earthhack.api.config;

import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.managers.config.util.ConfigDeleteException;

import java.io.IOException;
import java.util.Collection;

public interface ConfigHelper<C extends Config> extends Nameable
{
    void save() throws IOException;

    void refresh() throws IOException;

    void save(String name) throws IOException;

    void load(String name) throws IOException;

    void refresh(String name) throws IOException;

    void delete(String name) throws IOException, ConfigDeleteException;

    Collection<C> getConfigs();

}
