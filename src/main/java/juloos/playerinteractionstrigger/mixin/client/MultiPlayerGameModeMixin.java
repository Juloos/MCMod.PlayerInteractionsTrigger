package juloos.playerinteractionstrigger.mixin.client;

import juloos.playerinteractionstrigger.SubGameType;
import juloos.playerinteractionstrigger.bridge.AbilitiesBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(
            method = "startDestroyBlock",
            at = @At("HEAD"),
            cancellable = true
    )
    private void pit$startDestroyBlock(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (this.minecraft.player != null && !((AbilitiesBridge) this.minecraft.player.getAbilities()).pit$mayMine())
            cir.setReturnValue(false);
    }

    @Inject(
            method = "continueDestroyBlock",
            at = @At("HEAD"),
            cancellable = true
    )
    private void pit$continueDestroyBlock(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (this.minecraft.player != null && !((AbilitiesBridge) this.minecraft.player.getAbilities()).pit$mayMine())
            cir.setReturnValue(false);
    }

    @Inject(
            method = "adjustPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/GameType;updatePlayerAbilities(Lnet/minecraft/world/entity/player/Abilities;)V"
            ),
            cancellable = true
    )
    private void pit$adjustPlayer(Player player, CallbackInfo ci) {
        SubGameType.updatePlayerAbilities(player);
        ci.cancel();
    }

    @Inject(
            method = "setLocalMode(Lnet/minecraft/world/level/GameType;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/GameType;updatePlayerAbilities(Lnet/minecraft/world/entity/player/Abilities;)V"
            ),
            cancellable = true
    )
    private void pit$setLocalMode(GameType gameType, CallbackInfo ci) {
        assert this.minecraft.player != null;  // Impossible
        SubGameType.updatePlayerAbilities(this.minecraft.player);
        ci.cancel();
    }

    @Inject(
            method = "setLocalMode(Lnet/minecraft/world/level/GameType;Lnet/minecraft/world/level/GameType;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/GameType;updatePlayerAbilities(Lnet/minecraft/world/entity/player/Abilities;)V"
            ),
            cancellable = true
    )
    private void pit$setLocalMode(GameType gameType, GameType gameType2, CallbackInfo ci) {
        assert this.minecraft.player != null;  // Impossible
        SubGameType.updatePlayerAbilities(this.minecraft.player);
        ci.cancel();

    }
}
