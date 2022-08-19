package me.earth.earthhack.installer.version;

import com.google.gson.JsonObject;

import java.io.File;

public class Version
{
    private final String name;
    private final File file;
    private final JsonObject json;
    
    public Version(final String name, final File file, final JsonObject json) {
        this.name = name;
        this.file = file;
        this.json = json;
    }
    
    public String getName() {
        return this.name;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public JsonObject getJson() {
        return this.json;
    }
}
