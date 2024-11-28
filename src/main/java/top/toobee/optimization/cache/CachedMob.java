package top.toobee.optimization.cache;

import net.minecraft.entity.mob.MobEntity;

public interface CachedMob<T extends MobEntity, S extends StackingMobCache<T>> extends BeCached<S> {
    StackingMobCache.Caches<T, S> toobee$getCacheManager();

    default void toobee$updateCache(final T entity) {
        final S newCache;
        if (this.toobee$getCache() == null) {
            newCache = this.toobee$getCacheManager().findCache(entity.getWorld(), entity.getBlockPos());
            if (newCache != null) {
                newCache.getReferencedCounter().incrementAndGet();
                this.toobee$setCache(newCache);
            }
        } else if (!this.toobee$getCache().checkCondition(entity)) {
            newCache = this.toobee$getCacheManager().findCache(entity.getWorld(), entity.getBlockPos());
            if (newCache != this.toobee$getCache()) {
                this.toobee$getCache().getReferencedCounter().decrementAndGet();
                this.toobee$setCache(newCache);
                if (newCache != null)
                    newCache.getReferencedCounter().incrementAndGet();
            }
        }
    }
}