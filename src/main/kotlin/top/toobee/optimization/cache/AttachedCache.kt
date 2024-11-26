package top.toobee.optimization.cache

import net.minecraft.server.world.ServerWorld
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.Lock

interface AttachedCache<T> {
    val world: ServerWorld
    val referencedCounter: AtomicInteger
    val lock: Lock

    var lastUpdateTick: Long
    var hasUpdatedThisTick: Boolean

    fun checkCondition(t: T): Boolean
    fun truncate() {}

    /**
     * Run every end of tick to reset some variables
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