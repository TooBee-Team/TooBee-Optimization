package top.toobee.optimization.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.NearestLivingEntitiesSensor;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.toobee.optimization.cache.CachedMob;

import java.util.List;

@Mixin(NearestLivingEntitiesSensor.class)
public abstract class NearestLivingEntitiesSensorMixin<T extends LivingEntity> {
    @Inject(method = "sense", at = @At("HEAD"), cancellable = true)
    public void head(final ServerWorld world, final T entity, final CallbackInfo ci) {
        // The implementation of warden is moved to WardenAttackablesSensorMixin
        // Wait for completing more
        if (entity instanceof PiglinEntity mob) {
            final var cache = ((CachedMob<?, ?>) entity).toobee$getCache();
            if (cache != null && cache.getHasUpdatedThisTick()) {
                cache.newSense(world, mob);
                ci.cancel();
            }
        }
    }

    @Inject(method = "sense", at = @At("TAIL"))
    public void tail(final ServerWorld world, final T entity, final CallbackInfo ci, @Local final List<LivingEntity> list) {
        if (entity instanceof CachedMob<?,?> m) {
            final var b = m.toobee$getCache();
            if (b == null)
                m.toobee$checkToCreateCache(list, world, entity.getBlockPos());
            else
                b.setTrackers(list);
        }
    }
}
