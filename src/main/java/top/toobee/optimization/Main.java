package top.toobee.optimization;

import net.caffeinemc.mods.lithium.common.entity.pushable.PushableEntityClassGroup;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.world.entity.monster.warden.Warden;
import top.toobee.optimization.cache.PiglinCache;
import top.toobee.optimization.cache.WardenCache;
import top.toobee.optimization.accessor.EntityClassGroupAccessor;

public final class Main implements DedicatedServerModInitializer {
    public static void startServerTick() {
        WardenCache.CACHES.tick();
        PiglinCache.CACHES.tick();
    }

    @Override
    public void onInitializeServer() {
        ((EntityClassGroupAccessor) PushableEntityClassGroup.MAYBE_PUSHABLE)
                .getClass2GroupContains().addTo(Warden.class, (byte) 1);
        ((EntityClassGroupAccessor) PushableEntityClassGroup.CACHABLE_UNPUSHABILITY)
                .getClass2GroupContains().addTo(Warden.class, (byte) 1);
    }
}
