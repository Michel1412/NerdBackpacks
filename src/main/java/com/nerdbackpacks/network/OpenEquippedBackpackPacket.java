package com.nerdbackpacks.network;

import com.nerdbackpacks.content.backpack.BackpackOpener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record OpenEquippedBackpackPacket() {
    public static void encode(OpenEquippedBackpackPacket packet, FriendlyByteBuf buffer) {
    }

    public static OpenEquippedBackpackPacket decode(FriendlyByteBuf buffer) {
        return new OpenEquippedBackpackPacket();
    }

    public static void handle(OpenEquippedBackpackPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                BackpackOpener.openEquipped(player);
            }
        });
        context.setPacketHandled(true);
    }
}
