package com.nerdbackpacks.network;

import com.nerdbackpacks.content.backpack.BackpackMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record BackpackSortPacket() {
    public static void encode(BackpackSortPacket packet, FriendlyByteBuf buffer) {
    }

    public static BackpackSortPacket decode(FriendlyByteBuf buffer) {
        return new BackpackSortPacket();
    }

    public static void handle(BackpackSortPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof BackpackMenu menu) {
                menu.sortBackpack();
            }
        });
        context.setPacketHandled(true);
    }
}
