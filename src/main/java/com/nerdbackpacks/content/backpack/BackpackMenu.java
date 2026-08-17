package com.nerdbackpacks.content.backpack;

import com.nerdbackpacks.compat.curios.CuriosCompat;
import com.nerdbackpacks.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BackpackMenu extends AbstractContainerMenu {
    public static final int MAX_VISIBLE_ROWS = 7; // 63 slots

    private final @Nullable InteractionHand hand;
    private final @Nullable BlockPos blockPos;
    private final int playerInventorySlot;
    private final @Nullable String curiosSlotId;
    private final int curiosIndex;
    private final int slotCount;
    private final int rows;
    private final int visibleRows;
    private final int lockedPlayerSlot;
    private final IItemHandlerModifiable backpackInventory;
    private int scrollRow;

    public BackpackMenu(int containerId, Inventory playerInventory, InteractionHand hand, int slotCount) {
        super(ModMenus.BACKPACK.get(), containerId);
        this.hand = hand;
        this.blockPos = null;
        this.playerInventorySlot = -1;
        this.curiosSlotId = null;
        this.curiosIndex = -1;
        this.slotCount = slotCount;
        this.rows = slotCount / 9;
        this.visibleRows = Math.min(this.rows, MAX_VISIBLE_ROWS);
        this.scrollRow = 0;
        this.lockedPlayerSlot = hand == InteractionHand.MAIN_HAND
                ? playerInventory.selected
                : -1;

        ItemStack backpack = playerInventory.player.getItemInHand(hand);
        boolean client = playerInventory.player.level().isClientSide;
        this.backpackInventory = new BackpackInventory(backpack, slotCount, !client);

        addSlots(playerInventory);
    }

    public BackpackMenu(int containerId, Inventory playerInventory, int playerInventorySlot, int slotCount) {
        super(ModMenus.BACKPACK.get(), containerId);
        this.hand = null;
        this.blockPos = null;
        this.playerInventorySlot = playerInventorySlot;
        this.curiosSlotId = null;
        this.curiosIndex = -1;
        this.slotCount = slotCount;
        this.rows = slotCount / 9;
        this.visibleRows = Math.min(this.rows, MAX_VISIBLE_ROWS);
        this.scrollRow = 0;
        this.lockedPlayerSlot = playerInventorySlot;

        ItemStack backpack = playerInventory.getItem(playerInventorySlot);
        boolean client = playerInventory.player.level().isClientSide;
        this.backpackInventory = new BackpackInventory(backpack, slotCount, !client);

        addSlots(playerInventory);
    }

    public BackpackMenu(
            int containerId,
            Inventory playerInventory,
            String curiosSlotId,
            int curiosIndex,
            int slotCount
    ) {
        super(ModMenus.BACKPACK.get(), containerId);
        this.hand = null;
        this.blockPos = null;
        this.playerInventorySlot = -1;
        this.curiosSlotId = curiosSlotId;
        this.curiosIndex = curiosIndex;
        this.slotCount = slotCount;
        this.rows = slotCount / 9;
        this.visibleRows = Math.min(this.rows, MAX_VISIBLE_ROWS);
        this.scrollRow = 0;
        this.lockedPlayerSlot = -1;

        ItemStack backpack = resolveCuriosStack(playerInventory.player, curiosSlotId, curiosIndex)
                .orElse(ItemStack.EMPTY);
        boolean client = playerInventory.player.level().isClientSide;
        this.backpackInventory = new BackpackInventory(backpack, slotCount, !client);

        addSlots(playerInventory);
    }

    public BackpackMenu(int containerId, Inventory playerInventory, BackpackBlockEntity blockEntity) {
        super(ModMenus.BACKPACK.get(), containerId);
        this.hand = null;
        this.blockPos = blockEntity.getBlockPos();
        this.playerInventorySlot = -1;
        this.curiosSlotId = null;
        this.curiosIndex = -1;
        this.slotCount = blockEntity.getTier().getSlotCount();
        this.rows = slotCount / 9;
        this.visibleRows = Math.min(this.rows, MAX_VISIBLE_ROWS);
        this.scrollRow = 0;
        this.lockedPlayerSlot = -1;
        this.backpackInventory = blockEntity.getInventory();

        addSlots(playerInventory);
    }

    private BackpackMenu(
            int containerId,
            Inventory playerInventory,
            BlockPos pos,
            IItemHandlerModifiable handler,
            int slotCount
    ) {
        super(ModMenus.BACKPACK.get(), containerId);
        this.hand = null;
        this.blockPos = pos;
        this.playerInventorySlot = -1;
        this.curiosSlotId = null;
        this.curiosIndex = -1;
        this.slotCount = slotCount;
        this.rows = slotCount / 9;
        this.visibleRows = Math.min(this.rows, MAX_VISIBLE_ROWS);
        this.scrollRow = 0;
        this.lockedPlayerSlot = -1;
        this.backpackInventory = handler;
        addSlots(playerInventory);
    }

    private void addSlots(Inventory playerInventory) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                int index = col + row * 9;
                this.addSlot(new BackpackSlot(
                        this,
                        backpackInventory,
                        index,
                        8 + col * 18,
                        row
                ));
            }
        }

        int invY = 18 + visibleRows * 18 + 14;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int index = col + row * 9 + 9;
                int x = 8 + col * 18;
                int y = invY + row * 18;
                addPlayerSlot(playerInventory, index, x, y);
            }
        }

        int hotbarY = invY + 58;
        for (int col = 0; col < 9; col++) {
            int x = 8 + col * 18;
            addPlayerSlot(playerInventory, col, x, hotbarY);
        }

        setScrollRow(0);
    }

    public static BackpackMenu fromNetwork(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        byte source = buffer.readByte();
        return switch (source) {
            case BackpackOpener.SOURCE_BLOCK -> {
                BlockPos pos = buffer.readBlockPos();
                int slotCount = buffer.readVarInt();
                BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(pos);
                if (blockEntity instanceof BackpackBlockEntity backpack) {
                    yield new BackpackMenu(containerId, playerInventory, backpack);
                }
                ItemStackHandler fallback = new ItemStackHandler(slotCount);
                yield new BackpackMenu(containerId, playerInventory, pos, fallback, slotCount);
            }
            case BackpackOpener.SOURCE_PLAYER_INV -> {
                int invSlot = buffer.readVarInt();
                int slotCount = buffer.readVarInt();
                yield new BackpackMenu(containerId, playerInventory, invSlot, slotCount);
            }
            case BackpackOpener.SOURCE_CURIOS -> {
                String slotId = buffer.readUtf();
                int curiosIndex = buffer.readVarInt();
                int slotCount = buffer.readVarInt();
                yield new BackpackMenu(containerId, playerInventory, slotId, curiosIndex, slotCount);
            }
            default -> {
                InteractionHand hand = buffer.readEnum(InteractionHand.class);
                int slotCount = buffer.readVarInt();
                yield new BackpackMenu(containerId, playerInventory, hand, slotCount);
            }
        };
    }

    private void addPlayerSlot(Inventory playerInventory, int index, int x, int y) {
        if (index == lockedPlayerSlot) {
            this.addSlot(new LockedSlot(playerInventory, index, x, y));
        } else {
            this.addSlot(new Slot(playerInventory, index, x, y));
        }
    }

    public int getRows() {
        return rows;
    }

    public int getVisibleRows() {
        return visibleRows;
    }

    public int getSlotCount() {
        return slotCount;
    }

    public int getScrollRow() {
        return scrollRow;
    }

    public int getMaxScrollRow() {
        return Math.max(0, rows - visibleRows);
    }

    public boolean needsScroll() {
        return rows > visibleRows;
    }

    public void setScrollRow(int scrollRow) {
        this.scrollRow = Mth.clamp(scrollRow, 0, getMaxScrollRow());
        for (Slot slot : this.slots) {
            if (slot instanceof BackpackSlot backpackSlot) {
                backpackSlot.applyScroll(this.scrollRow);
            }
        }
    }

    public void sortBackpack() {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < backpackInventory.getSlots(); i++) {
            ItemStack stack = backpackInventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                stacks.add(stack.copy());
                backpackInventory.setStackInSlot(i, ItemStack.EMPTY);
            }
        }

        // Merge identical items into full stacks first, then remainder.
        List<ItemStack> merged = new ArrayList<>();
        for (ItemStack stack : stacks) {
            while (!stack.isEmpty()) {
                ItemStack target = null;
                for (ItemStack existing : merged) {
                    if (ItemStack.isSameItemSameTags(existing, stack)
                            && existing.getCount() < existing.getMaxStackSize()) {
                        target = existing;
                        break;
                    }
                }
                if (target == null) {
                    ItemStack next = stack.copy();
                    next.setCount(Math.min(stack.getCount(), stack.getMaxStackSize()));
                    merged.add(next);
                    stack.shrink(next.getCount());
                } else {
                    int move = Math.min(target.getMaxStackSize() - target.getCount(), stack.getCount());
                    target.grow(move);
                    stack.shrink(move);
                }
            }
        }

        // A→Z by registry id, then larger stacks before smaller (64 then 1).
        merged.sort(Comparator
                .comparing((ItemStack stack) -> {
                    Item item = stack.getItem();
                    var key = ForgeRegistries.ITEMS.getKey(item);
                    return key != null ? key.toString() : item.toString();
                })
                .thenComparing(Comparator.comparingInt(ItemStack::getCount).reversed()));

        for (int i = 0; i < merged.size() && i < backpackInventory.getSlots(); i++) {
            backpackInventory.setStackInSlot(i, merged.get(i));
        }
        broadcastChanges();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (blockPos != null) {
            BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
            if (!(blockEntity instanceof BackpackBlockEntity)) {
                return false;
            }
            return Container.stillValidBlockEntity(blockEntity, player);
        }
        if (hand != null) {
            ItemStack stack = player.getItemInHand(hand);
            return isBackpack(stack);
        }
        if (playerInventorySlot >= 0) {
            return isBackpack(player.getInventory().getItem(playerInventorySlot));
        }
        if (curiosSlotId != null) {
            return resolveCuriosStack(player, curiosSlotId, curiosIndex).map(BackpackMenu::isBackpack).orElse(false);
        }
        return false;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack result = stack.copy();

        if (index < slotCount) {
            // Backpack -> player inventory / hotbar
            if (!this.moveItemStackTo(stack, slotCount, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Player -> full backpack (all slots, including those outside the scroll window)
            if (!this.moveItemStackTo(stack, 0, slotCount, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        slot.onTake(player, stack);
        broadcastChanges();
        return result;
    }

    private static boolean isBackpack(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof BackpackItem;
    }

    private static java.util.Optional<ItemStack> resolveCuriosStack(Player player, String slotId, int index) {
        return CuriosCompat.getStack(player, slotId, index);
    }

    private static final class LockedSlot extends Slot {
        private LockedSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPickup(@NotNull Player player) {
            return false;
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return false;
        }
    }
}
