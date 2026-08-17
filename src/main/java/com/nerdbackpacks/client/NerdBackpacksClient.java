package com.nerdbackpacks.client;

import com.nerdbackpacks.NerdBackpacks;
import com.nerdbackpacks.client.render.BackpackChestLayer;
import com.nerdbackpacks.compat.curios.CuriosClientCompat;
import com.nerdbackpacks.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = NerdBackpacks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class NerdBackpacksClient {
    private NerdBackpacksClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.BACKPACK.get(), BackpackScreen::new);
            CuriosClientCompat.registerRenderers();
        });
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin);
            if (renderer != null) {
                renderer.addLayer(new BackpackChestLayer<>(renderer));
            }
        }
    }
}
