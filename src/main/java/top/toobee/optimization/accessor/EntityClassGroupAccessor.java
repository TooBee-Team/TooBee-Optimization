package top.toobee.optimization.accessor;

import it.unimi.dsi.fastutil.objects.Reference2ByteOpenHashMap;
import net.caffeinemc.mods.lithium.common.entity.EntityClassGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityClassGroup.class)
public interface EntityClassGroupAccessor {
    @Accessor(remap = false) Reference2ByteOpenHashMap<Class<?>> getClass2GroupContains();
}