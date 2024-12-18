package top.toobee.optimization.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.toobee.optimization.Optimization;

import java.util.function.BooleanSupplier;

// Substitution of Fabric API, implemented by myself
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void onStartTick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
        Optimization.Companion.startServerTick();
    }
}