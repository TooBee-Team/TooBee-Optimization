package top.toobee.optimization.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.WardenAngerManager;
import net.minecraft.entity.mob.WardenEntity;
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

@Mixin(WardenEntity.class)
public abstract class WardenMixin implements CachedMob<WardenEntity, WardenCache> {
    @Unique
    private WardenCache cache = null;

    @Shadow
    WardenAngerManager angerManager;

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
    public Caches<WardenEntity, WardenCache> toobee$getCacheManager() {
        return WardenCache.CACHES;
    }

    /**
     * @author Fungus
     * @reason Use the cache value of anger
     */
    @Overwrite
    private int getAngerAtTarget() {
        final int i;
        if (cache != null) {
            if (cache.getHasUpdatedThisTick()) {
                return cache.angerAtTarget;
            } else {
                i = angerManager.getAngerFor(getTarget());
                cache.angerAtTarget = i;
            }
        } else {
            i = angerManager.getAngerFor(getTarget());
        }
        return i;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickHead(CallbackInfo ci) {
        toobee$updateCache((WardenEntity) (Object) this);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tickTail(CallbackInfo ci) {
        if (cache != null)
            cache.setHasUpdatedThisTick(true);
    }
}
