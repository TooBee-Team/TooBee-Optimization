package top.toobee.optimization.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.toobee.optimization.track.BeTracked;
import top.toobee.optimization.track.TooBeeTrackers;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements BeTracked {
    @Unique
    private final LivingEntity self = (LivingEntity) (Object) this;
    @Unique
    private short tracked = 0;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void setWorld(final World world) {
        if (!self.getWorld().equals(world)) {
            if (this.tracked != 0)
                TooBeeTrackers.INSTANCE.removeTarget(self.getWorld(), this);
            super.setWorld(world);
        }
    }

    @Override
    public synchronized void toobee$increaseTrackedAmount() {
        ++this.tracked;
    }

    @Override
    public synchronized void toobee$decreaseTrackedAmount() {
        --this.tracked;
    }

    @Override
    public synchronized void toobee$resetTrackedAmount() {
        this.tracked = 0;
    }
}