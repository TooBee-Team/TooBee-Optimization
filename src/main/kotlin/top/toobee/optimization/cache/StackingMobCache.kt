package top.toobee.optimization.cache

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import top.toobee.optimization.track.BeTracked
import top.toobee.optimization.track.TooBeeTrackers
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

abstract class StackingMobCache<T: MobEntity> protected constructor(
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
        set (b) {
            if (b != field) {
                field = b
                this.recheckUpdate.set(b)
            }
        }

    // To synchronize "hasUpdatedThisTick" of CPU cache with main memory
    private val recheckUpdate = AtomicBoolean(false)
    var shouldRunMoveToTargetTask: Boolean? = null
    var supportingBlockPos: Optional<BlockPos> = Optional.empty()

    override fun checkCondition(t: T): Boolean {
        return t.blockPos == this.pos && t.world === this.world
    }

    override fun truncate() {
        TooBeeTrackers.removeTargets(this.world, this.tracker)
    }

    override fun tick() {
        super.tick()
        this.supportingBlockPos = Optional.empty()
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

    open fun updateTracker(new: MutableCollection<LivingEntity>) {
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
                this.recheckUpdate.set(true)
            }
        } finally {
            this.lock.unlock()
        }
    }

    abstract class Caches<S: StackingMobCache<*>> protected constructor(
        protected val all: MutableMap<Pair<ServerWorld?, BlockPos?>, S> = ConcurrentHashMap()
    ) {
        abstract fun getOrCreate(world: ServerWorld, pos: BlockPos, list: List<LivingEntity>): S
        fun findCache(world: ServerWorld?, pos: BlockPos): S? = all[world to pos]
        fun values() = this.all.values
    }
}