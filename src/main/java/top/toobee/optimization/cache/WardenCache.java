package top.toobee.optimization.cache;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public final class WardenCache extends StackingMobCache<WardenEntity> {
    public static final Caches<WardenEntity, WardenCache> CACHES = new Caches<>(WardenEntity.class, WardenCache::new);

    public int angerAtTarget = 0;

    private WardenCache(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public void truncate() {
        CACHES.all.remove(Pair.of(world, pos));
    }

    @Override
    public void newSense(ServerWorld serverWorld, WardenEntity entity) {
        senseNearestLivingEntities(serverWorld, entity);
        final var it = getNearestTarget();
        if (it == null)
            entity.getBrain().forget(MemoryModuleType.NEAREST_ATTACKABLE);
        else
            entity.getBrain().remember(MemoryModuleType.NEAREST_ATTACKABLE, it);
    }
}
