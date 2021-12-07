package me.earth.earthhack.installer;

import me.earth.earthhack.installer.version.Version;

// TODO: option to add new Version!
/**
 * Interface representing the actions an Installer can perform.
 */
public interface Installer
{
    /**
     * Refreshes all Versions.
     *
     * @return <tt>true</tt> if an exception has been thrown.
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean refreshVersions();

    /**
     * Installs the given version.
     * Also calls {@link Installer#update(boolean)}
     *
     * @return <tt>true</tt> if an exception has been thrown.
     */
    boolean install(Version version);

    /**
     * Uninstalls the given version.
     *
     * @return <tt>true</tt> if an exception has been thrown.
     */
    boolean uninstall(Version version);

    /**
     * Puts this jar into the folder and remaps it if needed.
     *
     * @param forge if we don't need to remap.
     * @return <tt>true</tt> if an exception has been thrown.
     */
    boolean update(boolean forge);

}
