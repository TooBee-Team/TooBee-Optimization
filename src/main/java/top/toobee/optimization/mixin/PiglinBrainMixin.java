package top.toobee.optimization.mixin;

import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.toobee.optimization.cache.PiglinCache;
import top.toobee.optimization.intermediary.CachedMob;

@Mixin(PiglinBrain.class)
public abstract class PiglinBrainMixin {
    @Redirect(method = "tickActivities", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/mob/PiglinEntity;setAttacking(Z)V"))
    private static void setAttacking(PiglinEntity instance, boolean b) {
        if (((CachedMob<?,?>) instance).toobee$getCache() instanceof PiglinCache c)
            c.redirectAttacking(instance, b);
        else
            instance.setAttacking(b);
    }
}