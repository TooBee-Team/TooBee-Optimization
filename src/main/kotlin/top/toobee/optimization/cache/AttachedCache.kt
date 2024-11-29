package top.toobee.optimization.cache

import net.minecraft.world.World
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.Lock

interface AttachedCache<T> {
    /**
     * For identification. Originally it is ServerWorld, but it's always inconvenient.
     */
    val world: World

    /**
     * Used to check whether to drop this cache.
     * @see tick()
     */
    val referencedCounter: AtomicInteger

    /**
     * Ensure the thread safety, not so useful though.
     */
    val lock: Lock

    /**
     * It always not used directly by external class, just for a compulsive requirement to implement.
     * @see lastUpdateTick
     */
    var lastUpdateTick: Long

    /**
     * Target judges whether using or updating cache depending on this
     */
    var hasUpdatedThisTick: Boolean

    /**
     * Check whether the target satisfy certain condition.
     */
    fun checkCondition(t: T): Boolean

    /**
     * When the cache is dropped, do some clear tasks.
     */
    fun truncate() {}

    /**
     * Run every end of tick to reset some variables.
     */
    fun tick() {
        if (this.lastUpdateTick != this.world.time) {
            this.lastUpdateTick = this.world.time
            if (this.referencedCounter.get() == 0)
                this.truncate()
            hasUpdatedThisTick = false
        }
    }
}