package top.toobee.optimization.intermediary;

import net.minecraft.world.entity.Mob;
import top.toobee.optimization.cache.Caches;
import top.toobee.optimization.cache.StackingMobCache;

public interface CachedMob<T extends Mob, S extends StackingMobCache<T>> extends BeCached<S> {
    Caches<T, S> toobee$getCacheManager();

    default void toobee$updateCache(final T entity) {
        final S newCache;
        if (toobee$getCache() == null) {
            newCache = toobee$getCacheManager().findCache(entity.level(), entity.blockPosition());
            if (newCache != null) {
                newCache.getReferencedCounter().incrementAndGet();
                toobee$setCache(newCache);
            }
        } else if (toobee$getCache().failCondition(entity)) {
            newCache = toobee$getCacheManager().findCache(entity.level(), entity.blockPosition());
            if (newCache != toobee$getCache()) {
                toobee$getCache().getReferencedCounter().decrementAndGet();
                toobee$setCache(newCache);
                if (newCache != null)
                    newCache.getReferencedCounter().incrementAndGet();
            }
        }
    }
}