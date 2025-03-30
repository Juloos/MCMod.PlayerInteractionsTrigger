package juloos.playerinteractionstrigger.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import juloos.playerinteractionstrigger.SubGameType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InteractCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(Commands.literal("interact")
            .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
            .then(Commands.argument("players", EntityArgument.players())
                .then(Commands.argument("interaction", SubGameType.SubGameModeArgument.subGameMode())
                    .then(Commands.argument("value", EnumBool.EnumBoolArgument.enumBool())
                        .executes(context -> {
                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "players");
                            SubGameType subGameType = SubGameType.SubGameModeArgument.getSubGameMode(context, "interaction");
                            switch (EnumBool.EnumBoolArgument.getEnumBool(context, "value")) {
                                case TRUE -> players.forEach(player -> player.removeTag(SubGameType.toTag(subGameType)));
                                case FALSE -> players.forEach(player -> player.addTag(SubGameType.toTag(subGameType)));
                                case ONLY -> players.forEach(player -> {
                                    Arrays.stream(SubGameType.values()).map(SubGameType::toTag).forEach(player::addTag);
                                    player.removeTag(SubGameType.toTag(subGameType));
                                });
                            }
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

    public enum EnumBool {
        TRUE, FALSE, ONLY;

        public static class EnumBoolArgument implements ArgumentType<EnumBool> {
            private static final Collection<String> VALUES;
            private static final DynamicCommandExceptionType ERROR_INVALID;

            public EnumBool parse(StringReader stringReader) throws CommandSyntaxException {
                String string = stringReader.readUnquotedString();
                try {
                    return EnumBool.valueOf(string.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw ERROR_INVALID.createWithContext(stringReader, string);
                }
            }

            public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
                return commandContext.getSource() instanceof SharedSuggestionProvider ? SharedSuggestionProvider.suggest(VALUES, suggestionsBuilder) : Suggestions.empty();
            }

            public Collection<String> getExamples() {
                return VALUES;
            }

            public static EnumBoolArgument enumBool() {
                return new EnumBoolArgument();
            }

            public static EnumBool getEnumBool(CommandContext<CommandSourceStack> commandContext, String string) {
                return commandContext.getArgument(string, EnumBool.class);
            }

            static {
                VALUES = Stream.of(EnumBool.values()).map(enumBool -> enumBool.name().toLowerCase()).collect(Collectors.toList());
                ERROR_INVALID = new DynamicCommandExceptionType((object) -> Component.literal("Invalid EnumBool argument: " + object));
            }
        }
    }
}
