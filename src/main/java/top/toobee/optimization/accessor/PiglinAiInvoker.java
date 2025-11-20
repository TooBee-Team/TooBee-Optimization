package top.toobee.optimization.accessor;

import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PiglinAi.class)
public interface PiglinAiInvoker {
    @Invoker("isNotHoldingLovedItemInOffHand")
    static boolean doesNotHaveGoldInOffHand(Piglin piglin) {
        throw new AssertionError();
    }
}
