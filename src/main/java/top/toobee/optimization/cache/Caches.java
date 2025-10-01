package top.toobee.optimization.cache;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public final class Caches<T extends MobEntity, S extends StackingMobCache<T>> {
    private final Class<T> cls;
    private final BiFunction<World, BlockPos, S> factory;
    public final Map<Pair<@Nullable World, @Nullable BlockPos>, S> all = new ConcurrentHashMap<>();

    public Caches(Class<T> cls, BiFunction<World, BlockPos, S> factory) {
        this.cls = cls;
        this.factory = factory;
    }

    public S findCache(@Nullable World world, @Nullable BlockPos pos) {
        return all.get(Pair.of(world, pos));
    }

    public void tick() {
        all.values().forEach(StackingMobCache::tick);
    }

    public void checkToCreate(World world, BlockPos pos, Iterable<?> list) {
        int i = 0;
        for (var c : list)
            if (cls.isInstance(c)) {
                @SuppressWarnings("unchecked")
                final var t = (T) c;
                if (t.getBlockPos().equals(pos) && t.getEntityWorld() == world && ++i > 16) {
                    all.computeIfAbsent(Pair.of(world, pos), p -> create(p.getLeft(), p.getRight()));
                    return;
                }
            }
    }

    public S create(World world, BlockPos pos) {
        return factory.apply(world, pos);
    }
}
