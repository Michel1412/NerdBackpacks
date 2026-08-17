package com.nerdbackpacks.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.nerdbackpacks.NerdBackpacks;
import com.nerdbackpacks.network.ModNetwork;
import com.nerdbackpacks.network.OpenEquippedBackpackPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public final class ModKeyMappings {
    public static final KeyMapping OPEN_BACKPACK = new KeyMapping(
            "key.nerdbackpacks.open_backpack",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories.nerdbackpacks"
    );

    private ModKeyMappings() {
    }

    @Mod.EventBusSubscriber(modid = NerdBackpacks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class Register {
        private Register() {
        }

        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(OPEN_BACKPACK);
        }
    }

    @Mod.EventBusSubscriber(modid = NerdBackpacks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class Handler {
        private Handler() {
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null || minecraft.screen != null) {
                return;
            }
            while (OPEN_BACKPACK.consumeClick()) {
                ModNetwork.CHANNEL.send(PacketDistributor.SERVER.noArg(), new OpenEquippedBackpackPacket());
            }
        }
    }
}
