package top.toobee.optimization.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.toobee.optimization.accessor.PiglinBrainInvoker;
import top.toobee.optimization.cache.PiglinCache;
import top.toobee.optimization.intermediary.CachedPiglin;

import java.util.List;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.sensing.NearestItemSensor;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;

import static net.minecraft.world.entity.ai.memory.MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM;

@Mixin(NearestItemSensor.class)
public abstract class NearestItemsSensorMixin {
    @Unique private static final String SENSE
            = "doTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;)V";

    @Inject(method = SENSE, cancellable = true, at = @At("HEAD"))
    protected void head(ServerLevel world, Mob entity, CallbackInfo ci, @Share("cache") LocalRef<PiglinCache> c) {
        if (entity instanceof Piglin piglin) {
            if (PiglinBrainInvoker.doesNotHaveGoldInOffHand(piglin) && piglin.canPickUpLoot()) {
                final PiglinCache cache = ((CachedPiglin) piglin).toobee$getCache();
                if (cache != null) {
                    c.set(cache);
                    if (cache.getNearestItems() != null) {
                        entity.getBrain().setMemory(NEAREST_VISIBLE_WANTED_ITEM, cache.getNearestItem(world, piglin));
                        ci.cancel();
                    }
                }
            } else {
                entity.getBrain().setMemory(NEAREST_VISIBLE_WANTED_ITEM, Optional.empty());
                ci.cancel();
            }
        }
    }

    // Wait for more accurate detection
    @Inject(method = SENSE, cancellable = true,
            at = @At(value = "INVOKE", target = "Ljava/util/List;sort(Ljava/util/Comparator;)V", shift = At.Shift.AFTER, remap = false))
    protected void sort(ServerLevel world, Mob entity, CallbackInfo ci,
                        @Share("cache") LocalRef<PiglinCache> c, @Local List<ItemEntity> list) {
        if (entity instanceof Piglin piglin) {
            final PiglinCache cache = c.get();
            if (cache != null && PiglinBrainInvoker.doesNotHaveGoldInOffHand(piglin) && piglin.canPickUpLoot()
                    && ((CachedPiglin) piglin).toobee$hasNotBeenHitByPlayer()) {
                cache.setNearestItems(list);
                piglin.getBrain().setMemory(NEAREST_VISIBLE_WANTED_ITEM, cache.getNearestItem(world, piglin));
                ci.cancel();
            }
        }
    }
}
