package top.toobee.optimization.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.WardenAngerManager;
import net.minecraft.entity.mob.WardenEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.toobee.optimization.cache.BeCached;
import top.toobee.optimization.cache.WardenCache;

@Mixin(WardenEntity.class)
public abstract class WardenMixin implements BeCached<WardenCache> {

    @Unique
    private WardenCache cache = null;
    @Shadow
    WardenAngerManager angerManager;
    @Shadow
    @Nullable
    public abstract LivingEntity getTarget();

    @Override
    public WardenCache toobee$getCache() {
        return this.cache;
    }

    @Override
    public void toobee$setCache(WardenCache cache) {
        this.cache = cache;
    }

    /**
     * @author Fungus
     * @reason Use the cache value of anger
     */
    @Overwrite
    private int getAngerAtTarget() {
        if (this.cache != null && this.cache.getHasUpdatedThisTick())
            return this.cache.getAngerAtTarget();
        int i = this.angerManager.getAngerFor(this.getTarget());
        if (this.cache!= null)
            this.cache.setAngerAtTarget(i);
        return i;
    }
}
