package top.toobee.optimization.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.toobee.optimization.intermediary.CachedPiglin;

import java.util.List;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

@Mixin(Mob.class)
public abstract class MobEntityMixin {
    @Redirect(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    public List<ItemEntity> getStackingEntities(Level instance, Class<ItemEntity> entityClass, AABB box) {
        if (this instanceof CachedPiglin p) {
            final var c = p.toobee$getCache();
            if (c != null) {
                if (!c.getHasUpdatedThisTick())
                    c.canPickUpItems = instance.getEntitiesOfClass(entityClass, box);
                return c.canPickUpItems;
            }
        }
        return instance.getEntitiesOfClass(entityClass, box);
    }
}