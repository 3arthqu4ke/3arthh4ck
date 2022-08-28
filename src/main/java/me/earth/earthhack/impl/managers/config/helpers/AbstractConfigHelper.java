package me.earth.earthhack.impl.managers.config.helpers;

import com.google.gson.JsonObject;
import me.earth.earthhack.api.config.Config;
import me.earth.earthhack.api.config.ConfigHelper;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.config.util.ConfigDeleteException;
import me.earth.earthhack.impl.managers.config.util.JsonPathWriter;
import me.earth.earthhack.impl.util.misc.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

// TODO: this config system is the worst thing I've ever seen
public abstract class AbstractConfigHelper<C extends Config>
        implements ConfigHelper<C>
{
    private static final Pattern ILLEGAL_FILENAME = Pattern.compile("^(CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])$|[\\\\|/<>*:\"?\\x00-\\x1F]", Pattern.CASE_INSENSITIVE);
    protected final Map<String, C> configs = new ConcurrentHashMap<>();
    protected final String name;
    protected final String path;

    public AbstractConfigHelper(String name,
                                String path)
    {
        this.name = name.trim();
        this.path = "earthhack" + File.separator + path;
    }

    protected abstract C create(String name);

    protected abstract JsonObject toJson(C config);

    protected abstract C readFile(InputStream stream, String name)
            throws IOException;

    @Override
    public void save() throws IOException
    {
        ensureDir(path);
        if (this.registerDefaultIfNotPresent())
        {
            C config = create("default");
            configs.put("default", config);
        }

        String current = CurrentConfig.getInstance().get(this);
        if (current != null
            && !configs.containsKey((current = current.toLowerCase())))
        {
            C config = create(current);
            configs.put(current, config);
        }

        try
        {
            for (String s : configs.keySet())
            {
                if (s.equalsIgnoreCase(current))
                {
                    ensureDir(path);
                    C config = create(s);
                    configs.put(s, config);
                    JsonObject object = toJson(config);
                    JsonPathWriter.write(Paths.get(path + File.separator + s + ".json"),
                                         object);
                    continue;
                }

                save(s);
            }
        }
        finally
        {
            CurrentConfig.getInstance().set(this, current);
        }
    }

    @Override
    public void refresh() throws IOException
    {
        ensureDir(path);
        Map<String, C> configMap = new HashMap<>();
        Files.walk(Paths.get(path)).forEach(p ->
        {
            if (p.getFileName().toString().endsWith(".json"))
            {
                try
                {
                    Earthhack.getLogger().info(this.getName()
                                                + " config found : "
                                                + p);
                    C config = read(p);
                    configMap.put(config.getName().toLowerCase(), config);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        configs.clear();
        configs.putAll(configMap);

        /*
        (See ConfigManager refresh)

        C currentConfig = configs.get(CurrentConfig.getInstance().get(this));
        if (currentConfig != null)
        {
            currentConfig.apply();
        }

        */
    }

    @Override
    public void save(String name) throws IOException
    {
        name = name.toLowerCase();
        if (ILLEGAL_FILENAME.matcher(name).find()) {
            throw new IOException("Illegal filename " + name);
        }
        ensureDir(path);

        C config = configs.get(name);
        if (config == null
                || name.equalsIgnoreCase(CurrentConfig.getInstance().get(this)))
        {
            config = create(name);
            configs.put(name, config);
        }

        JsonObject object = toJson(config);
        JsonPathWriter.write(Paths.get(path + File.separator + name + ".json"), object);
        CurrentConfig.getInstance().set(this, name);
    }

    @Override
    public void load(String name)
    {
        name = name.toLowerCase();
        C c = configs.get(name);
        if (c != null)
        {
            c.apply();
            CurrentConfig.getInstance().set(this, name);
        }
    }

    @Override
    public void refresh(String name) throws IOException
    {
        ensureDir(path);
        name = name.toLowerCase();
        Path path = Paths.get(name);
        C config = read(path);
        configs.put(name, config);
    }

    @Override
    public void delete(String name) throws ConfigDeleteException, IOException
    {
        name = name.toLowerCase();
        if ("default".equalsIgnoreCase(name))
        {
            throw new ConfigDeleteException("Can't delete the Default config!");
        }

        if (name.equalsIgnoreCase(CurrentConfig.getInstance().get(this)))
        {
            throw new ConfigDeleteException("This config is currently active." +
                    " Please switch to another config before deleting this.");
        }

        configs.remove(name);
        Path deletePath = Paths.get(this.path + "/" + name + ".json");
        Files.delete(deletePath);
    }

    @Override
    public Collection<C> getConfigs()
    {
        return configs.values();
    }

    @Override
    public String getName()
    {
        return name;
    }

    protected C read(Path path) throws IOException
    {
        String name = path.getFileName().toString();
        try (InputStream stream = Files.newInputStream(path))
        {
            // JsonSyntaxException might be thrown here
                             // remove .json;
            return readFile(stream, name.substring(0, name.length() - 5));
        }
    }

    protected boolean registerDefaultIfNotPresent()
    {
        String current = CurrentConfig.getInstance().get(this);
        if (current == null || current.equals("default"))
        {
            CurrentConfig.getInstance().set(this, "default");
            return true;
        }

        return false;
    }

    protected void ensureDir(String path)
    {
        FileUtil.createDirectory(Paths.get(path));
    }

}
