package top.toobee.optimization.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.toobee.optimization.cache.StackingMobCache;
import top.toobee.optimization.intermediary.CachedMob;

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

    // Wait for completing
    @Unique
    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType"})
    private static boolean toobee$check(final Optional<BlockPos> p, final Box box) {
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
