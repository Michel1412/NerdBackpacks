package com.nerdbackpacks.content.backpack;

import com.nerdbackpacks.compat.curios.CuriosCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Opens backpack GUIs from hand, placed block, vanilla chest armor, or Curios slots.
 */
public final class BackpackOpener {
    public static final byte SOURCE_HAND = 0;
    public static final byte SOURCE_BLOCK = 1;
    public static final byte SOURCE_PLAYER_INV = 2;
    public static final byte SOURCE_CURIOS = 3;

    /** Inventory index for the vanilla chestplate slot. */
    public static final int CHEST_INVENTORY_SLOT = 38;

    private static final Component TITLE = Component.translatable("container.nerdbackpacks.backpack");

    private BackpackOpener() {
    }

    public static void openHand(ServerPlayer player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof BackpackItem backpackItem)) {
            return;
        }
        int slotCount = backpackItem.getTier().getSlotCount();
        NetworkHooks.openScreen(
                player,
                new SimpleMenuProvider(
                        (containerId, inv, p) -> new BackpackMenu(containerId, inv, hand, slotCount),
                        TITLE
                ),
                buf -> {
                    buf.writeByte(SOURCE_HAND);
                    buf.writeEnum(hand);
                    buf.writeVarInt(slotCount);
                }
        );
    }

    public static void openBlock(ServerPlayer player, BackpackBlockEntity backpack) {
        BlockPos pos = backpack.getBlockPos();
        int slotCount = backpack.getTier().getSlotCount();
        NetworkHooks.openScreen(
                player,
                new SimpleMenuProvider(
                        (containerId, inv, p) -> new BackpackMenu(containerId, inv, backpack),
                        TITLE
                ),
                buf -> {
                    buf.writeByte(SOURCE_BLOCK);
                    buf.writeBlockPos(pos);
                    buf.writeVarInt(slotCount);
                }
        );
    }

    public static boolean openEquipped(ServerPlayer player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() instanceof BackpackItem backpackItem) {
            int slotCount = backpackItem.getTier().getSlotCount();
            NetworkHooks.openScreen(
                    player,
                    new SimpleMenuProvider(
                            (containerId, inv, p) ->
                                    new BackpackMenu(containerId, inv, CHEST_INVENTORY_SLOT, slotCount),
                            TITLE
                    ),
                    buf -> {
                        buf.writeByte(SOURCE_PLAYER_INV);
                        buf.writeVarInt(CHEST_INVENTORY_SLOT);
                        buf.writeVarInt(slotCount);
                    }
            );
            return true;
        }

        Optional<CuriosCompat.EquippedBackpack> curios = CuriosCompat.findCuriosBackpack(player);
        if (curios.isEmpty()) {
            return false;
        }

        CuriosCompat.EquippedBackpack equipped = curios.get();
        if (!(equipped.stack().getItem() instanceof BackpackItem backpackItem)) {
            return false;
        }
        int slotCount = backpackItem.getTier().getSlotCount();
        String slotId = equipped.slotId();
        int index = equipped.index();
        NetworkHooks.openScreen(
                player,
                new SimpleMenuProvider(
                        (containerId, inv, p) ->
                                new BackpackMenu(containerId, inv, slotId, index, slotCount),
                        TITLE
                ),
                buf -> {
                    buf.writeByte(SOURCE_CURIOS);
                    buf.writeUtf(slotId);
                    buf.writeVarInt(index);
                    buf.writeVarInt(slotCount);
                }
        );
        return true;
    }

    public static Optional<ItemStack> resolveEquipped(Player player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() instanceof BackpackItem) {
            return Optional.of(chest);
        }
        return CuriosCompat.findCuriosBackpack(player).map(CuriosCompat.EquippedBackpack::stack);
    }
}
