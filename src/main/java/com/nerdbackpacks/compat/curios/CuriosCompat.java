package com.nerdbackpacks.compat.curios;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Soft dependency entry point for Curios. Never references Curios classes directly
 * so the game loads fine when Curios is absent.
 */
public final class CuriosCompat {
    public static final boolean LOADED = ModList.get().isLoaded("curios");

    private CuriosCompat() {
    }

    public static ICapabilityProvider wrapCapabilities(ItemStack stack, @Nullable CompoundTag nbt, ICapabilityProvider inventory) {
        if (!LOADED) {
            return inventory;
        }
        return CuriosIntegration.wrapCapabilities(stack, inventory);
    }

    public static Optional<EquippedBackpack> findCuriosBackpack(Player player) {
        if (!LOADED) {
            return Optional.empty();
        }
        return CuriosIntegration.findBackpack(player);
    }

    public static Optional<ItemStack> getStack(Player player, String slotId, int index) {
        if (!LOADED) {
            return Optional.empty();
        }
        return CuriosIntegration.getStack(player, slotId, index);
    }

    /** True if any Curios slot (except the given one) already has a backpack. */
    public static boolean hasCuriosBackpackExcluding(Player player, String slotId, int index) {
        if (!LOADED) {
            return false;
        }
        return CuriosIntegration.hasBackpackExcluding(player, slotId, index);
    }

    public record EquippedBackpack(ItemStack stack, String slotId, int index) {
    }
}
