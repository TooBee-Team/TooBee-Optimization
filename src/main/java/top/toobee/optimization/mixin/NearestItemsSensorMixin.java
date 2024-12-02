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
import top.toobee.optimization.cache.PiglinCache;
import top.toobee.optimization.intermediary.CachedPiglin;

import java.util.List;

import static net.minecraft.entity.ai.brain.MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM;

@Mixin(NearestItemsSensor.class)
public abstract class NearestItemsSensorMixin {
    @Unique private static final String SENSE
            = "sense(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/MobEntity;)V";

    @Inject(method = SENSE, cancellable = true, at = @At("HEAD"))
    protected void head(ServerWorld world, MobEntity entity, CallbackInfo ci, @Share("cache") LocalRef<PiglinCache> c) {
        final PiglinCache cache;
        if (entity instanceof PiglinEntity piglin && (cache = ((CachedPiglin) piglin).toobee$getCache()) != null) {
            c.set(cache);
            if (!cache.getHasUpdatedThisTick()) {
                entity.getBrain().remember(NEAREST_VISIBLE_WANTED_ITEM, cache.getNearestItem(world, piglin));
                ci.cancel();
            }
        }
    }

    @Inject(method = SENSE, cancellable = true,
            at = @At(value = "INVOKE", target = "Ljava/util/List;sort(Ljava/util/Comparator;)V", shift = At.Shift.AFTER))
    protected void sort(ServerWorld world, MobEntity entity, CallbackInfo ci,
                        @Share("cache") LocalRef<PiglinCache> c, @Local List<ItemEntity> list) {
        final PiglinCache cache = c.get();
        if (cache != null) {
            cache.setNearestItems(list);
            entity.getBrain().remember(NEAREST_VISIBLE_WANTED_ITEM, cache.getNearestItem(world, (PiglinEntity) entity));
            ci.cancel();
        }
    }
}
