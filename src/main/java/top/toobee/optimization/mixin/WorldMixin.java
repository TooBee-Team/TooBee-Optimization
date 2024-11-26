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
                if (p.isPresent() && check(p.get().getX(), p.get().getZ(), box))
                    return cache.getSupportingBlockPos();
            } else {
                p = CollisionView.super.findSupportingBlockPos(entity, box);
                if (p.isPresent() && check(p.get().getX(), p.get().getZ(), box))
                    cache.setSupportingBlockPos(p);
                return p;
            }
        }
        return CollisionView.super.findSupportingBlockPos(entity, box);
    }

    @Unique
    private static boolean check(int x, int z, Box box) {
        int     x1 = (int) box.minX,
                x2 = (int) box.maxX,
                z1 = (int) box.minZ,
                z2 = (int) box.maxZ;
        return x <= x1 && x2 <= ++x && z <= z1 && ++z <= z2;
    }
}
