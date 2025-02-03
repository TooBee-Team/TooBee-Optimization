package top.toobee.optimization.mixin;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

@Mixin(HostileEntity.class)
public abstract class HostileMixin extends MobEntity {
    protected HostileMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique private static final Set<Item> exceptions = ReferenceOpenHashSet.of(
            Items.ARROW,
            Items.BONE,
            Items.BROWN_MUSHROOM,
            Items.COBBLED_DEEPSLATE,
            Items.COBBLESTONE,
            Items.DIRT,
            Items.EGG,
            Items.GRASS_BLOCK,
            Items.GRAVEL,
            Items.GUNPOWDER,
            Items.POINTED_DRIPSTONE,
            Items.RED_MUSHROOM,
            Items.ROTTEN_FLESH,
            Items.SPIDER_EYE,
            Items.STRING,
            Items.TORCH,
            Items.WHEAT_SEEDS
    );

    @Override
    protected void equipLootStack(EquipmentSlot slot, ItemStack stack) {
        this.equipStack(slot, stack);
        this.updateDropChances(slot);
        final var item = stack.getItem();
        if (!exceptions.contains(item) || !item.getComponents().isEmpty())
            this.setPersistent();
    }
}
