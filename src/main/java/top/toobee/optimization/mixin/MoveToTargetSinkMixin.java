package top.toobee.optimization.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.toobee.optimization.cache.StackingMobCache;
import top.toobee.optimization.intermediary.CachedMob;

@Mixin(MoveToTargetSink.class)
public abstract class MoveToTargetSinkMixin {
    @Unique
    private StackingMobCache<?> cache = null;

    @Inject(method = "tryComputePath", cancellable = true, at = @At("HEAD"))
    public void hasFinishedPathHead(Mob mob, WalkTarget walkTarget, long time, CallbackInfoReturnable<Boolean> cir) {
        if (mob instanceof CachedMob<?,?> beCached) {
            cache = beCached.toobee$getCache();
            if (cache != null) {
                final Boolean y = cache.shouldRunMoveToTargetTask;
                if (y != null)
                    cir.setReturnValue(y);
            }
        }
    }

    @Inject(method = "tryComputePath", at = @At(value = "RETURN"))
    public void hasFinishedPathReturn(Mob mob, WalkTarget walkTarget, long time, CallbackInfoReturnable<Boolean> cir) {
        if (cache != null && cache.shouldRunMoveToTargetTask == null) {
            cache.shouldRunMoveToTargetTask = cir.getReturnValueZ();
        }
    }
}
