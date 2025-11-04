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
public abstract class WorldMixin implements CollisionGetter {
    @Override
    public @NotNull Optional<BlockPos> findSupportingBlock(final Entity entity, final AABB box) {
        final StackingMobCache<?> cache;
        final Optional<BlockPos> p;
        if (entity instanceof CachedMob<?,?> c && (cache = c.toobee$getCache()) != null) {
            if (cache.getHasUpdatedThisTick()) {
                p = cache.supportingBlockPos;
                if (toobee$check(p, box))
                    return p;
            } else {
                p = CollisionGetter.super.findSupportingBlock(entity, box);
                if (toobee$check(p, box))
                    cache.supportingBlockPos = p;
                return p;
            }
        }
        return CollisionGetter.super.findSupportingBlock(entity, box);
    }

    // Wait for completing
    @Unique
    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType"})
    private static boolean toobee$check(final Optional<BlockPos> p, final AABB box) {
        if (p.isEmpty()) return false;
        int x = p.get().getX(), z = p.get().getZ();
        return  x <= (int) box.minX && (int) Math.ceil(box.maxX) <= ++x &&
                z <= (int) box.minZ && (int) Math.ceil(box.maxZ) <= ++z;

//        Box box = this.getBoundingBox().contract(0.001);
//        int i = MathHelper.floor(box.minX);
//        int j = MathHelper.ceil(box.maxX);
//        int k = MathHelper.floor(box.minY);
//        int l = MathHelper.ceil(box.maxY);
//        int m = MathHelper.floor(box.minZ);
//        int n = MathHelper.ceil(box.maxZ);
    }
}
