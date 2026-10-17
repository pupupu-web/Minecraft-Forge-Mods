package dev.whiteenderman.client;

import dev.whiteenderman.WhiteEndermanMod;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EndermanRenderState;
import net.minecraft.resources.Identifier;

/** Uses vanilla Enderman geometry, teleport animation and glowing eye layer. */
public final class WhiteEndermanRenderer extends EndermanRenderer {
    private static final Identifier WHITE_TEXTURE =
            Identifier.fromNamespaceAndPath(WhiteEndermanMod.MOD_ID,
                    "textures/entity/white_enderman.png");

    public WhiteEndermanRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(EndermanRenderState state) {
        return WHITE_TEXTURE;
    }
}
