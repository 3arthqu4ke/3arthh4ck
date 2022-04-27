package me.earth.modeltotem;

import jassimp.AiScene;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.util.render.image.GifConverter;
import me.earth.earthhack.impl.util.render.image.GifImage;
import me.earth.earthhack.impl.util.render.image.ImageUtil;
import me.earth.earthhack.impl.util.render.image.NameableImage;
import me.earth.earthhack.impl.util.render.model.IModel;
import me.earth.earthhack.impl.util.render.model.Mesh;
import me.earth.earthhack.impl.util.render.model.ModelUtil;
import me.earth.earthhack.impl.util.render.shader.SettingShader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public enum ModelTotemFileManager {
    INSTANCE;

    private static final File EARTHHACK_ROOT = new File("earthhack/");
    private static final File IMAGES = new File("earthhack/images");
    private static final File MODELS = new File("earthhack/models");
    private static final File SHADERS = new File("earthhack/shaders");

    private final Map<String, GifImage> gifs = new ConcurrentHashMap<>();
    private final Map<String, NameableImage> textures = new ConcurrentHashMap<>();
    private final Map<String, IModel> models = new ConcurrentHashMap<>();
    private final Map<String, SettingShader> shaders = new ConcurrentHashMap<>();

    private final List<GifImage> gifList = new ArrayList<>();
    private final List<NameableImage> imageList = new ArrayList<>();
    private final List<IModel> modelList = new ArrayList<>();
    private final List<SettingShader> shaderList = new ArrayList<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void init()
    {
        if (!IMAGES.exists())
        {
            IMAGES.mkdir();
        }

        if (!MODELS.exists())
        {
            MODELS.mkdir();
        }

        if (!SHADERS.exists())
        {
            SHADERS.mkdir();
        }

        handleImageDir(IMAGES);
        for (File file : Objects.requireNonNull(IMAGES.listFiles()))
        {
            if (file.isDirectory()) handleImageDir(file);
        }

        handleModelDir(MODELS);
        for (File file : Objects.requireNonNull(MODELS.listFiles()))
        {
            if (file.isDirectory()) handleModelDir(file);
        }
    }

    private void handleImageDir(File dir)
    {
        if (dir.isDirectory())
        {
            for (File file : Objects.requireNonNull(
                dir.listFiles((dir1, name) -> name.endsWith("gif")
                    || name.endsWith("png")
                    || name.endsWith("jpg")
                    || name.endsWith("jpeg"))))
            {
                if (file.getName().endsWith("gif"))
                {
                    if (!gifs.containsKey(file.getName()))
                    {
                        try
                        {
                            GifImage gif = GifConverter.readGifImage(new FileInputStream(file), file.getName());
                            gifList.add(gif);
                            gifs.put(file.getName(), gif);
                        }
                        catch (IOException e)
                        {
                            Earthhack.getLogger().error("Failed to load gif image " + file.getName() + "!");
                            e.printStackTrace();
                        }
                    }
                }
                else
                {
                    try
                    {
                        if (!textures.containsKey(file.getName()))
                        {
                            String[] split = file.getName().split("\\.");
                            String format = split[split.length - 1];
                            NameableImage image = new NameableImage(
                                ImageUtil.cacheBufferedImage(ImageUtil.createFlipped(ImageUtil.bufferedImageFromFile(file)), format, file.getName()), file.getName());
                            imageList.add(image);
                            textures.put(file.getName(), image);
                        }
                    }
                    catch (IOException | NoSuchAlgorithmException e)
                    {
                        Earthhack.getLogger().error("Failed to load image " + file.getName() + "!");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void handleModelDir(File dir)
    {
        if (dir.isDirectory())
        {
            for (File file : Objects.requireNonNull(dir.listFiles()))
            {
                if (!file.isDirectory())
                {
                    try
                    {
                        if (!models.containsKey(file.getName())
                            && !(file.getName().endsWith("gif")
                            || file.getName().endsWith("png")
                            || file.getName().endsWith("jpg")
                            || file.getName().endsWith("jpg")))
                        {
                            IModel model = ModelUtil.loadModel(file.getAbsolutePath(), file.getParent(), true);
                            model.setName(file.getName());
                            modelList.add(model);
                            models.put(file.getName(), model);
                        }
                    }
                    catch (IOException e)
                    {
                        Earthhack.getLogger().error("Failed to load model: " + file.getName() + "!");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void handleShaderDir(File dir)
    {
        if (dir.isDirectory()) {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                try
                {
                    if (!shaders.containsKey(file.getName())
                        && file.getName().endsWith(".shader"))
                    {
                        SettingShader shader = new SettingShader(new FileInputStream(file), file.getName());
                        shaders.put(file.getName(), shader);
                        shaderList.add(shader);
                    }
                }
                catch (IOException e)
                {
                    Earthhack.getLogger().error("Failed to shader model: " + file.getName() + "!");
                    e.printStackTrace();
                }
            }
        }
    }

    public GifImage getGif(String image)
    {
        return gifs.get(image);
    }

    public NameableImage getImage(String image)
    {
        return textures.get(image);
    }

    public IModel getModel(String model)
    {
        return models.get(model);
    }

    public List<GifImage> getGifs()
    {
        return gifList;
    }

    public List<NameableImage> getImages()
    {
        return imageList;
    }

    public List<IModel> getModels()
    {
        return modelList;
    }

    public List<SettingShader> getShaders()
    {
        return shaderList;
    }

    public IModel getInitialModel()
    {
        if (!modelList.isEmpty())
        {
            return modelList.get(0);
        }
        else
        {
            return new IModel()
            {
                @Override
                public void setupMesh(Mesh mesh) {

                }

                @Override
                public Mesh[] genMeshes(AiScene scene) {
                    return new Mesh[0];
                }

                @Override
                public void render(double x, double y, double z, double partialTicks) {

                }

                @Override
                public Mesh[] getMeshes() {
                    return new Mesh[0];
                }

                @Override
                public String getName() {
                    return "None!";
                }

                @Override
                public void setName(String name) {}
            };
        }
    }

    public NameableImage getInitialImage()
    {
        if (!imageList.isEmpty())
        {
            return imageList.get(0);
        }
        else
        {
            return new NameableImage(null,"None!");
        }
    }

    public GifImage getInitialGif()
    {
        if (!gifList.isEmpty())
        {
            return gifList.get(0);
        }
        else
        {
            return new GifImage(new ArrayList<BufferedImage>(), 0, "None!");
        }
    }

    // TODO:
    public SettingShader getInitialShader()
    {
        if (!shaderList.isEmpty())
        {
            return shaderList.get(0);
        }
        else
        {
            return new SettingShader("default");
        }
    }
}
