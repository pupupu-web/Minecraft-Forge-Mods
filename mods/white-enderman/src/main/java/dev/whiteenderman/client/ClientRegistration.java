package dev.whiteenderman.client;

import dev.whiteenderman.WhiteEndermanMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Client-only renderer hookup: never loaded on a dedicated server. */
@Mod.EventBusSubscriber(modid = WhiteEndermanMod.MOD_ID, value = Dist.CLIENT)
public final class ClientRegistration {
    private ClientRegistration() {}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(WhiteEndermanMod.WHITE_ENDERMAN.get(),
                WhiteEndermanRenderer::new);
    }
}
