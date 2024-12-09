package top.toobee.optimization.cache

import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.mob.WardenEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class WardenCache private constructor(
    world: World,
    pos: BlockPos,
) : StackingMobCache<WardenEntity>(world, pos) {
    companion object : Caches<WardenEntity, WardenCache>(WardenEntity::class.java) {
        override fun create(world: World, pos: BlockPos): WardenCache = WardenCache(world, pos)
    }

    var angerAtTarget: Int = 0

    override fun truncate() {
        super.truncate()
        all.remove(this.world to this.pos)
    }

    override fun newSense(world: ServerWorld, entity: WardenEntity) {
        this.senseNearestLivingEntities(world, entity)
        this.nearestTarget?.let {
            entity.brain.remember(MemoryModuleType.NEAREST_ATTACKABLE, it)
        } ?: entity.brain.forget(MemoryModuleType.NEAREST_ATTACKABLE)
    }
}