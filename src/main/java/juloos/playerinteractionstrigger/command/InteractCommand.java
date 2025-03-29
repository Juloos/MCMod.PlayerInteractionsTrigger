package juloos.playerinteractionstrigger.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import juloos.playerinteractionstrigger.SubGameType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;
import java.util.Collection;

public class InteractCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(Commands.literal("interact")
            .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
            .then(Commands.argument("players", EntityArgument.players())
                .then(Commands.argument("interaction", SubGameType.SubGameModeArgument.subGameMode())
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(context -> {
                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "players");
                            SubGameType subGameType = SubGameType.SubGameModeArgument.getSubGameMode(context, "interaction");
                            if (BoolArgumentType.getBool(context, "value"))
                                players.forEach(player -> player.removeTag(SubGameType.toTag(subGameType)));
                            else
                                players.forEach(player -> player.addTag(SubGameType.toTag(subGameType)));
                            context.getSource().sendSystemMessage(Component.literal("Players' abilities have been updated"));
                            return players.size();
                        }))
                ).then(Commands.literal("reset")
                    .executes(context -> {
                        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "players");
                        players.forEach(player -> Arrays.stream(SubGameType.values()).map(SubGameType::toTag).forEach(player::removeTag));
                        context.getSource().sendSystemMessage(Component.literal("Players' abilities have been reset"));
                        return players.size();
                    })
                )));
    }
}
