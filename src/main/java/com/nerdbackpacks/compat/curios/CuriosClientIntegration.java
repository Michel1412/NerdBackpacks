package com.nerdbackpacks.compat.curios;

import com.nerdbackpacks.client.render.BackpackOnBodyRenderer;
import com.nerdbackpacks.registry.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/**
 * Curios client-only registration and renderer. Loaded only when Curios is present.
 */
@OnlyIn(Dist.CLIENT)
final class CuriosClientIntegration {
    private CuriosClientIntegration() {
    }

    static void registerRenderers() {
        ICurioRenderer renderer = new BackpackCurioRenderer();
        register(ModItems.MOCHILA_1, renderer);
        register(ModItems.MOCHILA_2, renderer);
        register(ModItems.MOCHILA_3, renderer);
        register(ModItems.MOCHILA_4, renderer);
        register(ModItems.MOCHILA_5, renderer);
    }

    private static void register(RegistryObject<Item> item, ICurioRenderer renderer) {
        CuriosRendererRegistry.register(item.get(), () -> renderer);
    }

    private static final class BackpackCurioRenderer implements ICurioRenderer {
        @Override
        public <T extends LivingEntity, M extends EntityModel<T>> void render(
                ItemStack stack,
                SlotContext slotContext,
                PoseStack poseStack,
                RenderLayerParent<T, M> renderLayerParent,
                MultiBufferSource buffer,
                int light,
                float limbSwing,
                float limbSwingAmount,
                float partialTicks,
                float ageInTicks,
                float netHeadYaw,
                float headPitch
        ) {
            M model = renderLayerParent.getModel();
            if (!(model instanceof HumanoidModel<?> humanoid)) {
                return;
            }
            BackpackOnBodyRenderer.render(slotContext.entity(), stack, humanoid, poseStack, buffer, light);
        }
    }
}
