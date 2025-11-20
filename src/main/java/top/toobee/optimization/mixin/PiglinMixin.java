package top.toobee.optimization.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.toobee.optimization.cache.Caches;
import top.toobee.optimization.cache.PiglinCache;
import top.toobee.optimization.intermediary.CachedPiglin;

@Mixin(Piglin.class)
public abstract class PiglinMixin extends Mob implements CachedPiglin {
    protected PiglinMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Unique private PiglinCache cache = null;
    @Unique private boolean hasNotBeenHitByPlayer = true;

    @Override
    public void tick() {
        toobee$updateCache((Piglin) (Object) this);
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
    public Caches<Piglin, PiglinCache> toobee$getCacheManager() {
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
