package top.toobee.optimization.mixin;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.toobee.optimization.cache.StackingMobCache;
import top.toobee.optimization.intermediary.CachedMob;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

@Mixin(Level.class)
public abstract class LevelMixin implements CollisionGetter {
    @Override
    public @NotNull Optional<BlockPos> findSupportingBlock(final @NotNull Entity entity, final @NotNull AABB aabb) {
        final StackingMobCache<?> cache;
        final Optional<BlockPos> p;
        if (entity instanceof CachedMob<?,?> c && (cache = c.toobee$getCache()) != null) {
            if (cache.getHasUpdatedThisTick()) {
                p = cache.supportingBlockPos;
                if (toobee$check(p, aabb))
                    return p;
            } else {
                p = CollisionGetter.super.findSupportingBlock(entity, aabb);
                if (toobee$check(p, aabb))
                    cache.supportingBlockPos = p;
                return p;
            }
        }
        return CollisionGetter.super.findSupportingBlock(entity, aabb);
    }

    // Wait for completing
    @Unique
    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType"})
    private static boolean toobee$check(final Optional<BlockPos> p, final AABB aabb) {
        if (p.isEmpty()) return false;
        int x = p.get().getX(), z = p.get().getZ();
        return  x <= (int) aabb.minX && (int) Math.ceil(aabb.maxX) <= ++x &&
                z <= (int) aabb.minZ && (int) Math.ceil(aabb.maxZ) <= ++z;

//        Box aabb = this.getBoundingBox().contract(0.001);
//        int i = MathHelper.floor(aabb.minX);
//        int j = MathHelper.ceil(aabb.maxX);
//        int k = MathHelper.floor(aabb.minY);
//        int l = MathHelper.ceil(aabb.maxY);
//        int m = MathHelper.floor(aabb.minZ);
//        int n = MathHelper.ceil(aabb.maxZ);
    }
}
