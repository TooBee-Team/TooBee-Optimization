package top.toobee.optimization.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.NearestLivingEntitiesSensor;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.toobee.optimization.cache.BeCached;
import top.toobee.optimization.cache.WardenCache;

import java.util.List;

@Mixin(NearestLivingEntitiesSensor.class)
public abstract class NearestLivingEntitiesSensorMixin<T extends LivingEntity> {
    @Inject(method = "sense", at = @At("TAIL"))
    public void tail(final ServerWorld world, final T entity, final CallbackInfo ci, @Local final List<LivingEntity> list) {
        if (entity instanceof WardenEntity warden) {
            @SuppressWarnings("unchecked")
            final var b = (BeCached<WardenCache>) warden;
            if (b.toobee$getCache() instanceof WardenCache cache)
                cache.update(list);
            // Wait for checking: does the following code have concurrent problem?
            else if ((list.stream().filter(w -> w instanceof WardenEntity).count() & 0xFFFFFFF0L) != 0)
                b.toobee$updateCache(WardenCache.Companion.getOrCreate(world, warden.getBlockPos(), list));
        }
    }
}
