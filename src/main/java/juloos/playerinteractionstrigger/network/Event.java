package juloos.playerinteractionstrigger.network;

import juloos.playerinteractionstrigger.SubGameType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;

public class Event {
    public static void initRegistries() {
        try {
            PayloadTypeRegistry.playS2C().register(ClientboundPlayerTagsUpdatePacket.ID, ClientboundPlayerTagsUpdatePacket.CODEC);
        } catch (IllegalArgumentException ignored) {
            // Already registered
        }
    }

    public static void initCustomPayloadClient() {
        ClientPlayNetworking.registerGlobalReceiver(ClientboundPlayerTagsUpdatePacket.ID, (payload, context) -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null)
                return;
            Player player = level.getPlayerByUUID(payload.playerId);
            if (player == null)
                return;
            player.getTags().clear();
            player.getTags().addAll(payload.playerTags);
            SubGameType.updatePlayerAbilities(player);
        });
    }
}
