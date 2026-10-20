package dev.pupupu.cozybeds;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/**
 * Cozy Beds: a render-only vanilla sleep makeover.
 * No block displays, altered hitboxes, modified saves or network packets.
 */
@Mod(CozyBeds.MOD_ID)
public final class CozyBeds {
    public static final String MOD_ID = "cozybeds";

    public CozyBeds(IEventBus modBus, ModContainer container) {
        // Client-only render events are registered by CozyBedsClient.
        // The shared side is intentionally free of sleeping/gameplay modifications.
    }
}
