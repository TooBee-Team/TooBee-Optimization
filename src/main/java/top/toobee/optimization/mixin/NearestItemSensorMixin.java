package top.toobee.optimization.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.toobee.optimization.accessor.PiglinAiInvoker;
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
public abstract class NearestItemSensorMixin {
    @Unique private static final String SENSE
            = "doTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;)V";

    @Inject(method = SENSE, cancellable = true, at = @At("HEAD"))
    protected void head(ServerLevel level, Mob body, CallbackInfo ci, @Share("cache") LocalRef<PiglinCache> c) {
        if (body instanceof Piglin piglin) {
            final PiglinCache cache = ((CachedPiglin) piglin).toobee$getCache();
            if (PiglinAiInvoker.isNotHoldingLovedItemInOffHand(piglin)) {
                if (cache == null) return;
                c.set(cache);
                if (cache.getNearestItems() != null) {
                    piglin.getBrain().setMemory(NEAREST_VISIBLE_WANTED_ITEM, cache.getNearestItem(level, piglin));
                    ci.cancel();
                }
            } else {
                piglin.getBrain().setMemory(NEAREST_VISIBLE_WANTED_ITEM, Optional.empty());
                ci.cancel();
            }
        }
    }

    // Wait for more accurate detection
    @Inject(method = SENSE, cancellable = true,
            at = @At(value = "INVOKE", target = "Ljava/util/List;sort(Ljava/util/Comparator;)V", shift = At.Shift.AFTER))
    protected void sort(ServerLevel level, Mob body, CallbackInfo ci,
                        @Share("cache") LocalRef<PiglinCache> c, @Local(name = "items") List<ItemEntity> items) {
        if (body instanceof Piglin piglin) {
            final PiglinCache cache = c.get();
            if (cache == null) return;
            if (PiglinAiInvoker.isNotHoldingLovedItemInOffHand(piglin) && piglin.canPickUpLoot() && ((CachedPiglin) piglin).toobee$hasNotBeenHitByPlayer()) {
                cache.setNearestItems(items);
                piglin.getBrain().setMemory(NEAREST_VISIBLE_WANTED_ITEM, cache.getNearestItem(level, piglin));
                ci.cancel();
            }
        }
    }
}
