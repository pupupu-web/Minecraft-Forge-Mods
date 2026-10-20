package dev.pupupu.cozybeds.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.pupupu.cozybeds.CozyBeds;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.renderstate.AvatarRenderStateModifier;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

/**
 * The new blanket is positioned in WORLD SPACE at the head half of the
 * vanilla bed, then rotated toward the foot. Its position no longer depends
 * on the villager/player's unusual sleeping model rotations.
 */
@EventBusSubscriber(modid = CozyBeds.MOD_ID, value = Dist.CLIENT)
public final class CozyBedsClient {
    private CozyBedsClient() {}

    private static final Identifier FABRIC =
            Identifier.fromNamespaceAndPath(CozyBeds.MOD_ID, "textures/entity/cozy_fabric.png");
    private static final RenderType FABRIC_RENDER = RenderTypes.entityCutout(FABRIC);

    public static final ContextKey<DyeColor> BED_COLOR =
            new ContextKey<>(Identifier.fromNamespaceAndPath(CozyBeds.MOD_ID, "bed_color"));
    public static final ContextKey<BlockPos> BED_HEAD =
            new ContextKey<>(Identifier.fromNamespaceAndPath(CozyBeds.MOD_ID, "bed_head"));
    public static final ContextKey<Direction> BED_FACING =
            new ContextKey<>(Identifier.fromNamespaceAndPath(CozyBeds.MOD_ID, "bed_facing"));

    private static final ModelLayerLocation QUILT_LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(CozyBeds.MOD_ID, "quilt"), "main");
    private static final ModelLayerLocation FOLD_LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(CozyBeds.MOD_ID, "fold"), "main");
    private static final ModelLayerLocation STITCH_LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(CozyBeds.MOD_ID, "stitch"), "main");

    private static CozyQuiltModel quilt;
    private static CozyQuiltModel fold;
    private static CozyQuiltModel stitch;

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(QUILT_LAYER, CozyQuiltModel::makeQuilt);
        event.registerLayerDefinition(FOLD_LAYER, CozyQuiltModel::makeFold);
        event.registerLayerDefinition(STITCH_LAYER, CozyQuiltModel::makeStitch);
    }

    @SubscribeEvent
    public static void bakeModels(EntityRenderersEvent.AddLayers event) {
        var models = event.getEntityModels();
        quilt = new CozyQuiltModel(models.bakeLayer(QUILT_LAYER));
        fold = new CozyQuiltModel(models.bakeLayer(FOLD_LAYER));
        stitch = new CozyQuiltModel(models.bakeLayer(STITCH_LAYER));
    }

    @SubscribeEvent
    public static void registerSleepingStates(RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(VillagerRenderer.class, (villager, state) ->
                extractBedAndHideBody(villager, state, false));

        event.registerAvatarEntityModifier(new AvatarRenderStateModifier() {
            @Override
            public <T extends Avatar & ClientAvatarEntity> void accept(T avatar, AvatarRenderState state) {
                if (avatar instanceof AbstractClientPlayer player) {
                    extractBedAndHideBody(player, state, true);
                }
            }
        });
    }

    private static void extractBedAndHideBody(LivingEntity living,
                                               LivingEntityRenderState state,
                                               boolean player) {
        // This render state is reused; reset previous-entity data first.
        state.setRenderData(BED_COLOR, null);
        state.setRenderData(BED_HEAD, null);
        state.setRenderData(BED_FACING, null);

        if (!living.isSleeping()) return;
        BlockPos pos = living.getSleepingPos().orElse(null);
        if (pos == null) return;
        BlockState bedState = living.level().getBlockState(pos);
        if (!(bedState.getBlock() instanceof BedBlock bed)) return;

        Direction facing = bedState.getValue(BedBlock.FACING);
        BlockPos head = bedState.getValue(BedBlock.PART) == BedPart.HEAD
                ? pos : pos.relative(facing);
        // Do not modify a non-bed or a broken bed counterpart.
        if (!(living.level().getBlockState(head).getBlock() instanceof BedBlock)) return;

        state.setRenderData(BED_COLOR, bed.getColor());
        state.setRenderData(BED_HEAD, head.immutable());
        state.setRenderData(BED_FACING, facing);

        // These names address the visible vanilla model core. NeoForge applies
        // visibility overrides to submitted model layers as well.
        for (String part : new String[]{"body", "right_leg", "left_leg", "arms", "robe"}) {
            state.overrideModelPartVisibility(part, false);
        }
        if (player) {
            for (String part : new String[]{"right_arm", "left_arm", "jacket",
                    "right_sleeve", "left_sleeve", "right_pants", "left_pants"}) {
                state.overrideModelPartVisibility(part, false);
            }
        }
    }

    /**
     * Post is fired after the vanilla sleeping model transforms have popped.
     * Render at the exact bed block location rather than at model-relative
     * offsets; this avoids both the 0.2.0 head cube and foot overhang.
     */
    @SubscribeEvent
    public static void renderSleepingQuilt(RenderLivingEvent.Post<?, ?, ?> event) {
        LivingEntityRenderState state = event.getRenderState();
        if (!state.hasPose(Pose.SLEEPING) || state.isInvisible) return;
        BlockPos head = state.getRenderData(BED_HEAD);
        Direction facing = state.getRenderData(BED_FACING);
        DyeColor color = state.getRenderData(BED_COLOR);
        if (head == null || facing == null || color == null || quilt == null || fold == null || stitch == null) return;

        PoseStack stack = event.getPoseStack();
        stack.pushPose();
        // All three models have local pixel coordinates, which ModelPart converts
        // to 1/16 blocks. Local +Z points from bed head to foot.
        stack.translate(head.getX() + 0.5 - state.x,
                head.getY() + 9.0 / 16.0 - state.y,
                head.getZ() + 0.5 - state.z);
        stack.rotateDegrees(Axis.YP, 180.0F - facing.toYRot());

        int tint = (color == DyeColor.WHITE)
                ? 0xFFD0D7DB
                : 0xFF000000 | (color.getTextureDiffuseColor() & 0x00FFFFFF);
        var collector = event.getSubmitNodeCollector();
        collector.order(3).submitModel(quilt, state, stack, FABRIC_RENDER, state.lightCoords,
                OverlayTexture.NO_OVERLAY, tint, null, 0);
        collector.order(4).submitModel(fold, state, stack, FABRIC_RENDER, state.lightCoords,
                OverlayTexture.NO_OVERLAY, 0xFFF8EFE4, null, 0);
        collector.order(5).submitModel(stitch, state, stack, FABRIC_RENDER, state.lightCoords,
                OverlayTexture.NO_OVERLAY, 0xFF82949C, null, 0);
        stack.popPose();
    }
}
