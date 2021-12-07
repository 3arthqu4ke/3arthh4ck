package me.earth.earthhack.installer;

import me.earth.earthhack.impl.util.thread.SafeRunnable;
import me.earth.earthhack.installer.gui.ErrorPanel;
import me.earth.earthhack.installer.gui.InstallerFrame;
import me.earth.earthhack.installer.gui.VersionPanel;
import me.earth.earthhack.installer.main.Library;
import me.earth.earthhack.installer.main.LibraryClassLoader;
import me.earth.earthhack.installer.main.LibraryFinder;
import me.earth.earthhack.installer.main.MinecraftFiles;
import me.earth.earthhack.installer.service.InstallerService;
import me.earth.earthhack.installer.version.Version;
import me.earth.earthhack.installer.version.VersionFinder;

import javax.swing.*;
import java.util.List;

// TODO: allow us to add new profiles etc
/**
 * {@link me.earth.earthhack.installer.main.Main#main(String[])}
 */
@SuppressWarnings("unused")
public class EarthhackInstaller implements Installer
{
    private final MinecraftFiles files;
    private final InstallerFrame gui;
    private InstallerService service;

    public EarthhackInstaller()
    {
        this.files = new MinecraftFiles();
        this.gui = new InstallerFrame();
    }

    public void launch(LibraryClassLoader classLoader, String[] args)
    {
        SwingUtilities.invokeLater(gui::display);

        wrapErrorGui(() ->
        {
            files.findFiles(args);

            LibraryFinder libraryFinder = new LibraryFinder();
            for (Library library : libraryFinder.findLibraries(files))
            {
                classLoader.installLibrary(library);
            }

            service = new InstallerService();
            refreshVersions();
        });
    }

    @Override
    public boolean refreshVersions()
    {
        return wrapErrorGui(() ->
        {
            VersionFinder versionFinder = new VersionFinder();
            List<Version> versions = versionFinder.findVersions(files);

            gui.schedule(new VersionPanel(this, versions));
        });
    }

    @Override
    public boolean install(Version version)
    {
        return wrapErrorGui(() ->
        {
            service.install(files, version);
            refreshVersions();
        });
    }

    @Override
    public boolean uninstall(Version version)
    {
        return wrapErrorGui(() ->
        {
            service.uninstall(version);
            refreshVersions();
        });
    }

    @Override
    public boolean update(boolean forge)
    {
        return wrapErrorGui(() ->
        {
            service.update(files, forge);
            refreshVersions();
        });
    }

    private boolean wrapErrorGui(SafeRunnable runnable)
    {
        try
        {
            runnable.runSafely();
            return false;
        }
        catch (Throwable throwable)
        {
            gui.schedule(new ErrorPanel(throwable));
            throwable.printStackTrace();
            return true;
        }
    }

}
