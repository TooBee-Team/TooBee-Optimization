package top.toobee.optimization.cache;

// This interface is originally witten in kotlin, but default method of kotlin is not supported by java directly.
public interface BeCached<T extends AttachedCache<?>> {
    default void toobee$updateCache(final T cache) {
        final var old = this.toobee$getCache();
        if (cache != old) {
            if (old != null)
                old.getReferencedCounter().decrementAndGet();
            if (cache != null)
                cache.getReferencedCounter().incrementAndGet();
            toobee$setCache(cache);
        }
    }

    void toobee$setCache(final T cache);
    T toobee$getCache();
}
