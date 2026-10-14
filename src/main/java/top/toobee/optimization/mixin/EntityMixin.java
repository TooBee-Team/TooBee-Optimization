package top.toobee.optimization.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.toobee.optimization.cache.StackingMobCache;
import top.toobee.optimization.intermediary.BeCached;
import top.toobee.optimization.intermediary.CachedMob;

import java.util.Optional;

@Debug(export = true)
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "setRemoved", at = @At("HEAD"))
    public final void setRemoved(Entity.RemovalReason reason, CallbackInfo ci) {
        if (this instanceof BeCached<?> b)
            b.toobee$removeCache();
    }

    @Redirect(method = "checkSupportingBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;findSupportingBlock(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/Optional;"))
    protected Optional<BlockPos> findPos(Level instance, Entity entity, AABB aabb) {
        final StackingMobCache<?> cache;
        final Optional<BlockPos> p;
        if (entity instanceof CachedMob<?,?> c && (cache = c.toobee$getCache()) != null) {
            if (cache.getHasUpdatedThisTick()) {
                p = cache.supportingBlockPos;
                if (toobee$check(p, aabb))
                    return p;
            } else {
                p = instance.findSupportingBlock(entity, aabb);
                if (toobee$check(p, aabb))
                    cache.supportingBlockPos = p;
                return p;
            }
        }
        return instance.findSupportingBlock(entity, aabb);
    }

    @Unique
    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType"})
    private static boolean toobee$check(final Optional<BlockPos> p, final AABB aabb) {
        if (p.isEmpty()) return false;
        int x = p.get().getX(), z = p.get().getZ();
        return  x <= (int) aabb.minX && (int) Math.ceil(aabb.maxX) <= ++x &&
                z <= (int) aabb.minZ && (int) Math.ceil(aabb.maxZ) <= ++z;
    }
}
