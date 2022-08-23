package me.earth.earthhack.pingbypass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class PingBypassConfig {
    private final Properties properties = new Properties();
    private boolean loaded;

    public boolean isServer() {
        return Boolean.parseBoolean(getProperty("pb.server", "false"));
    }

    public String getIp() {
        return getProperty("pb.ip", "127.0.0.1");
    }

    public boolean noPassword() {
        return Boolean.parseBoolean(getProperty("pb.no.password", "false"));
    }

    public String getPassword() {
        return getProperty("pb.password", null);
    }

    public int getPort() {
        return Integer.parseInt(getProperty("pb.port", "25565"));
    }

    public boolean validateResourcePacksFurther() {
        return Boolean.parseBoolean(getProperty("pb.big.resource.validation", "false"));
    }

    public boolean shouldUseNativeTransport() {
        return Boolean.parseBoolean(getProperty("pb.native.transport", "false"));
    }

    public boolean enableJavaScript() {
        return Boolean.parseBoolean(getProperty("pb.enable.javascript", "false"));
    }

    public boolean enableQuitCommand() {
        return Boolean.parseBoolean(getProperty("pb.enable.quit", "false"));
    }

    public int getCompressionThreshold() {
        return Integer.parseInt(getProperty("pb.compression.threshold", "256"));
    }

    public String getProperty(String property, String def) {
        String system = System.getProperty(property);
        return system == null ? properties.getProperty(property, def) : system;
    }

    public boolean isLoaded() {
        return loaded;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void load() {
        try {
            File file = Paths.get("earthhack", "pingbypass.properties").toFile();
            file.getParentFile().mkdirs();
            file.createNewFile();
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            loaded = true;
        }
    }

}
