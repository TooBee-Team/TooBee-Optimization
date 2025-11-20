package top.toobee.optimization.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.WardenEntitySensor;
import net.minecraft.world.entity.monster.warden.Warden;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.toobee.optimization.cache.WardenCache;
import top.toobee.optimization.intermediary.BeCached;
import top.toobee.optimization.intermediary.CachedMob;

@Mixin(WardenEntitySensor.class)
public abstract class WardenEntitySensorMixin {
    @Unique private static final String TARGET_METHOD
            = "doTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/monster/warden/Warden;)V";

    @Inject(method = TARGET_METHOD, at = @At("HEAD"), cancellable = true)
    public void head(final ServerLevel serverLevel, final Warden warden, final CallbackInfo ci) {
        @SuppressWarnings("unchecked")
        final var cache = ((CachedMob<Warden, WardenCache>) warden).toobee$getCache();
        if (cache != null && cache.getHasUpdatedThisTick()) {
            cache.newSense(serverLevel, warden);
            ci.cancel();
        }
    }

    @Inject(method = TARGET_METHOD, at = @At("TAIL"))
    public void tail(final ServerLevel serverLevel, final Warden warden, final CallbackInfo ci) {
        if (((BeCached<?>) warden).toobee$getCache() instanceof WardenCache cache) {
            final var x = warden.getBrain().getMemoryInternal(MemoryModuleType.NEAREST_ATTACKABLE);
            if (!cache.getHasUpdatedThisTick() && x != null && x.isPresent())
                cache.setNearestTarget(x.get());
        }
    }
}
