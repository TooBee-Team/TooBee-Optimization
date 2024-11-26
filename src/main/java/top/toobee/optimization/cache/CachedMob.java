package top.toobee.optimization.cache;

import net.minecraft.entity.mob.MobEntity;

public interface CachedMob<T extends MobEntity, S extends StackingMobCache<T>> extends BeCached<S> {}
