package top.toobee.optimization.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.toobee.optimization.cache.StackingMobCache;
import top.toobee.optimization.intermediary.CachedMob;

import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

@Mixin(SetEntityLookTarget.class)
public abstract class LookAtMobTaskMixin {
    @Redirect(method = "lambda$create$5", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/memory/NearestVisibleLivingEntities;findClosest(Ljava/util/function/Predicate;)Ljava/util/Optional;"))
    private static Optional<LivingEntity> redirect(
            NearestVisibleLivingEntities instance,
            Predicate<LivingEntity> filter,
            @Local(argsOnly = true, name = "body") LivingEntity body
    ) {
        final StackingMobCache<?> cache;
        final Optional<LivingEntity> optional;
        if (body instanceof CachedMob<?,?> e && (cache = e.toobee$getCache()) != null) {
            if (cache.getHasUpdatedThisTick()) {
                return cache.lookAtMobTaskEntity;
            } else {
                optional = instance.findClosest(filter);
                cache.lookAtMobTaskEntity = optional;
            }
        } else {
            optional = instance.findClosest(filter);
        }
        return optional;
    }
}
