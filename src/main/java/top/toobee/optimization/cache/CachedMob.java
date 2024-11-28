package top.toobee.optimization.cache;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface CachedMob<T extends MobEntity, S extends StackingMobCache<T>> extends BeCached<S> {
    StackingMobCache.Caches<T, S> toobee$getCacheManager();

    default void toobee$updateCache(final T entity) {
        final S newCache;
        if (this.toobee$getCache() == null) {
            newCache = this.toobee$getCacheManager().findCache((ServerWorld) entity.getWorld(), entity.getBlockPos());
            if (newCache != null) {
                newCache.getReferencedCounter().incrementAndGet();
                this.toobee$setCache(newCache);
            }
        } else if (!this.toobee$getCache().checkCondition(entity)) {
            newCache = this.toobee$getCacheManager().findCache((ServerWorld) entity.getWorld(), entity.getBlockPos());
            if (newCache != this.toobee$getCache()) {
                this.toobee$getCache().getReferencedCounter().decrementAndGet();
                this.toobee$setCache(newCache);
                if (newCache != null)
                    newCache.getReferencedCounter().incrementAndGet();
            }
        }
    }

    default void toobee$checkToCreateCache(List<?> list, ServerWorld world, BlockPos pos) {
        this.toobee$getCacheManager().checkToCreate(world, pos, list);
    }
}