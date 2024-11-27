package top.toobee.optimization.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.NearestLivingEntitiesSensor;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.toobee.optimization.cache.CachedMob;
import top.toobee.optimization.cache.PiglinCache;
import top.toobee.optimization.cache.WardenCache;

import java.util.List;

@Mixin(NearestLivingEntitiesSensor.class)
public abstract class NearestLivingEntitiesSensorMixin<T extends LivingEntity> {
    @Inject(method = "sense", at = @At("HEAD"), cancellable = true)
    public void head(final ServerWorld world, final T entity, final CallbackInfo ci) {
        final var cache = ((CachedMob<?,?>) entity).toobee$getCache();
        // The implementation of warden is moved to WardenAttackablesSensorMixin
        // Wait for completing more
        if (entity instanceof PiglinEntity mob && cache != null && cache.getHasUpdatedThisTick()) {
            cache.newSense(world, mob);
            ci.cancel();
        }
    }

    @Inject(method = "sense", at = @At("TAIL"))
    public void tail(final ServerWorld world, final T entity, final CallbackInfo ci, @Local final List<LivingEntity> list) {
        if (entity instanceof CachedMob<?,?> m) {
            final var b = m.toobee$getCache();
            if (b != null)
                b.setTrackers(list);
            else if (entity instanceof WardenEntity) {
                @SuppressWarnings("unchecked") final var c = (CachedMob<WardenEntity, WardenCache>) m;
                WardenCache.Companion.checkToCreate(c, world, entity.getBlockPos(), list);
            } else if (entity instanceof PiglinEntity) {
                @SuppressWarnings("unchecked") final var c = (CachedMob<PiglinEntity, PiglinCache>) m;
                PiglinCache.Companion.checkToCreate(c, world, entity.getBlockPos(), list);
            }
        }
    }
}
