package top.toobee.optimization.mixin;

import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.toobee.optimization.cache.PiglinCache;
import top.toobee.optimization.intermediary.DataTrackerIntermediary;

@Mixin(SynchedEntityData.class)
public abstract class DataTrackerMixin implements DataTrackerIntermediary {
    @Shadow private boolean isDirty;
    @Shadow @Final private SynchedEntityData.DataItem<?>[] itemsById;

    @Override
    public void toobee$setMobFlags(byte value) {
        @SuppressWarnings("unchecked")
        final var entry = (SynchedEntityData.DataItem<Byte>) this.itemsById[PiglinCache.MOB_FLAGS_ID];
        entry.setValue(value);
        entry.setDirty(true);
        this.isDirty = true;
    }

    @Override
    public byte toobee$getMobFlags() {
        @SuppressWarnings("unchecked")
        final var entry = (SynchedEntityData.DataItem<Byte>) this.itemsById[PiglinCache.MOB_FLAGS_ID];
        return entry.getValue();
    }
}