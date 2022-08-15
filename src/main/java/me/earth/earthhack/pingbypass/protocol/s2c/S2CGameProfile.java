package me.earth.earthhack.pingbypass.protocol.s2c;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.ducks.network.INetHandlerPlayClient;
import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.core.mixins.entity.living.player.IEntityPlayer;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import me.earth.earthhack.pingbypass.protocol.S2CPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.util.UUID;

public class S2CGameProfile extends S2CPacket implements Globals {
    private static final ModuleCache<PingBypassModule> MODULE =
        Caches.getModule(PingBypassModule.class);
    private static final ModuleCache<Media> MEDIA =
        Caches.getModule(Media.class);

    private GameProfile profile;

    public S2CGameProfile() {
        super(ProtocolIds.S2C_GAME_PROFILE);
    }

    public S2CGameProfile(EntityPlayer player) {
        super(ProtocolIds.S2C_GAME_PROFILE);
        this.profile = new GameProfile(player.getUniqueID(), player.getName());
    }

    public S2CGameProfile(GameProfile profileIn) {
        super(ProtocolIds.S2C_GAME_PROFILE);
        this.profile = profileIn;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        String s = buf.readString(36);
        String s1 = buf.readString(16);
        UUID uuid = s.length() > 0 ? UUID.fromString(s) : null;
        this.profile = new GameProfile(uuid, s1);
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        UUID uuid = this.profile.getId();
        buf.writeString(uuid == null ? "" : uuid.toString());
        buf.writeString(this.profile.getName());
    }

    @Override
    public void execute(NetworkManager networkManager) {
        mc.addScheduledTask(() -> {
            Earthhack.getLogger().info(
                "Received PingBypass GameProfile: " + profile);
            if (networkManager.getNetHandler() instanceof INetHandlerPlayClient) {
                ((INetHandlerPlayClient) networkManager.getNetHandler())
                    .setGameProfile(this.profile);
            }

            if (mc.playerController != null) {
                ((INetHandlerPlayClient) ((IPlayerControllerMP)
                    mc.playerController).getConnection())
                    .setGameProfile(this.profile);
            }

            if (mc.player != null) {
                ((IEntityPlayer) mc.player).setGameProfile(this.profile);
                ((INetHandlerPlayClient) mc.player.connection)
                    .setGameProfile(this.profile);
            }

            MODULE.get().setServerName(this.profile.getName());
            MEDIA.computeIfPresent(media -> media.setPingBypassName(
                this.profile.getName()));
        });
    }

}
