package top.toobee.optimization.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.warden.AngerManagement;
import net.minecraft.world.entity.monster.warden.Warden;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.toobee.optimization.cache.Caches;
import top.toobee.optimization.cache.WardenCache;
import top.toobee.optimization.intermediary.CachedMob;

@Mixin(Warden.class)
public abstract class WardenMixin implements CachedMob<Warden, WardenCache> {
    @Unique
    private WardenCache cache = null;

    @Shadow
    AngerManagement angerManagement;

    @Shadow
    @Nullable
    public abstract LivingEntity getTarget();

    @Override
    public WardenCache toobee$getCache() {
        return this.cache;
    }

    @Override
    public void toobee$setCache(WardenCache cache) {
        this.cache = cache;
    }

    @Override
    public Caches<Warden, WardenCache> toobee$getCacheManager() {
        return WardenCache.CACHES;
    }

    /**
     * @author Fungus
     * @reason Use the cache value of anger
     */
    @Overwrite
    private int getActiveAnger() {
        final int i;
        if (cache != null) {
            if (cache.getHasUpdatedThisTick()) {
                return cache.angerAtTarget;
            } else {
                i = angerManagement.getActiveAnger(getTarget());
                cache.angerAtTarget = i;
            }
        } else {
            i = angerManagement.getActiveAnger(getTarget());
        }
        return i;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickHead(CallbackInfo ci) {
        toobee$updateCache((Warden) (Object) this);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tickTail(CallbackInfo ci) {
        if (cache != null)
            cache.setHasUpdatedThisTick(true);
    }
}
