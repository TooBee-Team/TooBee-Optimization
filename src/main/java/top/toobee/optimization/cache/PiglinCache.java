package top.toobee.optimization.cache;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import top.toobee.optimization.accessor.MobEntityAccessor;
import top.toobee.optimization.intermediary.DataTrackerIntermediary;
import top.toobee.optimization.util.ListExt;

import java.util.List;
import java.util.Optional;

public final class PiglinCache extends StackingMobCache<PiglinEntity> {
    public static final Caches<PiglinEntity, PiglinCache> CACHES = new Caches<>(PiglinEntity.class, PiglinCache::new);
    public static final int MOB_FLAGS_ID = MobEntityAccessor.getMobFlags().id();

    private record MemoryCache(
            Optional<BlockPos> NEAREST_REPELLENT,
            Optional<MobEntity> NEAREST_VISIBLE_NEMESIS,
            Optional<HoglinEntity> NEAREST_VISIBLE_HUNTABLE_HOGLIN,
            Optional<HoglinEntity> NEAREST_VISIBLE_BABY_HOGLIN,
            Optional<LivingEntity> NEAREST_VISIBLE_ZOMBIFIED,
            Optional<PlayerEntity> NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD,
            Optional<PlayerEntity> NEAREST_PLAYER_HOLDING_WANTED_ITEM,
            Optional<List<AbstractPiglinEntity>> NEARBY_ADULT_PIGLINS,
            Optional<List<AbstractPiglinEntity>> NEAREST_VISIBLE_ADULT_PIGLINS,
            Optional<Integer> VISIBLE_ADULT_PIGLIN_COUNT,
            Optional<Integer> VISIBLE_ADULT_HOGLIN_COUNT
    ) {
        static final MemoryCache DEFAULT = new MemoryCache(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }

    private MemoryCache memoryCache = MemoryCache.DEFAULT;

    public PiglinCache(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public void newSense(ServerWorld serverWorld, PiglinEntity entity) {
        final var brain = entity.getBrain();
        final var c = this.memoryCache;
        brain.remember(MemoryModuleType.NEAREST_REPELLENT, c.NEAREST_REPELLENT);
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, c.NEAREST_VISIBLE_NEMESIS);
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, c.NEAREST_VISIBLE_HUNTABLE_HOGLIN);
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, c.NEAREST_VISIBLE_BABY_HOGLIN);
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, c.NEAREST_VISIBLE_ZOMBIFIED);
        brain.remember(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, c.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
        brain.remember(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, c.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
        brain.remember(MemoryModuleType.NEARBY_ADULT_PIGLINS, c.NEARBY_ADULT_PIGLINS);
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, c.NEAREST_VISIBLE_ADULT_PIGLINS);
        brain.remember(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, c.VISIBLE_ADULT_PIGLIN_COUNT);
        brain.remember(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, c.VISIBLE_ADULT_HOGLIN_COUNT);

    }

    public void reset(Brain<PiglinEntity> brain) {
        this.memoryCache = new MemoryCache(
                brain.getOptionalMemory(MemoryModuleType.NEAREST_REPELLENT),
                brain.getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS),
                brain.getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN),
                brain.getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN),
                brain.getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED),
                brain.getOptionalMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD),
                brain.getOptionalMemory(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM),
                brain.getOptionalMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS),
                brain.getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS),
                brain.getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT),
                brain.getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT)
        );
    }

    @Override
    public void truncate() {
        CACHES.all.remove(Pair.of(world, pos));
    }

    @Override
    public boolean failCondition(PiglinEntity t) {
        return super.failCondition(t) || t.isBaby();
    }

    private boolean changeAttacking = false;
    private @Nullable List<ItemEntity> nearestItems = null;
    public List<ItemEntity> canPickUpItems = List.of();

    public @Nullable List<ItemEntity> getNearestItems() {
        return nearestItems;
    }

    public void setNearestItems(@Nullable List<ItemEntity> nearestItems) {
        if (nearestItems == null)
            this.nearestItems = null;
        else
            this.nearestItems = ListExt.prioritize(nearestItems,
                    it -> it.getEntityPos().isInRange(pos.toCenterPos(), 32.0),
                    it -> it.getStack().isOf(PiglinBrain.BARTERING_ITEM) && it.getBlockPos().equals(pos));
    }

    public void redirectAttacking(PiglinEntity instance, boolean attacking) {
        if (getHasUpdatedThisTick()) {
            // Accelerate setAttacking by implement specially
            if (changeAttacking) {
                final var t = (DataTrackerIntermediary) instance.getDataTracker();
                final var b = t.toobee$getMobFlags();
                t.toobee$setMobFlags((byte) (attacking ? b | 4 : b & -5));
            }
        } else {
            this.changeAttacking = instance.isAttacking() != attacking;
            instance.setAttacking(attacking);
        }
    }

    public Optional<ItemEntity> getNearestItem(ServerWorld serverWorld, PiglinEntity piglin) {
        final var items = this.nearestItems;
        if (items == null)
            return Optional.empty();
        for (var item : items)
            if (piglin.canGather(serverWorld, item.getStack()) && piglin.canSee(item))
                return Optional.of(item);
        return Optional.empty();
    }

    @Override
    public void tick() {
        super.tick();
        this.nearestItems = null;
    }
}
