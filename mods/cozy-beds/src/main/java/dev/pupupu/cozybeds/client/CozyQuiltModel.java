package dev.pupupu.cozybeds.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/**
 * Minecraft-style quilt panels, authored directly in 1/16-block units.
 * Axis X: left/right bed width (14 pixels instead of bed's full 16).
 * Axis Y: small elevation above existing vanilla mattress.
 * Axis Z: travel from head-center toward bed foot (5.5px to 23.4px).
 *
 * The additional cushion from version 0.2.0 was removed, because a vanilla
 * bed already has a pillow: that extra cube intersected the sleeper's head.
 */
public final class CozyQuiltModel extends EntityModel<LivingEntityRenderState> {
    public CozyQuiltModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition makeQuilt() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Continuous thin sheet: the surrounding mattress remains visible.
        root.addOrReplaceChild("blanket",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-7.05F, 0.45F, 5.55F, 14.10F, 1.35F, 17.90F),
                PartPose.ZERO);
        // Gentle silhouette of a covered torso, tapering toward the feet.
        root.addOrReplaceChild("chest",
                CubeListBuilder.create().texOffs(0, 23)
                        .addBox(-6.93F, 1.77F, 6.8F, 13.86F, 1.27F, 4.6F),
                PartPose.ZERO);
        root.addOrReplaceChild("middle",
                CubeListBuilder.create().texOffs(0, 41)
                        .addBox(-6.93F, 1.75F, 11.4F, 13.86F, 0.68F, 5.7F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static LayerDefinition makeFold() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("folded_edge",
                CubeListBuilder.create().texOffs(0, 2)
                        .addBox(-7.12F, 1.75F, 5.6F, 14.24F, 1.08F, 1.05F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static LayerDefinition makeStitch() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        // Fine dark piping to distinguish a white quilt from a white mattress.
        root.addOrReplaceChild("seam_left",
                CubeListBuilder.create().texOffs(0, 10)
                        .addBox(-7.10F, 1.79F, 6.65F, 0.31F, 0.12F, 16.7F),
                PartPose.ZERO);
        root.addOrReplaceChild("seam_right",
                CubeListBuilder.create().texOffs(0, 10)
                        .addBox(6.79F, 1.79F, 6.65F, 0.31F, 0.12F, 16.7F),
                PartPose.ZERO);
        root.addOrReplaceChild("seam_foot",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-7.1F, 1.80F, 23.2F, 14.2F, 0.10F, 0.3F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }
}
