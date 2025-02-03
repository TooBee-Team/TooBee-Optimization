package top.toobee.optimization.accessor;

import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PiglinBrain.class)
public interface PiglinBrainInvoker {
    @Invoker("doesNotHaveGoldInOffHand")
    static boolean doesNotHaveGoldInOffHand(PiglinEntity piglin) {
        throw new AssertionError();
    }
}
