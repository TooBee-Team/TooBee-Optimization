package top.toobee.optimization.cache;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public final class Caches<T extends Mob, S extends StackingMobCache<T>> {
    private final Class<T> cls;
    private final BiFunction<Level, BlockPos, S> factory;
    public final Map<Pair<@Nullable Level, @Nullable BlockPos>, S> all = new ConcurrentHashMap<>();

    public Caches(Class<T> cls, BiFunction<Level, BlockPos, S> factory) {
        this.cls = cls;
        this.factory = factory;
    }

    public S findCache(@Nullable Level level, @Nullable BlockPos pos) {
        return all.get(Pair.of(level, pos));
    }

    public void tick() {
        all.values().forEach(StackingMobCache::tick);
    }

    public void checkToCreate(Level level, BlockPos pos, Iterable<?> list) {
        int i = 0;
        for (var c : list)
            if (cls.isInstance(c)) {
                @SuppressWarnings("unchecked")
                final var t = (T) c;
                if (t.blockPosition().equals(pos) && level.equals(t.level()) && ++i > 16) {
                    all.computeIfAbsent(Pair.of(level, pos), p -> create(p.getLeft(), p.getRight()));
                    return;
                }
            }
    }

    public S create(Level level, BlockPos pos) {
        return factory.apply(level, pos);
    }
}
