package com.nerdbackpacks.content.backpack;

import com.nerdbackpacks.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BackpackBlockEntity extends BlockEntity {
    private final BackpackTier tier;
    private final ItemStackHandler inventory;
    private LazyOptional<IItemHandler> inventoryOptional = LazyOptional.empty();

    public BackpackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BACKPACK.get(), pos, state);
        if (state.getBlock() instanceof BackpackBlock backpackBlock) {
            this.tier = backpackBlock.getTier();
        } else {
            this.tier = BackpackTier.TIER_1;
        }
        this.inventory = createHandler(this.tier.getSlotCount());
    }

    public BackpackBlockEntity(BackpackTier tier, BlockPos pos, BlockState state) {
        super(ModBlockEntities.BACKPACK.get(), pos, state);
        this.tier = tier;
        this.inventory = createHandler(tier.getSlotCount());
    }

    private ItemStackHandler createHandler(int size) {
        return new ItemStackHandler(size) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return !(stack.getItem() instanceof BackpackItem);
            }
        };
    }

    public BackpackTier getTier() {
        return tier;
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public void loadFromItem(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(BackpackInventory.INVENTORY_TAG)) {
            return;
        }
        CompoundTag inventoryTag = tag.getCompound(BackpackInventory.INVENTORY_TAG);
        ListTag items = inventoryTag.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < items.size(); i++) {
            CompoundTag itemTag = items.getCompound(i);
            int slot = itemTag.getInt("Slot");
            if (slot >= 0 && slot < inventory.getSlots()) {
                inventory.setStackInSlot(slot, ItemStack.of(itemTag));
            }
        }
    }

    public void saveToItem(ItemStack stack) {
        stack.getOrCreateTag().put(BackpackInventory.INVENTORY_TAG, inventory.serializeNBT());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        inventoryOptional = LazyOptional.of(() -> inventory);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryOptional.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(BackpackInventory.INVENTORY_TAG, inventory.serializeNBT());
        tag.putString("Tier", tier.getId());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains(BackpackInventory.INVENTORY_TAG)) {
            inventory.deserializeNBT(tag.getCompound(BackpackInventory.INVENTORY_TAG));
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
