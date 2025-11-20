package top.toobee.optimization.cache;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

public final class WardenCache extends StackingMobCache<Warden> {
    public static final Caches<Warden, WardenCache> CACHES = new Caches<>(Warden.class, WardenCache::new);

    public int angerAtTarget = 0;

    private WardenCache(Level level, BlockPos pos) {
        super(level, pos);
    }

    @Override
    public void truncate() {
        CACHES.all.remove(Pair.of(level, pos));
    }

    @Override
    public void newSense(ServerLevel serverLevel, Warden entity) {
        senseNearestLivingEntities(serverLevel, entity);
        final var it = getNearestTarget();
        if (it == null)
            entity.getBrain().eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE);
        else
            entity.getBrain().setMemory(MemoryModuleType.NEAREST_ATTACKABLE, it);
    }
}
