package com.nerdbackpacks.network;

import com.nerdbackpacks.NerdBackpacks;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class ModNetwork {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(NerdBackpacks.MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static int packetId;

    private ModNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(
                packetId++,
                BackpackScrollPacket.class,
                BackpackScrollPacket::encode,
                BackpackScrollPacket::decode,
                BackpackScrollPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        CHANNEL.registerMessage(
                packetId++,
                BackpackSortPacket.class,
                BackpackSortPacket::encode,
                BackpackSortPacket::decode,
                BackpackSortPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        CHANNEL.registerMessage(
                packetId++,
                OpenEquippedBackpackPacket.class,
                OpenEquippedBackpackPacket::encode,
                OpenEquippedBackpackPacket::decode,
                OpenEquippedBackpackPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
    }
}
