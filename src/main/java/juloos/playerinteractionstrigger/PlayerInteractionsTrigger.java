package juloos.playerinteractionstrigger;

import juloos.playerinteractionstrigger.command.InteractCommand;
import juloos.playerinteractionstrigger.network.ClientboundPlayerTagsUpdatePacket;
import juloos.playerinteractionstrigger.network.Event;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.ResourceLocation;

public class PlayerInteractionsTrigger implements ModInitializer {
    @Override
    public void onInitialize() {
        Event.initRegistries();
        ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath("pit", "sub_game_type"), SubGameType.SubGameModeArgument.class, SingletonArgumentInfo.contextFree(SubGameType.SubGameModeArgument::subGameMode));
        ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath("pit", "enum_bool"), InteractCommand.EnumBool.EnumBoolArgument.class, SingletonArgumentInfo.contextFree(InteractCommand.EnumBool.EnumBoolArgument::enumBool));
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> InteractCommand.register(dispatcher));
        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> SubGameType.updatePlayerAbilities(handler.player)));
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 200 != 0)  // Every 10 seconds (if tick rate is 20)
                return;
            server.getPlayerList().getPlayers().forEach(serverPlayer -> {
                ServerPlayNetworking.send(serverPlayer, new ClientboundPlayerTagsUpdatePacket(
                        serverPlayer.getUUID(),
                        serverPlayer.getTags().stream().filter(t -> t.startsWith("pit.")).toList()
                ));
            });
        });
    }
}
