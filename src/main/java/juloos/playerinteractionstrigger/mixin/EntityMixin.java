package juloos.playerinteractionstrigger.mixin;

import juloos.playerinteractionstrigger.SubGameType;
import juloos.playerinteractionstrigger.network.ClientboundPlayerTagsUpdatePacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow @Final private Set<String> tags;

    @Shadow public abstract @Nullable MinecraftServer getServer();


    @Unique
    private void pit$sendTagsUpdateIfServer(Player player) {
        if (this.getServer() == null)
            return;
        this.getServer().getPlayerList().getPlayers().forEach(serverPlayer -> {
            ServerPlayNetworking.send(serverPlayer, new ClientboundPlayerTagsUpdatePacket(
                    player.getUUID(),
                    player.getTags().stream().filter(t -> t.startsWith("pit.")).toList()
            ));
        });
    }


    @Inject(
            method = "addTag",
            at = @At("HEAD"),
            cancellable = true
    )
    private void pit$addTag(String string, CallbackInfoReturnable<Boolean> cir) {
        if (((Entity) (Object) this) instanceof Player player) {
            if (this.tags.size() >= 1024) {
                cir.setReturnValue(false);
            } else {
                boolean val = this.tags.add(string);
                if (val) {
                    SubGameType.updatePlayerAbilities(player);
                    this.pit$sendTagsUpdateIfServer(player);
                }
                cir.setReturnValue(val);
            }
        }
    }

    @Inject(
            method = "removeTag",
            at = @At("HEAD"),
            cancellable = true
    )
    private void pit$removeTag(String string, CallbackInfoReturnable<Boolean> cir) {
        if (((Entity) (Object) this) instanceof Player player) {
            boolean val = this.tags.remove(string);
            if (val) {
                SubGameType.updatePlayerAbilities(player);
                this.pit$sendTagsUpdateIfServer(player);
            }
            cir.setReturnValue(val);
        }
    }
}
