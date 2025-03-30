package juloos.playerinteractionstrigger;

import juloos.playerinteractionstrigger.command.InteractCommand;
import juloos.playerinteractionstrigger.network.Event;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.ResourceLocation;

public class ClientPlayerInteractionsTrigger implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Event.initRegistries();
        Event.initCustomPayloadClient();
        ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath("pit", "sub_game_type"), SubGameType.SubGameModeArgument.class, SingletonArgumentInfo.contextFree(SubGameType.SubGameModeArgument::subGameMode));
        ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath("pit", "enum_bool"), InteractCommand.EnumBool.EnumBoolArgument.class, SingletonArgumentInfo.contextFree(InteractCommand.EnumBool.EnumBoolArgument::enumBool));
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> InteractCommand.register(dispatcher));
    }
}
