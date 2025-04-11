package top.toobee.optimization.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.toobee.optimization.cache.Caches;
import top.toobee.optimization.cache.PiglinCache;
import top.toobee.optimization.intermediary.CachedPiglin;

@Mixin(PiglinEntity.class)
public abstract class PiglinMixin extends MobEntity implements CachedPiglin {
    protected PiglinMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique private PiglinCache cache = null;
    @Unique private boolean hasNotBeenHitByPlayer = true;

    @Override
    public void tick() {
        toobee$updateCache((PiglinEntity) (Object) this);
        super.tick();
        if (cache != null)
            cache.setHasUpdatedThisTick(true);
    }

    @Override
    public PiglinCache toobee$getCache() {
        return cache;
    }

    @Override
    public void toobee$setCache(PiglinCache cache) {
        this.cache = cache;
    }

    @Override
    public Caches<PiglinEntity, PiglinCache> toobee$getCacheManager() {
        return PiglinCache.CACHES;
    }

    @Override
    public boolean toobee$hasNotBeenHitByPlayer() {
        return hasNotBeenHitByPlayer;
    }

    @Override
    public void toobee$setHasNotBeenHitByPlayer(boolean hasNotBeenHitByPlayer) {
        this.hasNotBeenHitByPlayer = hasNotBeenHitByPlayer;
    }
}
