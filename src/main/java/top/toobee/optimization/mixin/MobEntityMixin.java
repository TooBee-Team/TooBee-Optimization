package top.toobee.optimization.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.toobee.optimization.intermediary.CachedPiglin;

import java.util.List;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;getNonSpectatingEntities(Ljava/lang/Class;Lnet/minecraft/util/math/Box;)Ljava/util/List;"))
    public List<ItemEntity> getStackingEntities(World instance, Class<ItemEntity> entityClass, Box box) {
        if (this instanceof CachedPiglin p) {
            final var c = p.toobee$getCache();
            if (c != null) {
                if (!c.getHasUpdatedThisTick())
                    c.canPickUpItems = instance.getNonSpectatingEntities(entityClass, box);
                return c.canPickUpItems;
            }
        }
        return instance.getNonSpectatingEntities(entityClass, box);
    }
}