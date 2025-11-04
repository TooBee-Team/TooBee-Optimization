package top.toobee.optimization.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.stream.Stream;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(EnderMan.class)
public abstract class EndermanMixin {
    @Unique
    @SuppressWarnings("deprecation")
    private static final HolderSet<Block> EXCEPTIONS = HolderSet.direct(Stream.of(
            Blocks.BROWN_MUSHROOM,
            Blocks.CRIMSON_FUNGUS,
            Blocks.CRIMSON_NYLIUM,
            Blocks.CRIMSON_ROOTS,
            Blocks.DIRT,
            Blocks.GRASS_BLOCK,
            Blocks.GRAVEL,
            Blocks.RED_MUSHROOM,
            Blocks.SAND,
            Blocks.WARPED_FUNGUS,
            Blocks.WARPED_NYLIUM,
            Blocks.WARPED_ROOTS
    ).map(Block::builtInRegistryHolder).toList());

    @ModifyExpressionValue(method = "requiresCustomPersistence", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/EnderMan;getCarriedBlock()Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState cannotDespawn(@Nullable BlockState original) {
        return original != null && !original.is(EXCEPTIONS) ? original : null;
    }
}
