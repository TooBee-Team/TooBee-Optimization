package top.toobee.optimization.cache;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class StackingMobCache<T extends MobEntity> implements AttachedCache<T> {
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicInteger referencedCounter = new AtomicInteger(0);
    private List<LivingEntity> trackers = Collections.emptyList();
    private @Nullable LivingEntity nearestTarget = null;

    protected final World world;
    public final BlockPos pos;
    public @Nullable Boolean shouldRunMoveToTargetTask = null;
    public Optional<BlockPos> supportingBlockPos = Optional.empty();
    public Optional<LivingEntity> lookAtMobTaskEntity = Optional.empty();

    private long lastUpdateTick = 0;
    private volatile boolean recheckUpdate = false;
    private boolean hasUpdatedThisTick = false;

    public StackingMobCache(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    @Override public final World getWorld() { return world; }
    @Override public final Lock getLock() { return lock; }
    @Override public final AtomicInteger getReferencedCounter() { return referencedCounter; }
    @Override public final long getLastUpdateTick() { return lastUpdateTick; }
    @Override public final boolean getHasUpdatedThisTick() { return hasUpdatedThisTick; }
    public final @Nullable LivingEntity getNearestTarget() { return nearestTarget; }
    
    @Override
    public final void setLastUpdateTick(long l) {
        this.lastUpdateTick = l;
    }

    @Override public final void setHasUpdatedThisTick(boolean b) {
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
        return !(t.getBlockPos().equals(pos) && t.getWorld() == world && t.isAlive());
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

    public final void senseNearestLivingEntities(ServerWorld serverWorld, T t) {
        final var brain = t.getBrain();
        brain.remember(MemoryModuleType.MOBS, trackers);
        brain.remember(MemoryModuleType.VISIBLE_MOBS, new LivingTargetCache(serverWorld, t, trackers));
    }

    public abstract void newSense(ServerWorld serverWorld, T entity);
}
