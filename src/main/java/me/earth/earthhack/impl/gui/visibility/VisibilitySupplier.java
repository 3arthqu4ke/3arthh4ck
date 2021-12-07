package me.earth.earthhack.impl.gui.visibility;

/**
 * An interface representing the Visibility of something.
 */
public interface VisibilitySupplier
{
    /**
     * @return <tt>true</tt> if this should be visible.
     */
    boolean isVisible();

    /**
     * This method is used if there's already another
     * VisibilitySupplier registered, while we are
     * registering this VisibilitySupplier. In that
     * case this method will be called on the one
     * you are trying to register.
     *
     * The default implementation just returns
     * this Supplier, effectively overriding the other one.
     *
     * @param other the supplier to be composed with this one.
     * @return a combination of this and the other supplier.
     */
    @SuppressWarnings("unused")
    default VisibilitySupplier compose(VisibilitySupplier other)
    {
        return this;
    }
}
