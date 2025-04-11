package top.toobee.optimization.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.NearestLivingEntitiesSensor;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.toobee.optimization.intermediary.CachedMob;
import top.toobee.optimization.intermediary.CachedPiglin;

import java.util.List;

@Debug(export = true)
@Mixin(NearestLivingEntitiesSensor.class)
public abstract class NearestLivingEntitiesSensorMixin<T extends LivingEntity> {
    // Some mods might inject at the head of this as well,
    // mine should be with higher priority to cutoff as much calculation as possible.
    @Inject(method = "sense", at = @At("HEAD"), cancellable = true, order = 900)
    public void head(final ServerWorld world, final T entity, final CallbackInfo ci) {
        // The implementation of warden is moved to WardenAttackablesSensorMixin
        // Wait for completing more
        if (entity instanceof PiglinEntity mob) {
            final var cache = ((CachedPiglin) entity).toobee$getCache();
            if (cache != null && cache.getHasUpdatedThisTick()) {
                cache.senseNearestLivingEntities(world, mob);
                ci.cancel();
            }
        }
    }

    @Inject(method = "sense", at = @At("TAIL"))
    public void tail(final ServerWorld world, final T entity, final CallbackInfo ci, @Local final List<LivingEntity> list) {
        if (entity instanceof CachedMob<?,?> m) {
            final var b = m.toobee$getCache();
            if (b == null)
                m.toobee$getCacheManager().checkToCreate(world, entity.getBlockPos(), list);
            else
                b.setTrackers(list);
        }
    }

    // Omit the error message of this as I don't know how to turn it off.
    @TargetHandler(
            mixin = "com.github.cao.awa.sepals.mixin.entity.ai.brain.sensor.nearest.NearestLivingEntitiesSensorMixin",
            name = "sense"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At("TAIL"), require = 0)
    private void tailForSepals(ServerWorld world, T entity, CallbackInfo ci0, CallbackInfo ci1, @Local List<LivingEntity> list) {
        if (entity instanceof CachedMob<?,?> m) {
            final var b = m.toobee$getCache();
            if (b == null)
                m.toobee$getCacheManager().checkToCreate(world, entity.getBlockPos(), list);
            else
                b.setTrackers(list);
        }
    }
}