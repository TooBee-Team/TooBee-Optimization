package top.toobee.optimization.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.sensing.PiglinSpecificSensor;
import net.minecraft.world.entity.monster.piglin.Piglin;
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
    @Inject(method = "doTick", at = @At("HEAD"), cancellable = true)
    public void head(final ServerLevel world, final LivingEntity entity, final CallbackInfo ci) {
        if (entity instanceof Piglin piglin) {
            final var cache = ((CachedPiglin) piglin).toobee$getCache();
            if (cache != null && cache.getHasUpdatedThisTick()) {
                cache.newSense(world, piglin);
                ci.cancel();
            }
        }
    }

    @Inject(method = "doTick", at = @At("TAIL"))
    public void tail(final ServerLevel world, final LivingEntity entity, final CallbackInfo ci, @Local final Brain<Piglin> brain) {
        if (entity instanceof Piglin piglin) {
            final var b = (CachedMob<?,?>) piglin;
            if (b.toobee$getCache() instanceof PiglinCache cache)
                cache.reset(brain);
        }
    }
}
