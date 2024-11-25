package top.toobee.optimization.cache

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.brain.LivingTargetCache
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.mob.WardenEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import java.util.concurrent.ConcurrentHashMap

class WardenCache private constructor(
    world: ServerWorld,
    pos: BlockPos,
    tracker: MutableList<LivingEntity>,
    var angerAtTarget: Int = 0
): StackingMobCache<WardenEntity>(world, pos, tracker) {
    companion object {
        private val ALL = ConcurrentHashMap<Pair<ServerWorld?, BlockPos>, WardenCache>()
        fun findCache(world: ServerWorld?, pos: BlockPos): WardenCache? = ALL[world to pos]
        fun getOrCreate(world: ServerWorld, pos: BlockPos, list: List<LivingEntity>): WardenCache {
            return ALL.computeIfAbsent(world to pos) {
                WardenCache(world, pos, list.toMutableList())
            }
        }
        fun values() = ALL.values
    }

    override fun truncate() {
        super.truncate()
        ALL.remove(this.world to this.pos)
    }

    fun newSense(world: ServerWorld, warden: WardenEntity) {
        val brain = warden.brain
        val list = this.tracker
        brain.remember<List<LivingEntity>>(MemoryModuleType.MOBS, list)
        brain.remember(MemoryModuleType.VISIBLE_MOBS, LivingTargetCache(world, warden, list))
        this.nearestTarget?.let {
            brain.remember(MemoryModuleType.NEAREST_ATTACKABLE, this.nearestTarget)
        } ?: brain.forget(MemoryModuleType.NEAREST_ATTACKABLE)
    }
}