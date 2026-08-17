package com.nerdbackpacks.content.backpack;

import com.nerdbackpacks.compat.curios.CuriosCompat;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Ensures at most one backpack is worn (vanilla chestplate and/or Curios).
 */
public final class BackpackWear {
    private BackpackWear() {
    }

    public static boolean isBackpack(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof BackpackItem;
    }

    /** True if chest or Curios already has a backpack. */
    public static boolean hasEquippedBackpack(LivingEntity entity) {
        if (isBackpack(entity.getItemBySlot(EquipmentSlot.CHEST))) {
            return true;
        }
        if (entity instanceof Player player) {
            return CuriosCompat.findCuriosBackpack(player).isPresent();
        }
        return false;
    }

    /** Equipping into the vanilla chest slot: only blocked if Curios already has a backpack. */
    public static boolean canEquipInChest(LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return true;
        }
        return !CuriosCompat.findCuriosBackpack(player).isPresent();
    }

    /**
     * Equipping into a Curios slot: blocked if chest has a backpack, or another Curios
     * slot (not this one) already has a backpack.
     */
    public static boolean canEquipInCurios(LivingEntity entity, String slotId, int index) {
        if (isBackpack(entity.getItemBySlot(EquipmentSlot.CHEST))) {
            return false;
        }
        if (!(entity instanceof Player player)) {
            return true;
        }
        return !CuriosCompat.hasCuriosBackpackExcluding(player, slotId, index);
    }
}
