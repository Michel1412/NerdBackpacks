package com.nerdbackpacks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nerdbackpacks.content.backpack.BackpackItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Renders a backpack equipped in the vanilla chestplate slot on the player's back.
 */
@OnlyIn(Dist.CLIENT)
public class BackpackChestLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public BackpackChestLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffer,
            int packedLight,
            @NotNull T entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof BackpackItem)) {
            return;
        }

        BackpackOnBodyRenderer.render(entity, chest, getParentModel(), poseStack, buffer, packedLight);
    }
}
