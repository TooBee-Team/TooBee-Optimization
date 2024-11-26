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
): StackingMobCache<WardenEntity>(world, pos) {
    companion object: Caches<WardenEntity, WardenCache>(WardenEntity::class.java) {
        override fun create(world: ServerWorld, pos: BlockPos): WardenCache = WardenCache(world, pos)
    }

    var angerAtTarget: Int = 0

    override fun truncate() {
        super.truncate()
        all.remove(this.world to this.pos)
    }

    fun newSense(world: ServerWorld, warden: WardenEntity) {
        val brain = warden.brain
        val list = this.trackers
        brain.remember<List<LivingEntity>>(MemoryModuleType.MOBS, list)
        brain.remember(MemoryModuleType.VISIBLE_MOBS, LivingTargetCache(world, warden, list))
        this.nearestTarget?.let {
            brain.remember(MemoryModuleType.NEAREST_ATTACKABLE, it)
        } ?: brain.forget(MemoryModuleType.NEAREST_ATTACKABLE)
    }
}