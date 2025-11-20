package top.toobee.optimization.cache;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import net.minecraft.world.level.Level;

public interface AttachedCache<T> {
    /**
     * For identification. Originally it is ServerWorld, but it's always inconvenient.
     */
    Level getLevel();

    /**
     * Used to check whether to drop this cache.
     * @see #tick()
     */
    AtomicInteger getReferencedCounter();

    /**
     * Ensure the thread safety, not so useful though.
     */
    Lock getLock();

    /**
     * It always not used directly by external class, just for a compulsive requirement to implement.
     * @see #getLastUpdateTick()
     */
    long getLastUpdateTick();
    void setLastUpdateTick(long l);

    /**
     * Target judges whether using or updating cache depending on this
     */
    boolean getHasUpdatedThisTick();
    void setHasUpdatedThisTick(boolean b);

    /**
     * Check whether the target satisfy certain condition.
     */
    boolean failCondition(T t);

    /**
     * When the cache is dropped, do some clear tasks.
     */
    void truncate();

    /**
     * Run every end of tick to reset some variables.
     */
    default void tick() {
        final var time = getLevel().getGameTime();
        if (getLastUpdateTick() != time) {
            if (getHasUpdatedThisTick()) {
                setHasUpdatedThisTick(false);
                setLastUpdateTick(time);
            } else if ((time - getLastUpdateTick()) >> 8 != 0L) {
                truncate();
            }
            if (getReferencedCounter().get() >> 4 == 0)
                truncate();
        }
    }
}
