package com.nerdbackpacks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Shared worn-backpack draw path for the vanilla chest layer and Curios.
 */
@OnlyIn(Dist.CLIENT)
public final class BackpackOnBodyRenderer {
    private BackpackOnBodyRenderer() {
    }

    public static void render(
            LivingEntity entity,
            ItemStack stack,
            HumanoidModel<?> humanoid,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        if (stack.isEmpty() || entity.isInvisible()) {
            return;
        }

        poseStack.pushPose();
        humanoid.body.translateAndRotate(poseStack);

        // Sit on the back face of the torso (+Z in body space).
        poseStack.translate(
                BackpackBodyPose.BODY_OFFSET_X,
                BackpackBodyPose.BODY_OFFSET_Y,
                BackpackBodyPose.BODY_OFFSET_Z
        );
        poseStack.mulPose(Axis.XP.rotationDegrees(BackpackBodyPose.ROTATION_X));
        poseStack.mulPose(Axis.YP.rotationDegrees(BackpackBodyPose.ROTATION_Y));
        poseStack.mulPose(Axis.ZP.rotationDegrees(BackpackBodyPose.ROTATION_Z));
        poseStack.scale(BackpackBodyPose.SCALE, BackpackBodyPose.SCALE, BackpackBodyPose.SCALE);

        // Block models are drawn in 0–16 space with origin at a corner. Shift so the
        // north (player-facing) face starts at the back plane instead of centering the mesh.
        poseStack.translate(
                -0.5F,
                -BackpackBodyPose.modelBottomY() + BackpackBodyPose.MODEL_Y_LIFT,
                -BackpackBodyPose.modelAttachZ()
        );

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderStatic(
                entity,
                stack,
                ItemDisplayContext.NONE,
                false,
                poseStack,
                buffer,
                entity.level(),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                entity.getId() + ItemDisplayContext.NONE.ordinal()
        );

        poseStack.popPose();
    }
}
