package top.toobee.optimization.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.toobee.optimization.intermediary.CachedPiglin;

@Mixin(PiglinBrain.class)
public abstract class PiglinBrainMixin {
    @Shadow
    private static boolean doesNotHaveGoldInOffHand(PiglinEntity piglin) {
        throw new AssertionError();
    }

    @Redirect(method = "tickActivities", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/mob/PiglinEntity;setAttacking(Z)V"))
    private static void setAttacking(PiglinEntity instance, boolean b) {
        final var c = ((CachedPiglin) instance).toobee$getCache();
        if (c != null)
            c.redirectAttacking(instance, b);
        else
            instance.setAttacking(b);
    }

    @Inject(method = "canGather", at = @At("HEAD"), cancellable = true)
    private static void canGather(PiglinEntity piglin, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        final var p = (CachedPiglin) piglin;
        if (p.toobee$getCache() != null) {
            if (p.toobee$hasNotBeenHitByPlayer() && stack.isOf(Items.GOLD_INGOT))
                cir.setReturnValue(doesNotHaveGoldInOffHand(piglin));
            else
                p.toobee$setHasNotBeenHitByPlayer(piglin.getBrain().hasMemoryModule(MemoryModuleType.ADMIRING_DISABLED));
        }
    }

    @Redirect(method = "doesNotHaveGoldInOffHand", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/mob/PiglinBrain;isGoldenItem(Lnet/minecraft/item/ItemStack;)Z"))
    private static boolean isGoldenItem(ItemStack stack) {
        return stack.isOf(Items.GOLD_INGOT) || stack.isIn(ItemTags.PIGLIN_LOVED);
    }

    @Inject(method = "onAttacked", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/ai/brain/Brain;remember(Lnet/minecraft/entity/ai/brain/MemoryModuleType;Ljava/lang/Object;J)V"))
    private static void hasNotBeenHitByPlayer(ServerWorld world, PiglinEntity piglin, LivingEntity attacker, CallbackInfo ci) {
        ((CachedPiglin) piglin).toobee$setHasNotBeenHitByPlayer(false);
    }
}