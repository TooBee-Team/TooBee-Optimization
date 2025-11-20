package top.toobee.optimization.cache;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.level.Level;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class StackingMobCache<T extends Mob> implements AttachedCache<T> {
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicInteger referencedCounter = new AtomicInteger(0);
    private List<LivingEntity> trackers = Collections.emptyList();
    private @Nullable LivingEntity nearestTarget = null;

    protected final Level level;
    public final BlockPos pos;
    public @Nullable Boolean shouldRunMoveToTargetTask = null;
    public Optional<BlockPos> supportingBlockPos = Optional.empty();
    public Optional<LivingEntity> lookAtMobTaskEntity = Optional.empty();

    private long lastUpdateTick = 0;
    private volatile boolean recheckUpdate = false;
    private boolean hasUpdatedThisTick = false;

    public StackingMobCache(Level level, BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    @Override public final Level getLevel() { return level; }
    @Override public final Lock getLock() { return lock; }
    @Override public final AtomicInteger getReferencedCounter() { return referencedCounter; }
    @Override public final long getLastUpdateTick() { return lastUpdateTick; }
    @Override public final boolean getHasUpdatedThisTick() { return hasUpdatedThisTick; }
    public final @Nullable LivingEntity getNearestTarget() { return nearestTarget; }
    
    @Override
    public final void setLastUpdateTick(long l) {
        this.lastUpdateTick = l;
    }

    @Override
    public final void setHasUpdatedThisTick(boolean b) {
        if (b != hasUpdatedThisTick) {
            this.hasUpdatedThisTick = b;
            this.recheckUpdate = b;
        }
    }

    public final void setTrackers(List<LivingEntity> trackers) {
        if (!recheckUpdate && lock.tryLock()) {
            try {
                this.trackers = trackers;
                this.recheckUpdate = true;
            } finally {
                lock.unlock();
            }
        }
    }

    public final void setNearestTarget(@Nullable LivingEntity e) {
        lock.lock();
        try {
            this.nearestTarget = e;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean failCondition(T t) {
        return !(t.blockPosition().equals(pos) && level.equals(t.level()) && t.isAlive());
    }

    @Override
    public void tick() {
        AttachedCache.super.tick();
        this.shouldRunMoveToTargetTask = null;
        this.supportingBlockPos = Optional.empty();
        this.lookAtMobTaskEntity = Optional.empty();
        this.nearestTarget = null;
        this.recheckUpdate = false;
    }

    public final void senseNearestLivingEntities(ServerLevel serverLevel, T t) {
        final var brain = t.getBrain();
        brain.setMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES, trackers);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, new NearestVisibleLivingEntities(serverLevel, t, trackers));
    }

    public abstract void newSense(ServerLevel serverLevel, T entity);
}
