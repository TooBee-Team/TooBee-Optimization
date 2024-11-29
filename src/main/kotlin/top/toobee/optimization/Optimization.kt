package top.toobee.optimization

import net.caffeinemc.mods.lithium.common.entity.pushable.PushableEntityClassGroup
import net.fabricmc.api.DedicatedServerModInitializer
import net.minecraft.entity.mob.WardenEntity
import top.toobee.optimization.cache.PiglinCache
import top.toobee.optimization.cache.WardenCache
import top.toobee.optimization.accessor.EntityClassGroupAccessor

class Optimization : DedicatedServerModInitializer {
    companion object {
        fun endServerTick() {
            WardenCache.tick()
            PiglinCache.tick()
        }
    }

    override fun onInitializeServer() {
        (PushableEntityClassGroup.MAYBE_PUSHABLE as EntityClassGroupAccessor).class2GroupContains.addTo(WardenEntity::class.java, 1)
        (PushableEntityClassGroup.CACHABLE_UNPUSHABILITY as EntityClassGroupAccessor).class2GroupContains.addTo(WardenEntity::class.java, 1)
    }
}
