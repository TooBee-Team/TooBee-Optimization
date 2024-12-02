package top.toobee.optimization.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.LookAtMobTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.toobee.optimization.cache.StackingMobCache;
import top.toobee.optimization.intermediary.CachedMob;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(LookAtMobTask.class)
public abstract class LookAtMobTaskMixin {
    @Redirect(method = "method_47063", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/ai/brain/LivingTargetCache;findFirst(Ljava/util/function/Predicate;)Ljava/util/Optional;"))
    private static Optional<LivingEntity> redirect(
            LivingTargetCache instance,
            Predicate<LivingEntity> predicate,
            TaskTriggerer.TaskContext<LivingEntity> context,
            MemoryQueryResult<?,?> lookTarget,
            Predicate<LivingEntity> predicate2,
            float maxDistance,
            MemoryQueryResult<?,?> visibleMobs,
            ServerWorld world,
            LivingEntity entity) {
        final StackingMobCache<?> cache;
        final Optional<LivingEntity> optional;
        if (entity instanceof CachedMob<?,?> && (cache = ((CachedMob<?,?>) entity).toobee$getCache()) != null) {
            if (cache.getHasUpdatedThisTick()) {
                return cache.getLookAtMobTaskEntity();
            } else {
                optional = instance.findFirst(predicate);
                cache.setLookAtMobTaskEntity(optional);
            }
        } else {
            optional = instance.findFirst(predicate);
        }
        return optional;
    }
}
