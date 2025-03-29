package juloos.playerinteractionstrigger;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import juloos.playerinteractionstrigger.bridge.AbilitiesBridge;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SubGameType {
    PIT$DISABLED_BUILDING, PIT$DISABLED_MINING;

    public static void updatePlayerAbilities(Player player) {
        Abilities abilities = player.getAbilities();
        if (player instanceof ServerPlayer serverPlayer)  // Dangerous Client-Server side check, but cannot technically crash
            serverPlayer.gameMode.getGameModeForPlayer().updatePlayerAbilities(abilities);
        else if (player instanceof AbstractClientPlayer clientPlayer && clientPlayer.playerInfo != null)
            clientPlayer.playerInfo.getGameMode().updatePlayerAbilities(abilities);
        ((AbilitiesBridge) abilities).pit$mayMine(true);
        player.getTags().stream().map(SubGameType::fromTag).filter(Objects::nonNull).forEach(subGameType -> {
            switch (subGameType) {
                case PIT$DISABLED_BUILDING:
                    abilities.mayBuild = false;
                    break;
                case PIT$DISABLED_MINING:
                    ((AbilitiesBridge) abilities).pit$mayMine(false);
                    break;
            }
        });
    }

    public static String toTag(SubGameType subGameType) {
        return subGameType.name().replace('$', '.').toLowerCase();
    }

    public static SubGameType fromTag(String string) {
        try {
            return SubGameType.valueOf(string.replace('.', '$').toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static class SubGameModeArgument implements ArgumentType<SubGameType> {
        private static final Collection<String> VALUES;
        private static final DynamicCommandExceptionType ERROR_INVALID;

        public SubGameType parse(StringReader stringReader) throws CommandSyntaxException {
            String string = stringReader.readUnquotedString();
            try {
                return SubGameType.valueOf("PIT$DISABLED_" + string.toUpperCase());
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

        public static SubGameModeArgument subGameMode() {
            return new SubGameModeArgument();
        }

        public static SubGameType getSubGameMode(CommandContext<CommandSourceStack> commandContext, String string) {
            return commandContext.getArgument(string, SubGameType.class);
        }

        static {
            VALUES = Stream.of(SubGameType.values()).map(subGameType -> subGameType.name().replaceFirst("PIT\\$DISABLED_", "").toLowerCase()).collect(Collectors.toList());
            ERROR_INVALID = new DynamicCommandExceptionType((object) -> Component.literal("Invalid SubGameType argument: " + object));
        }
    }
}
