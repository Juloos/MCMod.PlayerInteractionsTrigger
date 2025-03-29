package juloos.playerinteractionstrigger.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final Minecraft minecraft;

    @Inject(
            method = "shouldRenderBlockOutline",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/player/Abilities;mayBuild:Z"
            ),
            cancellable = true
    )
    private void pit$shouldRenderBlockOutline(CallbackInfoReturnable<Boolean> cir) {
        if (this.minecraft.gameMode == null)
            return;
        cir.setReturnValue(!this.minecraft.gameMode.localPlayerMode.isBlockPlacingRestricted());
    }
}
