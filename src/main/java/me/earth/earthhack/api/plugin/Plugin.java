package me.earth.earthhack.api.plugin;

/**
 * Plugins can be used to add functionality, modules or commands
 * to 3arthh4ck. Plugins can contain Mixins and a MixinConfig and
 * are located in the earthhack/plugins folder. A Plugin should
 * be a jar file. Dependencies like Mixin don't need to be included
 * as they are already included in the 3arthh4ck jar.
 * <p>
 * TODO: CorePlugin implementing IClassTransformer for ASM if requested?
 * TODO: unload plugins
 */
public interface Plugin
{
    /**
     * Loads this Plugin.
     */
    void load();

}
