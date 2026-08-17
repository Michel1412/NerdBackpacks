package com.nerdbackpacks.compat.jade;

import com.nerdbackpacks.NerdBackpacks;
import com.nerdbackpacks.content.backpack.BackpackBlock;
import com.nerdbackpacks.content.backpack.BackpackBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

@WailaPlugin
public class NerdBackpacksJadePlugin implements IWailaPlugin {
    public static final ResourceLocation BACKPACK = new ResourceLocation(NerdBackpacks.MOD_ID, "backpack");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(BackpackProvider.INSTANCE, BackpackBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(BackpackProvider.INSTANCE, BackpackBlock.class);
    }

    public enum BackpackProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
        INSTANCE;

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            CompoundTag data = accessor.getServerData();
            if (!data.contains("Items", Tag.TAG_LIST)) {
                return;
            }

            if (!accessor.getPlayer().isShiftKeyDown()) {
                tooltip.add(Component.translatable("nerdbackpacks.jade.hold_shift"));
                return;
            }

            ListTag items = data.getList("Items", Tag.TAG_COMPOUND);
            if (items.isEmpty()) {
                tooltip.add(Component.translatable("nerdbackpacks.jade.empty"));
                return;
            }

            IElementHelper helper = tooltip.getElementHelper();
            boolean first = true;
            for (int i = 0; i < items.size(); i++) {
                ItemStack stack = ItemStack.of(items.getCompound(i));
                if (stack.isEmpty()) {
                    continue;
                }
                IElement icon = helper.item(stack);
                if (first) {
                    tooltip.add(icon);
                    first = false;
                } else {
                    tooltip.append(icon);
                }
            }
        }

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            if (!(accessor.getBlockEntity() instanceof BackpackBlockEntity backpack)) {
                return;
            }
            ListTag items = new ListTag();
            var inventory = backpack.getInventory();
            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    items.add(stack.save(new CompoundTag()));
                }
            }
            data.put("Items", items);
        }

        @Override
        public ResourceLocation getUid() {
            return BACKPACK;
        }
    }
}
