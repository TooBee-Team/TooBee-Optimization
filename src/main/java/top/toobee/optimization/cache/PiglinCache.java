package top.toobee.optimization.cache;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import top.toobee.optimization.accessor.MobAccessor;
import top.toobee.optimization.intermediary.MobFlagsTouch;
import top.toobee.optimization.util.ListExt;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class PiglinCache extends StackingMobCache<Piglin> {
    public static final Caches<Piglin, PiglinCache> CACHES = new Caches<>(Piglin.class, PiglinCache::new);
    public static final int MOB_FLAGS_ID = MobAccessor.getMobFlags().id();

    private record MemoryCache(
            Optional<BlockPos> NEAREST_REPELLENT,
            Optional<Mob> NEAREST_VISIBLE_NEMESIS,
            Optional<Hoglin> NEAREST_VISIBLE_HUNTABLE_HOGLIN,
            Optional<Hoglin> NEAREST_VISIBLE_BABY_HOGLIN,
            Optional<LivingEntity> NEAREST_VISIBLE_ZOMBIFIED,
            Optional<Player> NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD,
            Optional<Player> NEAREST_PLAYER_HOLDING_WANTED_ITEM,
            Optional<List<AbstractPiglin>> NEARBY_ADULT_PIGLINS,
            Optional<List<AbstractPiglin>> NEAREST_VISIBLE_ADULT_PIGLINS,
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

    public PiglinCache(Level level, BlockPos pos) {
        super(level, pos);
    }

    @Override
    public void newSense(ServerLevel serverLevel, Piglin entity) {
        final var brain = entity.getBrain();
        final var c = this.memoryCache;
        brain.setMemory(MemoryModuleType.NEAREST_REPELLENT, c.NEAREST_REPELLENT);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, c.NEAREST_VISIBLE_NEMESIS);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, c.NEAREST_VISIBLE_HUNTABLE_HOGLIN);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, c.NEAREST_VISIBLE_BABY_HOGLIN);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, c.NEAREST_VISIBLE_ZOMBIFIED);
        brain.setMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, c.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
        brain.setMemory(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, c.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
        brain.setMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS, c.NEARBY_ADULT_PIGLINS);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, c.NEAREST_VISIBLE_ADULT_PIGLINS);
        brain.setMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, c.VISIBLE_ADULT_PIGLIN_COUNT);
        brain.setMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, c.VISIBLE_ADULT_HOGLIN_COUNT);

    }

    public void reset(Brain<Piglin> brain) {
        this.memoryCache = new MemoryCache(
                brain.getMemoryInternal(MemoryModuleType.NEAREST_REPELLENT),
                brain.getMemoryInternal(MemoryModuleType.NEAREST_VISIBLE_NEMESIS),
                brain.getMemoryInternal(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN),
                brain.getMemoryInternal(MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN),
                brain.getMemoryInternal(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED),
                brain.getMemoryInternal(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD),
                brain.getMemoryInternal(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM),
                brain.getMemoryInternal(MemoryModuleType.NEARBY_ADULT_PIGLINS),
                brain.getMemoryInternal(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS),
                brain.getMemoryInternal(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT),
                brain.getMemoryInternal(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT)
        );
    }

    @Override
    public void truncate() {
        CACHES.all.remove(Pair.of(level, pos));
    }

    @Override
    public boolean failCondition(Piglin t) {
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
                    it -> it.position().closerThan(pos.getCenter(), 32.0),
                    it -> it.getItem().is(PiglinAi.BARTERING_ITEM) && it.blockPosition().equals(pos));
    }

    public void redirectAttacking(Piglin instance, boolean attacking) {
        if (getHasUpdatedThisTick()) {
            // Accelerate setAttacking by implement specially
            if (changeAttacking) {
                final var t = (MobFlagsTouch) instance.getEntityData();
                final var b = t.toobee$getMobFlags();
                t.toobee$setMobFlags((byte) (attacking ? b | 4 : b & -5));
            }
        } else {
            this.changeAttacking = instance.isAggressive() != attacking;
            instance.setAggressive(attacking);
        }
    }

    public Optional<ItemEntity> getNearestItem(ServerLevel serverLevel, Piglin piglin) {
        final var items = this.nearestItems;
        if (items == null)
            return Optional.empty();
        for (var item : items)
            if (piglin.wantsToPickUp(serverLevel, item.getItem()) && piglin.hasLineOfSight(item))
                return Optional.of(item);
        return Optional.empty();
    }

    @Override
    public void tick() {
        super.tick();
        this.nearestItems = null;
    }
}
