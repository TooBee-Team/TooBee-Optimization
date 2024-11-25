package top.toobee.optimization

import net.caffeinemc.mods.lithium.common.entity.pushable.PushableEntityClassGroup
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.entity.mob.WardenEntity
import net.minecraft.server.MinecraftServer
import top.toobee.optimization.cache.WardenCache
import top.toobee.optimization.mixin.EntityClassGroupAccessor

class Optimization : DedicatedServerModInitializer {
    companion object {
        var server: MinecraftServer? = null; private set
    }

    override fun onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register { server = it }
        (PushableEntityClassGroup.MAYBE_PUSHABLE as EntityClassGroupAccessor).class2GroupContains.addTo(WardenEntity::class.java, 1)
        (PushableEntityClassGroup.CACHABLE_UNPUSHABILITY as EntityClassGroupAccessor).class2GroupContains.addTo(WardenEntity::class.java, 1)

        ServerTickEvents.START_SERVER_TICK.register {
            WardenCache.values().forEach { it.tick() }
        }
    }
}
