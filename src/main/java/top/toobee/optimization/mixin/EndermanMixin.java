package top.toobee.optimization.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.registry.entry.RegistryEntryList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.stream.Stream;

@Mixin(EndermanEntity.class)
public abstract class EndermanMixin {
    @Unique
    @SuppressWarnings("deprecation")
    private static final RegistryEntryList<Block> EXCEPTIONS = RegistryEntryList.of(Stream.of(
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
    ).map(Block::getRegistryEntry).toList());

    @ModifyExpressionValue(method = "cannotDespawn", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/mob/EndermanEntity;getCarriedBlock()Lnet/minecraft/block/BlockState;"))
    private BlockState cannotDespawn(@Nullable BlockState original) {
        return original != null && !original.isIn(EXCEPTIONS) ? original : null;
    }
}
