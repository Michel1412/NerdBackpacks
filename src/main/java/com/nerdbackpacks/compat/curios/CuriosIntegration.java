package com.nerdbackpacks.compat.curios;

import com.nerdbackpacks.content.backpack.BackpackItem;
import com.nerdbackpacks.content.backpack.BackpackWear;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Curios-only code. Loaded only through {@link CuriosCompat} when the mod is present.
 */
final class CuriosIntegration {
    private static final List<String> PREFERRED_SLOTS = List.of("back", "chest");

    private CuriosIntegration() {
    }

    static ICapabilityProvider wrapCapabilities(ItemStack stack, ICapabilityProvider inventory) {
        ICapabilityProvider curios = CuriosApi.createCurioProvider(new ICurio() {
            @Override
            public ItemStack getStack() {
                return stack;
            }

            @Override
            public boolean canEquip(SlotContext slotContext) {
                LivingEntity entity = slotContext.entity();
                return BackpackWear.canEquipInCurios(entity, slotContext.identifier(), slotContext.index());
            }

            @Override
            public boolean canEquipFromUse(SlotContext slotContext) {
                // Keep right-click for opening the backpack GUI; equip via drag/drop in Curios.
                return false;
            }
        });

        return new ICapabilityProvider() {
            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                LazyOptional<T> inventoryCap = inventory.getCapability(cap, side);
                if (inventoryCap.isPresent()) {
                    return inventoryCap;
                }
                return curios.getCapability(cap, side);
            }
        };
    }

    static Optional<CuriosCompat.EquippedBackpack> findBackpack(Player player) {
        return CuriosApi.getCuriosInventory(player).map(handler -> {
            for (String slotId : PREFERRED_SLOTS) {
                Optional<SlotResult> found = handler.findCurio(slotId, 0);
                if (found.isPresent()) {
                    ItemStack stack = found.get().stack();
                    if (stack.getItem() instanceof BackpackItem) {
                        return Optional.of(new CuriosCompat.EquippedBackpack(stack, slotId, 0));
                    }
                }
            }

            return handler.findFirstCurio(stack -> stack.getItem() instanceof BackpackItem)
                    .map(result -> new CuriosCompat.EquippedBackpack(
                            result.stack(),
                            result.slotContext().identifier(),
                            result.slotContext().index()
                    ));
        }).orElse(Optional.empty());
    }

    static Optional<ItemStack> getStack(Player player, String slotId, int index) {
        return CuriosApi.getCuriosInventory(player)
                .map(handler -> handler.findCurio(slotId, index).map(SlotResult::stack))
                .orElse(Optional.empty());
    }

    static boolean hasBackpackExcluding(Player player, String excludeSlotId, int excludeIndex) {
        return CuriosApi.getCuriosInventory(player).map(handler -> {
            Map<String, ICurioStacksHandler> curios = handler.getCurios();
            for (Map.Entry<String, ICurioStacksHandler> entry : curios.entrySet()) {
                String slotId = entry.getKey();
                IDynamicStackHandler stacks = entry.getValue().getStacks();
                for (int i = 0; i < stacks.getSlots(); i++) {
                    if (slotId.equals(excludeSlotId) && i == excludeIndex) {
                        continue;
                    }
                    if (stacks.getStackInSlot(i).getItem() instanceof BackpackItem) {
                        return true;
                    }
                }
            }
            return false;
        }).orElse(false);
    }
}
