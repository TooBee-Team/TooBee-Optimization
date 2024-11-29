package top.toobee.optimization.mixin;

import net.minecraft.entity.data.DataTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.toobee.optimization.cache.PiglinCache;
import top.toobee.optimization.intermediary.DataTrackerIntermediary;

@Mixin(DataTracker.class)
public abstract class DataTrackerMixin implements DataTrackerIntermediary {
    @Shadow private boolean dirty;
    @Shadow @Final private DataTracker.Entry<?>[] entries;

    @Override
    public void toobee$setMobFlags(byte value) {
        @SuppressWarnings("unchecked")
        final var entry = (DataTracker.Entry<Byte>) this.entries[PiglinCache.getMOB_FLAGS_ID()];
        entry.set(value);
        entry.setDirty(true);
        this.dirty = true;
    }

    @Override
    public byte toobee$getMobFlags() {
        @SuppressWarnings("unchecked")
        final var entry = (DataTracker.Entry<Byte>) this.entries[PiglinCache.getMOB_FLAGS_ID()];
        return entry.get();
    }
}