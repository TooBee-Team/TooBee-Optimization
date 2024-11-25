package top.toobee.optimization.track

@Suppress("FunctionName")
interface BeTracked {
    fun `toobee$increaseTrackedAmount`()
    fun `toobee$decreaseTrackedAmount`()
    fun `toobee$resetTrackedAmount`()
}