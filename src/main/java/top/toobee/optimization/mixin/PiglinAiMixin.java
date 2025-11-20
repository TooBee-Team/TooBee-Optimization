package top.toobee.optimization.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.toobee.optimization.intermediary.CachedPiglin;

@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin {
    @Shadow
    private static boolean isNotHoldingLovedItemInOffHand(Piglin piglin) {
        throw new AssertionError();
    }

    @Redirect(method = "updateActivity", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/piglin/Piglin;setAggressive(Z)V"))
    private static void setAttacking(Piglin instance, boolean b) {
        final var c = ((CachedPiglin) instance).toobee$getCache();
        if (c != null)
            c.redirectAttacking(instance, b);
        else
            instance.setAggressive(b);
    }

    @Inject(method = "wantsToPickup", at = @At("HEAD"), cancellable = true)
    private static void canGather(Piglin piglin, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        final var p = (CachedPiglin) piglin;
        if (p.toobee$getCache() != null) {
            if (p.toobee$hasNotBeenHitByPlayer() && stack.is(PiglinAi.BARTERING_ITEM))
                cir.setReturnValue(isNotHoldingLovedItemInOffHand(piglin));
            else
                p.toobee$setHasNotBeenHitByPlayer(!piglin.getBrain().hasMemoryValue(MemoryModuleType.ADMIRING_DISABLED));
        }
    }

    @Redirect(method = "isNotHoldingLovedItemInOffHand", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isLovedItem(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean isGoldenItem(ItemStack stack) {
        return stack.is(PiglinAi.BARTERING_ITEM) || stack.is(ItemTags.PIGLIN_LOVED);
    }

    @Inject(method = "wasHurtBy", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/Brain;setMemoryWithExpiry(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;Ljava/lang/Object;J)V"))
    private static void hasNotBeenHitByPlayer(ServerLevel level, Piglin piglin, LivingEntity attacker, CallbackInfo ci) {
        ((CachedPiglin) piglin).toobee$setHasNotBeenHitByPlayer(false);
    }
}