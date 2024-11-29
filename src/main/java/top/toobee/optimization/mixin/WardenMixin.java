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
import top.toobee.optimization.cache.StackingMobCache;
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
    public StackingMobCache.Caches<WardenEntity, WardenCache> toobee$getCacheManager() {
        return WardenCache.Companion;
    }

    /**
     * @author Fungus
     * @reason Use the cache value of anger
     */
    @Overwrite
    private int getAngerAtTarget() {
        final int i;
        if (this.cache != null) {
            if (this.cache.getHasUpdatedThisTick()) {
                return this.cache.getAngerAtTarget();
            } else {
                i = this.angerManager.getAngerFor(this.getTarget());
                this.cache.setAngerAtTarget(i);
            }
        } else {
            i = this.angerManager.getAngerFor(this.getTarget());
        }
        return i;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickHead(CallbackInfo ci) {
        this.toobee$updateCache((WardenEntity) (Object) this);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tickTail(CallbackInfo ci) {
        if (this.cache != null)
            this.cache.setHasUpdatedThisTick(true);
    }
}
