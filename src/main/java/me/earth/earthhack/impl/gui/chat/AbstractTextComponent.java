package me.earth.earthhack.impl.gui.chat;

import net.minecraft.util.text.TextComponentString;

//TODO: For all Components use setInsertion in Style!
//TODO: cool and all but actually not, especially wrapping is shit

/**
 * A Custom TextComponent.
 * <p></p>
 * <p>Important Note:
 * <p>The Parent Component should always be an AbstractComponent!
 * Using a {@link TextComponentString} for example and appending
 * AbstractComponents won't work properly. (TODO: fix)
 * For now you can use {@link AbstractTextComponent#EMPTY} for convenience.
 */
@SuppressWarnings("NullableProblems")
public abstract class AbstractTextComponent extends TextComponentString
{
    public static final AbstractTextComponent EMPTY = new AbstractTextComponent("")
    {
        @Override
        public String getText()
        {
            return "";
        }

        @Override
        public String getUnformattedComponentText()
        {
            return "";
        }

        @Override
        public TextComponentString createCopy()
        {
            return EMPTY;
        }
    };

    private boolean wrap;

    public AbstractTextComponent(String initial)
    {
        super(initial);
    }

    /**
     * Note that the wrapping on AbstractComponents is different,
     * (and a bit wonky) to preserve the integrity of all siblings
     * and components. So rather try not to wrap these.
     *
     * @param wrap returned by {@link AbstractTextComponent#isWrapping()}
     */
    public AbstractTextComponent setWrap(boolean wrap)
    {
        this.wrap = wrap;
        return this;
    }

    /**
     * @return <tt>false</tt> if this component can go
     *          outside the limits of chat.
     */
    public boolean isWrapping()
    {
        return this.wrap;
    }

    @Override
    public abstract String getText();

    @Override
    public abstract String getUnformattedComponentText();

    @Override
    public abstract TextComponentString createCopy();

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        else if (!(o instanceof AbstractTextComponent))
        {
            return false;
        }
        else
        {
            return this.getText().equals(((AbstractTextComponent) o).getText());
        }
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public String toString()
    {
        return "CustomComponent{text='"
                + this.getText()
                + '\''
                + ", siblings="
                + this.siblings
                + ", style="
                + this.getStyle()
                + '}';
    }

}
