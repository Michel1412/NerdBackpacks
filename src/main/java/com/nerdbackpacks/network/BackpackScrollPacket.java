package com.nerdbackpacks.network;

import com.nerdbackpacks.content.backpack.BackpackMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record BackpackScrollPacket(int scrollRow) {
    public static void encode(BackpackScrollPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.scrollRow);
    }

    public static BackpackScrollPacket decode(FriendlyByteBuf buffer) {
        return new BackpackScrollPacket(buffer.readVarInt());
    }

    public static void handle(BackpackScrollPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof BackpackMenu menu) {
                menu.setScrollRow(packet.scrollRow());
            }
        });
        context.setPacketHandled(true);
    }
}
