package top.toobee.optimization.accessor;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEntity.class)
public interface MobEntityAccessor {
    @Accessor("MOB_FLAGS")
    static TrackedData<Byte> getMobFlags() {
        throw new AssertionError();
    }
}