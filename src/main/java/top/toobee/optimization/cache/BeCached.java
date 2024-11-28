package top.toobee.optimization.cache;

// This interface is originally witten in kotlin, but default method of kotlin is not supported by java directly.
public interface BeCached<T extends AttachedCache<?>> {
    default void toobee$removeCache() {
        final var old = this.toobee$getCache();
        if (old != null) {
            old.getReferencedCounter().decrementAndGet();
            toobee$setCache(null);
        }
    }

    void toobee$setCache(T cache);
    T toobee$getCache();
}
