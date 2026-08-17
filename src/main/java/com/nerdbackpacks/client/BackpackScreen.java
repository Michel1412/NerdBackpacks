package com.nerdbackpacks.client;

import com.nerdbackpacks.content.backpack.BackpackMenu;
import com.nerdbackpacks.network.BackpackScrollPacket;
import com.nerdbackpacks.network.BackpackSortPacket;
import com.nerdbackpacks.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import yalter.mousetweaks.api.IMTModGuiContainer3;
import yalter.mousetweaks.api.MouseTweaksDisableWheelTweak;

/**
 * Vanilla chest-style GUI. Caps visible backpack rows at {@link BackpackMenu#MAX_VISIBLE_ROWS}
 * and scrolls the rest (GuiScale-friendly).
 */
@MouseTweaksDisableWheelTweak
public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> implements IMTModGuiContainer3 {
    private static final ResourceLocation GENERIC_54 =
            new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

    private static final int SCROLLBAR_X = 169;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SLOT_AREA_TOP = 17;
    private static final int SLOT_AREA_LEFT = 7;

    private final int visibleRows;
    private boolean scrolling;

    public BackpackScreen(BackpackMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.visibleRows = menu.getVisibleRows();
        this.imageHeight = 114 + this.visibleRows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;

        guiGraphics.blit(GENERIC_54, left, top, 0, 0, this.imageWidth, 17);
        for (int row = 0; row < this.visibleRows; row++) {
            guiGraphics.blit(GENERIC_54, left, top + 17 + row * 18, 0, 17, this.imageWidth, 18);
        }
        guiGraphics.blit(GENERIC_54, left, top + 17 + this.visibleRows * 18, 0, 126, this.imageWidth, 96);

        if (this.menu.needsScroll()) {
            renderScrollbar(guiGraphics, left, top);
        }
    }

    private void renderScrollbar(GuiGraphics guiGraphics, int left, int top) {
        int trackX = left + SCROLLBAR_X;
        int trackY = top + SLOT_AREA_TOP;
        int trackHeight = this.visibleRows * 18;

        guiGraphics.fill(trackX, trackY, trackX + SCROLLBAR_WIDTH, trackY + trackHeight, 0xFF000000);
        guiGraphics.fill(trackX + 1, trackY + 1, trackX + SCROLLBAR_WIDTH - 1, trackY + trackHeight - 1, 0xFF373737);

        int thumbHeight = getThumbHeight(trackHeight);
        int thumbY = trackY + getThumbOffset(trackHeight, thumbHeight);
        guiGraphics.fill(trackX + 1, thumbY, trackX + SCROLLBAR_WIDTH - 1, thumbY + thumbHeight, 0xFF8B8B8B);
        guiGraphics.fill(trackX + 1, thumbY, trackX + SCROLLBAR_WIDTH - 2, thumbY + thumbHeight - 1, 0xFFC6C6C6);
    }

    private int getThumbHeight(int trackHeight) {
        int totalRows = this.menu.getRows();
        return Mth.clamp(trackHeight * this.visibleRows / totalRows, 8, trackHeight);
    }

    private int getThumbOffset(int trackHeight, int thumbHeight) {
        int maxScroll = this.menu.getMaxScrollRow();
        if (maxScroll == 0) {
            return 0;
        }
        return this.menu.getScrollRow() * (trackHeight - thumbHeight) / maxScroll;
    }

    private void setScrollRow(int scrollRow) {
        int clamped = Mth.clamp(scrollRow, 0, this.menu.getMaxScrollRow());
        if (clamped == this.menu.getScrollRow()) {
            return;
        }
        this.menu.setScrollRow(clamped);
        ModNetwork.CHANNEL.sendToServer(new BackpackScrollPacket(clamped));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.menu.needsScroll() && isOverBackpackArea(mouseX, mouseY)) {
            setScrollRow(this.menu.getScrollRow() - (int) Math.signum(delta));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 2 && isOverBackpackArea(mouseX, mouseY)) {
            ModNetwork.CHANNEL.sendToServer(new BackpackSortPacket());
            return true;
        }
        if (button == 0 && this.menu.needsScroll() && isOverScrollbar(mouseX, mouseY)) {
            this.scrolling = true;
            updateScrollFromMouse(mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrolling && this.menu.needsScroll()) {
            updateScrollFromMouse(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.scrolling = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void updateScrollFromMouse(double mouseY) {
        int trackY = this.topPos + SLOT_AREA_TOP;
        int trackHeight = this.visibleRows * 18;
        int thumbHeight = getThumbHeight(trackHeight);
        int maxScroll = this.menu.getMaxScrollRow();
        if (maxScroll <= 0) {
            return;
        }

        double usable = trackHeight - thumbHeight;
        double relative = mouseY - trackY - thumbHeight / 2.0;
        int scroll = Mth.clamp((int) Math.round(relative * maxScroll / usable), 0, maxScroll);
        setScrollRow(scroll);
    }

    private boolean isOverBackpackArea(double mouseX, double mouseY) {
        int left = this.leftPos + SLOT_AREA_LEFT;
        int top = this.topPos + SLOT_AREA_TOP;
        return mouseX >= left
                && mouseX < left + 162 + SCROLLBAR_WIDTH + 2
                && mouseY >= top
                && mouseY < top + this.visibleRows * 18;
    }

    private boolean isOverScrollbar(double mouseX, double mouseY) {
        int left = this.leftPos + SCROLLBAR_X;
        int top = this.topPos + SLOT_AREA_TOP;
        return mouseX >= left
                && mouseX < left + SCROLLBAR_WIDTH
                && mouseY >= top
                && mouseY < top + this.visibleRows * 18;
    }

    @Override
    public boolean MT_isMouseTweaksDisabled() {
        return false;
    }

    @Override
    public boolean MT_isWheelTweakDisabled() {
        return true;
    }

    @Override
    public AbstractContainerMenu MT_getContainer() {
        return this.menu;
    }

    @Override
    public Slot MT_getSlotUnderMouse(double mouseX, double mouseY) {
        for (Slot slot : this.menu.slots) {
            if (!slot.isActive()) {
                continue;
            }
            if (this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
                return slot;
            }
        }
        return null;
    }

    @Override
    public boolean MT_isCraftingOutput(Slot slot) {
        return false;
    }

    @Override
    public boolean MT_isIgnored(Slot slot) {
        return false;
    }

    @Override
    public boolean MT_disableRMBDraggingFunctionality() {
        this.isQuickCrafting = false;
        return false;
    }
}
