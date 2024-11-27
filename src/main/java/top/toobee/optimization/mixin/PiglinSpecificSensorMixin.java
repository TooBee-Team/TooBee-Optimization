package top.toobee.optimization.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.sensor.PiglinSpecificSensor;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.toobee.optimization.cache.BeCached;
import top.toobee.optimization.cache.CachedMob;
import top.toobee.optimization.cache.PiglinCache;

import java.util.List;

@Mixin(PiglinSpecificSensor.class)
public abstract class PiglinSpecificSensorMixin {
    @Inject(method = "sense", at = @At("HEAD"), cancellable = true)
    public void head(final ServerWorld world, final LivingEntity entity, final CallbackInfo ci) {
        if (entity instanceof PiglinEntity piglin) {
            @SuppressWarnings("unchecked")
            final var cache = ((BeCached<PiglinCache>) piglin).toobee$getCache();
            if (cache != null && cache.getHasUpdatedThisTick()) {
                cache.newSense(world, piglin);
                ci.cancel();
            }
        }
    }

    @Inject(method = "sense", at = @At("TAIL"))
    public void tail(final ServerWorld world, final LivingEntity entity, final CallbackInfo ci,
                     @Local final Brain<PiglinEntity> brain, @Local(ordinal = 1) final List<AbstractPiglinEntity> list) {
        if (entity instanceof PiglinEntity piglin) {
            @SuppressWarnings("unchecked")
            final var b = (CachedMob<PiglinEntity, PiglinCache>) piglin;
            if (b.toobee$getCache() instanceof PiglinCache cache)
                cache.reset(brain);
        }
    }
}
