package me.earth.earthhack.impl.util.render.image;

import me.earth.earthhack.api.util.interfaces.Nameable;
import org.newdawn.slick.Animation;

public class GifAnimationWrapper implements Nameable
{

    private final String name;
    private final Animation animation;

    public GifAnimationWrapper(String name, Animation animation)
    {
        this.name = name;
        this.animation = animation;
    }

    public int getCurrentFrameTexID()
    {
        if (animation == null) return 0;
        animation.updateNoDraw();
        return animation.getCurrentFrame().getTexture().getTextureID();
    }

    public Animation getAnimation()
    {
        return animation;
    }

    @Override
    public String getName()
    {
        return name;
    }

}
