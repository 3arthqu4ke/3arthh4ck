package me.earth.earthhack.impl.util.render.image;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.api.util.interfaces.Nameable;
import org.lwjgl.Sys;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class GifImage implements Globals, Nameable
{

    private String name;
    private List<BufferedImage> frames = new LinkedList<>();
    private List<EfficientTexture> textures = new LinkedList<>();
    private int offset;
    private int delay;
    private boolean firstUpdate;
    private long lastUpdate;
    private long timeLeft;

    public GifImage(List<BufferedImage> images, int delay, String name)
    {
        this.name = name;
        for (BufferedImage image : images)
        {
            this.frames.add(ImageUtil.createFlipped(image));
        }
        this.offset = 0;
        this.delay = delay;
        firstUpdate = true;
        for (BufferedImage image : this.frames)
        {
            try
            {
                String generatedString = UUID.randomUUID().toString().split("-")[0];
                textures.add(ImageUtil.cacheBufferedImage(image, "gif", generatedString));
            }
            catch (NoSuchAlgorithmException | IOException e)
            {
                e.printStackTrace();
            }
        }
        reset();
    }

    // TODO: clip down sheet
    public GifImage(String name, int delay, List<EfficientTexture> textures)
    {
        this.name = name;
        this.delay = delay;
        this.offset = 0;
        this.textures = textures;
        this.firstUpdate = true;
        reset();
    }

    public void reset() {
        firstUpdate = true;
        timeLeft = delay;
        offset = 0;
    }

    public BufferedImage getBufferedImage()
    {
        if (updateOffset()) {
            return null;
        }
        return frames.get(offset);
    }

    public EfficientTexture getDynamicTexture()
    {
        if (updateOffset()) {
            return null;
        }
        return textures.get(offset);
    }

    /**
     * @return true if {@code frames.size() == 0}
     */
    private boolean updateOffset()
    {
        if (frames.size() == 0) return true;
        long now = getTime();
        long delta = now - lastUpdate;
        if (firstUpdate) {
            delta = 0;
            firstUpdate = false;
        }
        lastUpdate = now;
        timeLeft -= delta;
        if (timeLeft <= 0)
        {
            offset++;
            timeLeft = delay;
        }
        if (offset >= frames.size()) offset = 0;
        return false;
    }

    private long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    @Override
    public String getName()
    {
        return name;
    }

    public int getTextureSize() {
        return textures.size();
    }

}
