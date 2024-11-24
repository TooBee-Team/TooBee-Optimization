package top.toobee.optimization.cache

interface BeCached {
    fun `toobee$addCache`(cache: AttachedCache<*>?)
    fun `toobee$getCache`(): AttachedCache<*>?
    fun `toobee$removeCache`()
}
