package top.toobee.optimization.cache

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.brain.LivingTargetCache
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.mob.WardenEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

class WardenCache private constructor(
    world: ServerWorld,
    pos: BlockPos,
    tracker: MutableList<LivingEntity>,
    var angerAtTarget: Int = 0
): StackingMobCache<WardenEntity>(world, pos, tracker) {
    companion object: Caches<WardenCache>() {
        override fun getOrCreate(world: ServerWorld, pos: BlockPos, list: List<LivingEntity>): WardenCache {
            return this.all.computeIfAbsent(world to pos) {
                WardenCache(world, pos, list.toMutableList())
            }
        }
    }

    override fun truncate() {
        super.truncate()
        all.remove(this.world to this.pos)
    }

    fun newSense(world: ServerWorld, warden: WardenEntity) {
        val brain = warden.brain
        val list = this.tracker
        brain.remember<List<LivingEntity>>(MemoryModuleType.MOBS, list)
        brain.remember(MemoryModuleType.VISIBLE_MOBS, LivingTargetCache(world, warden, list))
        this.nearestTarget?.let {
            brain.remember(MemoryModuleType.NEAREST_ATTACKABLE, it)
        } ?: brain.forget(MemoryModuleType.NEAREST_ATTACKABLE)
    }
}