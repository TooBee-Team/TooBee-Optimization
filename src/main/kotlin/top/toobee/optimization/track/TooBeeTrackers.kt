package top.toobee.optimization.track

import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMaps
import net.minecraft.world.World
import top.toobee.optimization.Optimization
import java.util.concurrent.CopyOnWriteArrayList

typealias Trackers = MutableList<MutableCollection<*>>

object TooBeeTrackers {
    private val trackers: Map<World, Trackers> = Reference2ReferenceMaps.unmodifiable(init())

    private fun init(): Reference2ReferenceMap<World, Trackers> {
        val m = Reference2ReferenceArrayMap<World, Trackers>()
        Optimization.server!!.worlds!!.forEach {
            m[it] = CopyOnWriteArrayList()
        }
        return m
    }

    fun removeTarget(world: World, target: BeTracked) {
        this.trackers[world]?.forEach { it.remove(target) }
        target.`toobee$resetTrackedAmount`()
    }

    fun removeTargets(world: World, targets: MutableCollection<*>) {
        this.trackers[world]?.remove(targets)
        targets.forEach { (it as? BeTracked)?.`toobee$decreaseTrackedAmount`() }
    }

    fun addTargets(world: World, targets: MutableCollection<*>) {
        this.trackers[world]?.add(targets)
        targets.forEach { (it as? BeTracked)?.`toobee$increaseTrackedAmount`() }
    }

}