package top.toobee.optimization.intermediary;

import net.minecraft.entity.mob.PiglinEntity;

/**
 * IDEA seems not to identify the use from kotlin
 * @see top.toobee.optimization.cache.PiglinCache#redirectAttacking(PiglinEntity, boolean)
 */
@SuppressWarnings("unused")
public interface DataTrackerIntermediary {
    byte toobee$getMobFlags();
    void toobee$setMobFlags(byte value);
}
