package me.earth.earthhack.impl.util.thread;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.media.Media;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LookUpUtil implements Globals
{
    private static final ModuleCache<Media> MEDIA = Caches.getModule(
        Media.class);
    private static final BiMap<String, UUID> CACHE = HashBiMap.create();
    private static final JsonParser PARSER = new JsonParser();

    public static UUID getUUIDSimple(String name)
    {
        UUID cached = CACHE.get(name);
        if (cached != null)
        {
            return cached;
        }

        if (mc.getConnection() != null)
        {
            List<NetworkPlayerInfo> infoMap =
                    new ArrayList<>(mc.getConnection().getPlayerInfoMap());
            NetworkPlayerInfo profile = infoMap.stream().filter(info ->
                                                info
                                                    .getGameProfile()
                                                    .getName()
                                                    .equalsIgnoreCase(name))
                                            .findFirst()
                                            .orElse(null);
            if (profile != null)
            {
                UUID result = profile.getGameProfile().getId();
                CACHE.forcePut(name, result);
                return result;
            }
        }

        return null;
    }

    public static UUID getUUID(String name)
    {
        String ids = requestIDs("[\"" + name + "\"]");

        if (ids == null || ids.isEmpty())
        {
            return null;
        }
        else
        {
            JsonElement element = PARSER.parse(ids);
            if (element.getAsJsonArray().size() == 0)
            {
                return null;
            }
            else
            {
                try
                {
                    String id = element
                                    .getAsJsonArray()
                                    .get(0)
                                    .getAsJsonObject()
                                    .get("id")
                                    .getAsString();
                    UUID result =
                            UUIDTypeAdapter.fromString(id);
                    CACHE.forcePut(name, result);
                    return result;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static String getNameSimple(UUID uuid)
    {
        String cached = CACHE.inverse().get(uuid);
        if (cached != null)
        {
            return cached;
        }

        if (mc.getConnection() != null)
        {
            List<NetworkPlayerInfo> infoMap = new ArrayList<>(
                                        mc.getConnection().getPlayerInfoMap());
            for (NetworkPlayerInfo info : infoMap)
            {
                GameProfile gameProfile = info.getGameProfile();
                if (gameProfile.getId().equals(uuid))
                {
                    String name = gameProfile.getName();
                    CACHE.forcePut(name, uuid);
                    return name;
                }
            }
        }

        return null;
    }

    public static String getName(UUID uuid)
    {
        String url = "https://api.mojang.com/user/profiles/"
                        + uuidToString(uuid)
                        + "/names";
        try
        {
            String name = IOUtils.toString(new URL(url),
                                           StandardCharsets.UTF_8);
            JsonArray array = (JsonArray) PARSER.parse(name);
            String player = array.get(array.size() - 1).toString();
            JsonObject object = (JsonObject) PARSER.parse(player);
            String result = object.get("name").toString();
            CACHE.inverse().put(uuid, result);
            return result;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static String requestIDs(String data)
    {
        try
        {
            String query = "https://api.mojang.com/profiles/minecraft";
            URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type",
                                    "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes(StandardCharsets.UTF_8));
            os.close();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String res = convertStreamToString(in);
            in.close();
            conn.disconnect();
            return res;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static Map<Date, String> getNameHistory(UUID id)
    {
        Map<Date, String> result = new TreeMap<>(Collections.reverseOrder());

        try
        {
            JsonArray array = getResources(
                    new URL("https://api.mojang.com/user/profiles/"
                            + uuidToString(id)
                            + "/names"))
                    .getAsJsonArray();

            for (JsonElement element : array)
            {
                JsonObject node = element.getAsJsonObject();
                String name = node.get("name").getAsString();
                long changedAt = node.has("changedToAt")
                        ? node.get("changedToAt").getAsLong()
                        : 0;

                result.put(new Date(changedAt), name);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    private static JsonElement getResources(URL url) throws Exception
    {
        HttpsURLConnection connection = null;

        try
        {
            connection = (HttpsURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder builder = new StringBuilder();

            while(scanner.hasNextLine())
            {
                builder.append(scanner.nextLine());
                builder.append('\n');
            }

            scanner.close();
            String json = builder.toString();
            return PARSER.parse(json);
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
    }

    public static String findNextPlayerName(String input)
    {
        if (mc.getConnection() != null)
        {
            List<NetworkPlayerInfo> infoMap = new ArrayList<>(
                                        mc.getConnection().getPlayerInfoMap());
            NetworkPlayerInfo profile =
                infoMap.stream()
                       .filter(info -> TextUtil.startsWith(
                               info.getGameProfile().getName(), input))
                       .findFirst()
                       .orElse(null);

            if (profile != null)
            {
                String name = profile.getGameProfile().getName();
                if (name != null && !MEDIA.returnIfPresent(
                    m -> m.isHidingInCommands(name), false)) {
                    return name;
                }
            }
        }

        for (String str : CACHE.keySet())
        {
            if (TextUtil.startsWith(str, input) && !MEDIA.returnIfPresent(
                m -> m.isHidingInCommands(str), false))
            {
                return str;
            }
        }

        return null;
    }

    public static String uuidToString(UUID uuid)
    {
        return uuid.toString().replace("-", "");
    }

    public static String convertStreamToString(InputStream is)
    {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "/";
    }

}
