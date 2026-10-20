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
 * Sculpted in 1/16-block pixels in the same local coordinate system as the
 * sleeping humanoid. The vanilla sleeping transform rotates the quilt with
 * the sleeper, keeping it aligned to all bed orientations.
 *
 * Unlike the old 0.1.0 version, no full-block geometry is involved:
 * the fabric thickness is between 1.2 and 3.0 model pixels.
 */
public final class CozyQuiltModel extends EntityModel<LivingEntityRenderState> {
    public CozyQuiltModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition makeQuilt() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Gently raised around the torso, flatter toward the feet.
        root.addOrReplaceChild("bottom",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-1.0f, -8.0f, -7.2f, 2.0f, 8.5f, 14.4f),
                PartPose.ZERO);
        root.addOrReplaceChild("middle",
                CubeListBuilder.create().texOffs(0, 23)
                        .addBox(-1.55f, -15.5f, -7.3f, 3.1f, 7.5f, 14.6f),
                PartPose.ZERO);
        root.addOrReplaceChild("chest",
                CubeListBuilder.create().texOffs(0, 43)
                        .addBox(-1.3f, -19.2f, -7.35f, 2.6f, 3.7f, 14.7f),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static LayerDefinition makeLinen() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Folded white edge, at the head end of the duvet.
        root.addOrReplaceChild("fold",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-1.40f, -19.4f, -7.45f, 2.8f, 1.3f, 14.9f),
                PartPose.ZERO);
        // A separate slim cushion. The head of the sleeper remains uncovered.
        root.addOrReplaceChild("pillow",
                CubeListBuilder.create().texOffs(0, 20)
                        .addBox(-1.9f, -25.1f, -6.1f, 3.8f, 3.1f, 12.2f),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }
}
