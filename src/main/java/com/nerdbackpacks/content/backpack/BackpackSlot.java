package com.nerdbackpacks.content.backpack;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

/**
 * Stable handler-index slot. Scroll only changes {@link #isActive()} and screen Y —
 * menu slot index always matches the backpack inventory index (no remapping / no voids).
 */
public class BackpackSlot extends SlotItemHandler {
    private static final Field SLOT_Y = findSlotY();

    private final BackpackMenu menu;
    private final int baseRow;

    public BackpackSlot(
            BackpackMenu menu,
            IItemHandlerModifiable handler,
            int index,
            int x,
            int baseRow
    ) {
        super(handler, index, x, 18 + baseRow * 18);
        this.menu = menu;
        this.baseRow = baseRow;
    }

    private static Field findSlotY() {
        try {
            Field field = Slot.class.getDeclaredField("y");
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException("Unable to access Slot.y for backpack scrolling", exception);
        }
    }

    public void applyScroll(int scrollRow) {
        int newY = 18 + (baseRow - scrollRow) * 18;
        try {
            SLOT_Y.setInt(this, newY);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException("Unable to update backpack slot Y", exception);
        }
    }

    @Override
    public boolean isActive() {
        int scrollRow = this.menu.getScrollRow();
        return baseRow >= scrollRow && baseRow < scrollRow + this.menu.getVisibleRows();
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return !(stack.getItem() instanceof BackpackItem) && super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return isActive() && super.mayPickup(player);
    }
}
