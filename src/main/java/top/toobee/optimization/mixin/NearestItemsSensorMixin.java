package top.toobee.optimization.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.sensor.NearestItemsSensor;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;
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

import static net.minecraft.entity.ai.brain.MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM;

@Mixin(NearestItemsSensor.class)
public abstract class NearestItemsSensorMixin {
    @Unique private static final String SENSE
            = "sense(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/MobEntity;)V";

    @Inject(method = SENSE, cancellable = true, at = @At("HEAD"))
    protected void head(ServerWorld world, MobEntity entity, CallbackInfo ci, @Share("cache") LocalRef<PiglinCache> c) {
        if (entity instanceof PiglinEntity piglin) {
            if (PiglinBrainInvoker.doesNotHaveGoldInOffHand(piglin) && piglin.canPickUpLoot()) {
                final PiglinCache cache = ((CachedPiglin) piglin).toobee$getCache();
                if (cache != null) {
                    c.set(cache);
                    if (cache.getNearestItems() != null) {
                        entity.getBrain().remember(NEAREST_VISIBLE_WANTED_ITEM, cache.getNearestItem(world, piglin));
                        ci.cancel();
                    }
                }
            } else {
                entity.getBrain().remember(NEAREST_VISIBLE_WANTED_ITEM, Optional.empty());
                ci.cancel();
            }
        }
    }

    // Wait for more accurate detection
    @Inject(method = SENSE, cancellable = true,
            at = @At(value = "INVOKE", target = "Ljava/util/List;sort(Ljava/util/Comparator;)V", shift = At.Shift.AFTER, remap = false))
    protected void sort(ServerWorld world, MobEntity entity, CallbackInfo ci,
                        @Share("cache") LocalRef<PiglinCache> c, @Local List<ItemEntity> list) {
        if (entity instanceof PiglinEntity piglin) {
            final PiglinCache cache = c.get();
            if (cache != null && PiglinBrainInvoker.doesNotHaveGoldInOffHand(piglin) && piglin.canPickUpLoot()
                    && ((CachedPiglin) piglin).toobee$hasNotBeenHitByPlayer()) {
                cache.setNearestItems(list);
                piglin.getBrain().remember(NEAREST_VISIBLE_WANTED_ITEM, cache.getNearestItem(world, piglin));
                ci.cancel();
            }
        }
    }
}
