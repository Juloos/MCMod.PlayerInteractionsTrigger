package juloos.playerinteractionstrigger.mixin;

import juloos.playerinteractionstrigger.bridge.AbilitiesBridge;
import net.minecraft.world.entity.player.Abilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Abilities.class)
public abstract class AbilitiesMixin implements AbilitiesBridge {
    @Unique
    boolean pit$mayMine = true;

    @Override
    public boolean pit$mayMine() {
        return this.pit$mayMine;
    }

    @Override
    public void pit$mayMine(boolean mayMine) {
        this.pit$mayMine = mayMine;
    }
}
