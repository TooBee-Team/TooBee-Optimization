package top.toobee.optimization.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.toobee.optimization.cache.CachedMob;
import top.toobee.optimization.cache.StackingMobCache;

import java.util.Optional;

@Mixin(World.class)
public abstract class WorldMixin implements CollisionView {
    @Override
    public Optional<BlockPos> findSupportingBlockPos(final Entity entity, final Box box) {
        final StackingMobCache<?> cache;
        final Optional<BlockPos> p;
        if (entity instanceof CachedMob<?,?> c && (cache = c.toobee$getCache()) != null) {
            if (cache.getHasUpdatedThisTick()) {
                p = cache.getSupportingBlockPos();
                if (toobee$check(p, box))
                    return p;
            } else {
                p = CollisionView.super.findSupportingBlockPos(entity, box);
                if (toobee$check(p, box))
                    cache.setSupportingBlockPos(p);
                return p;
            }
        }
        return CollisionView.super.findSupportingBlockPos(entity, box);
    }

    @Unique
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static boolean toobee$check(final Optional<BlockPos> p, final Box box) {
        if (p.isEmpty()) return false;
        int x = p.get().getX(), z = p.get().getZ();
        return x <= box.minX && box.maxX <= ++x && z <= box.minZ && box.maxZ <= ++z;
    }
}
