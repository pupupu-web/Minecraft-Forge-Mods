package dev.pupupu.cozybeds.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.DyeColor;

/**
 * A client-side entity layer, not an in-world entity or block.
 *
 * The duvet is dyed to match the bed, while the turned-down trim and pillow
 * remain pale. Queued after the base player/villager model in the feature
 * submission system.
 */
public final class CozyQuiltLayer<S extends LivingEntityRenderState,
        M extends EntityModel<? super S>> extends RenderLayer<S, M> {

    private static final Identifier FABRIC =
            Identifier.fromNamespaceAndPath("cozybeds", "textures/entity/cozy_fabric.png");
    private static final RenderType FABRIC_RENDER = RenderType.entityCutoutNoCull(FABRIC);

    private final CozyQuiltModel quilt;
    private final CozyQuiltModel linen;

    public CozyQuiltLayer(RenderLayerParent<S, M> renderer, EntityModelSet models) {
        super(renderer);
        quilt = new CozyQuiltModel(models.bakeLayer(CozyBedsClient.QUILT_LAYER));
        linen = new CozyQuiltModel(models.bakeLayer(CozyBedsClient.LINEN_LAYER));
    }

    @Override
    public void submit(PoseStack poses, SubmitNodeCollector collector, int light,
                       S state, float yRot, float xRot) {
        if (!state.hasPose(Pose.SLEEPING) || state.bedOrientation == null || state.isInvisible) return;
        DyeColor dye = state.getRenderData(CozyBedsClient.BED_COLOR);
        if (dye == null) return;

        int tint = 0xFF000000 | (dye.getTextureDiffuseColor() & 0x00FFFFFF);
        collector.order(1).submitModel(quilt, state, poses, FABRIC_RENDER, light,
                OverlayTexture.NO_OVERLAY, tint, null, 0);
        collector.order(2).submitModel(linen, state, poses, FABRIC_RENDER, light,
                OverlayTexture.NO_OVERLAY, 0xFFF5F1EA, null, 0);
    }
}
