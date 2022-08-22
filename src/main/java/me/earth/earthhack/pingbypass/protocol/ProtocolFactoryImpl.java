package me.earth.earthhack.pingbypass.protocol;

import me.earth.earthhack.pingbypass.protocol.c2s.*;
import me.earth.earthhack.pingbypass.protocol.s2c.*;

public class ProtocolFactoryImpl extends ProtocolFactory {
    public ProtocolFactoryImpl() {
        this.register(ProtocolIds.C2S_JOIN, C2SJoinPacket::new);
        this.register(ProtocolIds.S2C_PASSWORD, S2CPasswordPacket::new);
        this.register(ProtocolIds.C2S_PASSWORD, C2SPasswordPacket::new);
        this.register(ProtocolIds.S2C_CONFIRM_TRANSACTION, S2CConfirmTransaction::new);
        this.register(ProtocolIds.C2S_COMMAND, C2SCommandPacket::new);
        this.register(ProtocolIds.S2C_CHAT, S2CChatPacket::new);
        this.register(ProtocolIds.C2S_CLEAR_FRIENDS, C2SClearFriendsPacket::new);
        this.register(ProtocolIds.C2S_FRIEND, C2SFriendPacket::new);
        this.register(ProtocolIds.S2C_POSITION, S2CPositionPacket::new);
        this.register(ProtocolIds.C2S_NO_MOTION_UPDATE, C2SNoMotionUpdateEvent::new);
        this.register(ProtocolIds.S2C_RENDER, S2CRenderPacket::new);
        this.register(ProtocolIds.C2S_SETTING, C2SSettingPacket::new);
        this.register(ProtocolIds.C2S_STAY, C2SStayPacket::new);
        this.register(ProtocolIds.S2C_ACTUAL_IP, S2CActualServerPacket::new);
        this.register(ProtocolIds.S2C_SWING, S2CSwingPacket::new);
        this.register(ProtocolIds.S2C_SETTING, S2CSettingPacket::new);
        this.register(ProtocolIds.C2S_KEYBOARD, C2SKeyboardPacket::new);
        this.register(ProtocolIds.C2S_POST_KEYBOARD, C2SPostKeyPacket::new);
        this.register(ProtocolIds.C2S_BIND_SETTING, C2SBindSettingPacket::new);
        this.register(ProtocolIds.S2C_BIND_SETTING, S2CBindSettingPacket::new);
        this.register(ProtocolIds.S2C_GAME_PROFILE, S2CGameProfile::new);
        this.register(ProtocolIds.C2S_MOUSE, C2SMousePacket::new);
        this.register(ProtocolIds.C2S_SPEED, C2SSpeedPacket::new);
        this.register(ProtocolIds.C2S_DAMAGE_BLOCK, C2SDamageBlockPacket::new);
        this.register(ProtocolIds.C2S_CLICK_BLOCK, C2SClickBlockPacket::new);
        this.register(ProtocolIds.C2S_OPEN_INVENTORY, C2SOpenInventory::new);
        this.register(ProtocolIds.S2C_WINDOW_CLICK, S2CWindowClick::new);
        this.register(ProtocolIds.S2C_WORLD_TICK, S2CWorldTickPacket::new);
        this.register(ProtocolIds.C2S_STEP, C2SStepPacket::new);
        this.register(ProtocolIds.C2S_ACTUAL_POS, C2SActualPos::new);
        this.register(ProtocolIds.C2S_NO_ROTATION, C2SNoRotation::new);
        this.register(ProtocolIds.S2C_UNLOAD_WORLD, S2CUnloadWorldPacket::new);
        this.register(ProtocolIds.C2S_RIDDEN_ENTITY, C2SRiddenEntityPosition::new);
        this.register(ProtocolIds.S2C_ASYNC_TOTEM, S2CAsyncTotemPacket::new);
        this.register(ProtocolIds.S2C_LAG_BACK, S2CLagBack::new);
        this.register(ProtocolIds.C2S_LAG_BACK_CONFIRM, C2SConfirmLagBack::new);
    }

}
