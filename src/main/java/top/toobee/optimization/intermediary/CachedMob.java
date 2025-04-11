package top.toobee.optimization.intermediary;

import net.minecraft.entity.mob.MobEntity;
import top.toobee.optimization.cache.Caches;
import top.toobee.optimization.cache.StackingMobCache;

public interface CachedMob<T extends MobEntity, S extends StackingMobCache<T>> extends BeCached<S> {
    Caches<T, S> toobee$getCacheManager();

    default void toobee$updateCache(final T entity) {
        final S newCache;
        if (toobee$getCache() == null) {
            newCache = toobee$getCacheManager().findCache(entity.getWorld(), entity.getBlockPos());
            if (newCache != null) {
                newCache.getReferencedCounter().incrementAndGet();
                toobee$setCache(newCache);
            }
        } else if (toobee$getCache().failCondition(entity)) {
            newCache = toobee$getCacheManager().findCache(entity.getWorld(), entity.getBlockPos());
            if (newCache != toobee$getCache()) {
                toobee$getCache().getReferencedCounter().decrementAndGet();
                toobee$setCache(newCache);
                if (newCache != null)
                    newCache.getReferencedCounter().incrementAndGet();
            }
        }
    }
}