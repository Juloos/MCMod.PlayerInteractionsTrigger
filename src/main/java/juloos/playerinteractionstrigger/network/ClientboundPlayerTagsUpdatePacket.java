package juloos.playerinteractionstrigger.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientboundPlayerTagsUpdatePacket implements CustomPacketPayload {
    public static Type<ClientboundPlayerTagsUpdatePacket> ID = new Type<>(ResourceLocation.fromNamespaceAndPath("playerinteractionstrigger", "player_tags_update"));

    public final UUID playerId;
    public final List<String> playerTags;

    public ClientboundPlayerTagsUpdatePacket(UUID playerId, List<String> playerTags) {
        this.playerId = playerId;
        this.playerTags = playerTags;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }


    public static StreamCodec<FriendlyByteBuf, ClientboundPlayerTagsUpdatePacket> CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, ClientboundPlayerTagsUpdatePacket packet) {
            buf.writeUUID(packet.playerId);
            buf.writeCollection(packet.playerTags, FriendlyByteBuf::writeUtf);
        }

        @Override
        public ClientboundPlayerTagsUpdatePacket decode(FriendlyByteBuf buf) {
            UUID playerId = buf.readUUID();
            List<String> playerTags = buf.readCollection(ArrayList::new, FriendlyByteBuf::readUtf);
            return new ClientboundPlayerTagsUpdatePacket(playerId, playerTags);
        }
    };
}
