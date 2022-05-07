package me.earth.modeltotem;

import com.badlogic.gdx.physics.bullet.Bullet;
import jassimp.IHMCJassimp;
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.PluginManager;
import me.earth.earthhack.impl.modules.render.modeltotem.ModelTotem;
import me.earth.earthhack.impl.modules.render.rechams.ReChams;
import me.earth.earthhack.impl.util.misc.ReflectionUtil;
import us.ihmc.tools.nativelibraries.NativeLibraryLoader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

@SuppressWarnings("unused")
public class ModelTotemPlugin implements Plugin {
    @Override
    public void load() {
        Earthhack.getLogger().info("Loading ModelTotem Plugin!");
        // Dirty hack because commons-lang3 for Minecraft seems to be missing the ArchUtils.
        // and the LaunchClassloader ignores ours because it has an exclusion on org.apache.commons
        // and for some reason the ProtectionDomain stuff doesn't work either so we need to check all plugins.
        // TODO: this is so dumb wtf
        try {
            File d = new File("earthhack/plugins");
            for (File file : Objects.requireNonNull(d.listFiles())) {
                if (file.getName().endsWith(".jar")) {
                    try {
                        JarFile jarFile = new JarFile(file);
                        Manifest manifest = jarFile.getManifest();
                        if (manifest == null) {
                            continue;
                        }

                        Attributes attributes = manifest.getMainAttributes();
                        if (attributes.getValue("3arthh4ckConfig").equals("ModelTotemPluginConfig.json")) {
                            Earthhack.getLogger().debug("Adding " + file.getAbsolutePath() + " to classpath");
                            ReflectionUtil.addToClassPath((URLClassLoader) ClassLoader.getSystemClassLoader(), file);
                            // break; vanilla mapped plugin could exist TODO: detect
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //noinspection InstantiationOfUtilityClass
        new IHMCJassimp(); // call static initializer
        NativeLibraryLoader.loadLibrary("bullet", "bullet");
        Bullet.init();

        ModelTotemFileManager.INSTANCE.init();
        try {
            Managers.MODULES.register(new ModelTotem());
            Managers.MODULES.register(new ReChams());
        } catch (AlreadyRegisteredException e) {
            Earthhack.getLogger().error("Couldn't register ModelTotem module!");
            e.printStackTrace();
        }
    }

}
