package me.earth.earthhack.impl.util.render.image;

public abstract class Texture
{

	protected int id = -1;
	protected int width;
	protected int height;

	public Texture(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	protected abstract void init();

	public abstract void bind();

	public abstract void unbind();

	public int getId()
	{
		return id;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

}
