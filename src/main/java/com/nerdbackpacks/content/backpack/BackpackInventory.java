package com.nerdbackpacks.content.backpack;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

/**
 * ItemStack-backed inventory. Persists to the backpack stack NBT on the server only
 * (client writes caused scrolled-out slots to be wiped from NBT and void items).
 */
public class BackpackInventory extends ItemStackHandler {
    public static final String INVENTORY_TAG = "Inventory";

    private final ItemStack stack;
    private final boolean persistToItem;

    public BackpackInventory(ItemStack stack, int size) {
        this(stack, size, true);
    }

    public BackpackInventory(ItemStack stack, int size, boolean persistToItem) {
        super(size);
        this.stack = stack;
        this.persistToItem = persistToItem;
        loadFromStack();
    }

    private void loadFromStack() {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(INVENTORY_TAG)) {
            return;
        }
        CompoundTag inventoryTag = tag.getCompound(INVENTORY_TAG);
        ListTag items = inventoryTag.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < items.size(); i++) {
            CompoundTag itemTag = items.getCompound(i);
            int slot = itemTag.getInt("Slot");
            if (slot >= 0 && slot < getSlots()) {
                stacks.set(slot, ItemStack.of(itemTag));
            }
        }
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (persistToItem) {
            stack.getOrCreateTag().put(INVENTORY_TAG, serializeNBT());
        }
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack itemStack) {
        return !(itemStack.getItem() instanceof BackpackItem);
    }
}
