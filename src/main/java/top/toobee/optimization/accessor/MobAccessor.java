package top.toobee.optimization.accessor;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
public interface MobAccessor {
    @Accessor("DATA_MOB_FLAGS_ID")
    static EntityDataAccessor<@NotNull Byte> getMobFlags() {
        throw new AssertionError();
    }
}