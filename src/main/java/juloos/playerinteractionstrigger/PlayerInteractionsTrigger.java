package juloos.playerinteractionstrigger;

import juloos.playerinteractionstrigger.command.InteractCommand;
import juloos.playerinteractionstrigger.network.Event;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
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
    }
}
