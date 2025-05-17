package top.toobee.optimization.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartMixin extends Entity {
    public AbstractMinecartMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    // Fix MC-296337, from https://github.com/Exopandora/MinecartMemoryLeakFix
    @Inject(method = "tickBlockCollision", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;tickBlockCollision(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)V"))
    private void tickBlockCollision(CallbackInfo ci) {
        popQueuedCollisionCheck();
    }
}
