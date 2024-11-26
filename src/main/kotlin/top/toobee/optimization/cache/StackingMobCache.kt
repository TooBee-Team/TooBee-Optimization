package top.toobee.optimization.cache

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

const val MIN_CACHE_SIZE_MASKED = Int.MAX_VALUE - 0b1111

abstract class StackingMobCache<T: MobEntity> protected constructor(
    final override val world: ServerWorld,
    val pos: BlockPos,
): AttachedCache<T> {
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
    var trackers: List<LivingEntity> = emptyList()
        set (value) {
            try {
                if (!this.recheckUpdate.get() && this.lock.tryLock()) {
                    field = value
                    this.recheckUpdate.set(true)
                }
            } finally {
                this.lock.unlock()
            }
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

    override fun checkCondition(t: T): Boolean {
        return t.blockPos == this.pos && t.world === this.world
    }

    override fun tick() {
        super.tick()
        this.shouldRunMoveToTargetTask = null
        this.supportingBlockPos = Optional.empty()
        this.recheckUpdate.set(false)
    }

    abstract class Caches<T: MobEntity, S: StackingMobCache<T>> protected constructor(
        private val cls: Class<T>,
        protected val all: MutableMap<Pair<ServerWorld?, BlockPos?>, S> = ConcurrentHashMap()
    ) {
        fun findCache(world: ServerWorld?, pos: BlockPos): S? = all[world to pos]

        fun values() = this.all.values

        fun checkToCreate(target: CachedMob<T, S>, world: ServerWorld, pos: BlockPos, list: List<*>) {
            val count = list.filterIsInstance(cls).count { e -> e.blockPos == pos && e.world === world }
            if (count and MIN_CACHE_SIZE_MASKED != 0)
                target.`toobee$updateCache`(all.computeIfAbsent(world to pos) { this.create(world, pos) })
        }

        protected abstract fun create(world: ServerWorld, pos: BlockPos): S
    }
}