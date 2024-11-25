package top.toobee.optimization.cache

import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import top.toobee.optimization.track.BeTracked
import top.toobee.optimization.track.TooBeeTrackers
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

abstract class StackingMobCache<T: LivingEntity>(
    final override val world: ServerWorld,
    val pos: BlockPos,
    val tracker: MutableList<LivingEntity>
): AttachedCache<T> {
    init {
        TooBeeTrackers.addTargets(this.world, this.tracker)
    }

    override val referencedCounter = AtomicInteger(0)
    override val lock = ReentrantLock()
    override var lastUpdateTick = this.world.time
    override var hasUpdatedThisTick = false

    // To synchronize "hasUpdatedThisTick" of CPU cache with main memory
    private val recheckUpdate = AtomicBoolean(false)
    private var shouldRunMoveToTargetTask: Boolean? = null

    override fun checkCondition(t: T): Boolean {
        return t.blockPos == this.pos && t.world === this.world
    }

    override fun truncate() {
        TooBeeTrackers.removeTargets(this.world, this.tracker)
    }

    override fun tick() {
        super.tick()
        this.recheckUpdate.set(false)
    }

    var nearestTarget: LivingEntity? = null
        set(value) {
            lock.lock()
            try {
                field = value
            } finally {
                lock.unlock()
            }
        }

    fun removeNearestTarget(entity: LivingEntity) {
        lock.lock()
        try {
            if (this.nearestTarget == entity)
                this.nearestTarget = null
        } finally {
            lock.unlock()
        }
    }

    fun update(new: MutableCollection<LivingEntity>) {
        try {
            if (!this.recheckUpdate.get() && this.lock.tryLock()) {
                if (this.tracker == new) return
                val o = this.tracker.toSet()
                val n = new.toSet()
                for (i in o - n) (i as? BeTracked)?.`toobee$decreaseTrackedAmount`()
                for (i in n - o) (i as? BeTracked)?.`toobee$increaseTrackedAmount`()
                this.tracker.clear()
                this.tracker.addAll(new)

                this.shouldRunMoveToTargetTask = null
                this.hasUpdatedThisTick = true
                this.recheckUpdate.set(true)
            }
        } finally {
            this.lock.unlock()
        }
    }
}