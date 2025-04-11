package top.toobee.optimization.mixin;

import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MoveToTargetTask;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.toobee.optimization.cache.StackingMobCache;
import top.toobee.optimization.intermediary.CachedMob;

@Mixin(MoveToTargetTask.class)
public abstract class MoveToTargetTaskMixin {
    @Unique
    private StackingMobCache<?> cache = null;

    @Inject(method = "hasFinishedPath", cancellable = true, at = @At("HEAD"))
    public void hasFinishedPathHead(MobEntity entity, WalkTarget walkTarget, long time, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof CachedMob<?,?> beCached) {
            cache = beCached.toobee$getCache();
            if (cache != null) {
                final Boolean y = cache.shouldRunMoveToTargetTask;
                if (y != null)
                    cir.setReturnValue(y);
            }
        }
    }

    @Inject(method = "hasFinishedPath", at = @At(value = "RETURN"))
    public void hasFinishedPathReturn(MobEntity entity, WalkTarget walkTarget, long time, CallbackInfoReturnable<Boolean> cir) {
        if (cache != null && cache.shouldRunMoveToTargetTask == null) {
            cache.shouldRunMoveToTargetTask = cir.getReturnValueZ();
        }
    }
}
