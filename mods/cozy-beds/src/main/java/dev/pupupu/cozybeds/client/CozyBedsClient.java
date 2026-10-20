package dev.pupupu.cozybeds.client;

import dev.pupupu.cozybeds.CozyBeds;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.AvatarRenderer;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.renderstate.AvatarRenderStateModifier;
import net.neoforged.neoforge.client.renderstate.ContextKey;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

/**
 * All hooks execute only on a physical client.
 * No render mixins and no server-side decorator entities.
 */
@EventBusSubscriber(modid = CozyBeds.MOD_ID, value = Dist.CLIENT)
public final class CozyBedsClient {
    private CozyBedsClient() {}

    public static final ContextKey<DyeColor> BED_COLOR =
            new ContextKey<>(Identifier.fromNamespaceAndPath(CozyBeds.MOD_ID, "bed_color"));

    static final ModelLayerLocation QUILT_LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(CozyBeds.MOD_ID, "quilt"), "main");
    static final ModelLayerLocation LINEN_LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(CozyBeds.MOD_ID, "linen"), "main");

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(QUILT_LAYER, CozyQuiltModel::makeQuilt);
        event.registerLayerDefinition(LINEN_LAYER, CozyQuiltModel::makeLinen);
    }

    @SubscribeEvent
    public static void attachLayers(EntityRenderersEvent.AddLayers event) {
        for (var skin : event.getSkins()) {
            AvatarRenderer<AbstractClientPlayer> renderer = event.getPlayerRenderer(skin);
            if (renderer != null) {
                renderer.addLayer(new CozyQuiltLayer<>(renderer, event.getEntityModels()));
            }
        }

        if (event.getRenderer(EntityTypes.VILLAGER) instanceof VillagerRenderer renderer) {
            renderer.addLayer(new CozyQuiltLayer<>(renderer, event.getEntityModels()));
        }
    }

    @SubscribeEvent
    public static void prepareSleepStates(RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(VillagerRenderer.class, (villager, state) -> {
            applySleepingColorAndVisibility(villager, state, false);
        });

        event.registerAvatarEntityModifier(new AvatarRenderStateModifier() {
            @Override
            public <T extends Avatar & ClientAvatarEntity> void accept(T avatar, AvatarRenderState state) {
                if (avatar instanceof AbstractClientPlayer player) {
                    applySleepingColorAndVisibility(player, state, true);
                }
            }
        });
    }

    private static void applySleepingColorAndVisibility(LivingEntity entity,
                                                        LivingEntityRenderState state,
                                                        boolean player) {
        DyeColor color = resolveBedColor(entity);
        state.setRenderData(BED_COLOR, color);
        if (color == null) return;

        // NeoForge applies these after vanilla model animations have run.
        // The head/headwear are deliberately never hidden.
        for (String part : new String[] {"body", "right_leg", "left_leg"}) {
            state.overrideModelPartVisibility(part, false);
        }

        if (player) {
            for (String part : new String[] {
                    "right_arm", "left_arm", "jacket", "right_sleeve", "left_sleeve",
                    "right_pants", "left_pants"}) {
                state.overrideModelPartVisibility(part, false);
            }
        } else {
            state.overrideModelPartVisibility("arms", false);
        }
    }

    private static DyeColor resolveBedColor(LivingEntity entity) {
        if (!entity.isSleeping()) return null;
        BlockPos pos = entity.getSleepingPos().orElse(null);
        if (pos == null) return null;
        BlockState state = entity.level().getBlockState(pos);
        if (state.getBlock() instanceof BedBlock bed) return bed.getColor();
        return null;
    }
}
