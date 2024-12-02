package top.toobee.optimization.intermediary;

import net.minecraft.entity.mob.PiglinEntity;
import top.toobee.optimization.cache.PiglinCache;

public interface CachedPiglin extends CachedMob<PiglinEntity, PiglinCache> {
    boolean toobee$hasNotBeenHitByPlayer();
    void toobee$setHasNotBeenHitByPlayer(boolean b);
}
