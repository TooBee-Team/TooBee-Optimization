package top.toobee.optimization

import net.caffeinemc.mods.lithium.common.entity.pushable.PushableEntityClassGroup
import net.fabricmc.api.DedicatedServerModInitializer
import net.minecraft.entity.mob.WardenEntity
import top.toobee.optimization.mixin.EntityClassGroupAccessor

class Optimization : DedicatedServerModInitializer {
    override fun onInitializeServer() {
        (PushableEntityClassGroup.MAYBE_PUSHABLE as EntityClassGroupAccessor).class2GroupContains.addTo(WardenEntity::class.java, 1)
        (PushableEntityClassGroup.CACHABLE_UNPUSHABILITY as EntityClassGroupAccessor).class2GroupContains.addTo(WardenEntity::class.java, 1)
    }
}
