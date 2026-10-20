package dev.pupupu.cozybeds;

import java.util.ArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * One-way migration from v0.1.0. It never creates or updates display entities.
 * The old version persisted its generated quilts as block displays.
 * Remove only displays tagged with its unique persistent-data key.
 */
@EventBusSubscriber(modid = CozyBeds.MOD_ID)
public final class LegacyDecorationCleanup {
    private static long ticks;

    private LegacyDecorationCleanup() {}

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (++ticks % 200 != 0) return;
        for (ServerLevel level : event.getServer().getAllLevels()) {
            var outdated = new ArrayList<Display.BlockDisplay>();
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof Display.BlockDisplay display
                        && display.getPersistentData().getBooleanOr("cozybeds_generated", false)) {
                    outdated.add(display);
                }
            }
            outdated.forEach(Entity::discard);
        }
    }
}
