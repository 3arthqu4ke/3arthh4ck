package me.earth.earthhack.installer.srg2notch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Fields annotated with {@link org.spongepowered.asm.mixin.Shadow}
 * or with this Annotation will have their name remapped.
 */
@SuppressWarnings("unused")
@Target(ElementType.FIELD)
public @interface RemapFieldName { }