package top.toobee.optimization.mixin;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.WardenAttackablesSensor;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.toobee.optimization.cache.BeCached;
import top.toobee.optimization.cache.WardenCache;

@Mixin(WardenAttackablesSensor.class)
public abstract class WardenAttackablesSensorMixin {
    @Unique
    private static final String TARGET_METHOD
            = "sense(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/WardenEntity;)V";

    @Inject(method = TARGET_METHOD, cancellable = true, at = @At("HEAD"))
    public void cache1(final ServerWorld serverWorld, final WardenEntity warden, final CallbackInfo ci) {
        @SuppressWarnings("unchecked")
        final var beCached = (BeCached<WardenCache>) warden;
        var cache = beCached.toobee$getCache();

        if (cache != null && cache.checkCondition(warden)) {
            if (cache.getHasUpdatedThisTick()) {
                cache.newSense(serverWorld, warden);
                ci.cancel();
            } else {
                cache.setHasUpdatedThisTick(true);
            }
        } else {
            cache = WardenCache.Companion.findCache(serverWorld, warden.getBlockPos());
            beCached.toobee$updateCache(cache);
            if (cache != null) {
                cache.newSense(serverWorld, warden);
                ci.cancel();
            }
        }
    }

    @Inject(method = TARGET_METHOD, at = @At("TAIL"))
    public void cache2(final ServerWorld serverWorld, final WardenEntity warden, final CallbackInfo ci) {
        if (((BeCached<?>) warden).toobee$getCache() instanceof WardenCache cache) {
            final var x = warden.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE);
            if (x != null && x.isPresent())
                cache.setNearestTarget(x.get());
        }
    }
}
