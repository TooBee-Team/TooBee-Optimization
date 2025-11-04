package top.toobee.optimization.intermediary;

import net.minecraft.world.entity.monster.piglin.Piglin;
import top.toobee.optimization.cache.PiglinCache;

public interface CachedPiglin extends CachedMob<Piglin, PiglinCache> {
    boolean toobee$hasNotBeenHitByPlayer();
    void toobee$setHasNotBeenHitByPlayer(boolean b);
}
