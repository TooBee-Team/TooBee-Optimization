package top.toobee.optimization.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.toobee.optimization.cache.StackingMobCache;
import top.toobee.optimization.intermediary.CachedMob;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

@Mixin(SetEntityLookTarget.class)
public abstract class LookAtMobTaskMixin {
    @Redirect(method = "method_47063", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/memory/NearestVisibleLivingEntities;findClosest(Ljava/util/function/Predicate;)Ljava/util/Optional;"))
    private static Optional<LivingEntity> redirect(
            NearestVisibleLivingEntities instance,
            Predicate<LivingEntity> predicate,
            BehaviorBuilder.Instance<LivingEntity> context,
            MemoryAccessor<?,?> lookTarget,
            Predicate<LivingEntity> predicate2,
            float maxDistance,
            MemoryAccessor<?,?> visibleMobs,
            ServerLevel world,
            LivingEntity entity) {
        final StackingMobCache<?> cache;
        final Optional<LivingEntity> optional;
        if (entity instanceof CachedMob<?,?> && (cache = ((CachedMob<?,?>) entity).toobee$getCache()) != null) {
            if (cache.getHasUpdatedThisTick()) {
                return cache.lookAtMobTaskEntity;
            } else {
                optional = instance.findClosest(predicate);
                cache.lookAtMobTaskEntity = optional;
            }
        } else {
            optional = instance.findClosest(predicate);
        }
        return optional;
    }
}
