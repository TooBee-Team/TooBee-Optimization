package top.toobee.optimization.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.sensor.PiglinSpecificSensor;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.toobee.optimization.cache.PiglinCache;
import top.toobee.optimization.intermediary.CachedMob;
import top.toobee.optimization.intermediary.CachedPiglin;

//@Debug(export = true)
@Mixin(PiglinSpecificSensor.class)
public abstract class PiglinSpecificSensorMixin {
    @Inject(method = "sense", at = @At("HEAD"), cancellable = true)
    public void head(final ServerWorld world, final LivingEntity entity, final CallbackInfo ci) {
        if (entity instanceof PiglinEntity piglin) {
            final var cache = ((CachedPiglin) piglin).toobee$getCache();
            if (cache != null && cache.getHasUpdatedThisTick()) {
                cache.newSense(world, piglin);
                ci.cancel();
            }
        }
    }

    @Inject(method = "sense", at = @At("TAIL"))
    public void tail(final ServerWorld world, final LivingEntity entity, final CallbackInfo ci, @Local final Brain<PiglinEntity> brain) {
        if (entity instanceof PiglinEntity piglin) {
            final var b = (CachedMob<?,?>) piglin;
            if (b.toobee$getCache() instanceof PiglinCache cache)
                cache.reset(brain);
        }
    }
}
