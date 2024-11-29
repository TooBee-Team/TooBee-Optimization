package top.toobee.optimization.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.toobee.optimization.intermediary.BeCached;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "setRemoved", at = @At("HEAD"))
    public final void setRemoved(Entity.RemovalReason reason, CallbackInfo ci) {
        if (this instanceof BeCached<?> b)
            b.toobee$removeCache();
    }
}
