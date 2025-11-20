package top.toobee.optimization.mixin;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

@Mixin(Monster.class)
public abstract class MonsterMixin extends Mob {
    protected MonsterMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
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
            Items.POTATO,
            Items.ROTTEN_FLESH,
            Items.SPIDER_EYE,
            Items.STONE,
            Items.STRING,
            Items.TORCH,
            Items.WHEAT_SEEDS
    );

    @Override
    protected void setItemSlotAndDropWhenKilled(EquipmentSlot slot, ItemStack stack) {
        setItemSlot(slot, stack);
        setGuaranteedDrop(slot);
        final var item = stack.getItem();
        if (!exceptions.contains(item))// || !item.getComponents().isEmpty())
            setPersistenceRequired();
    }
}
