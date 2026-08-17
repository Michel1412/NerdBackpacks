package yalter.mousetweaks.api;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

/**
 * Soft-dependency Mouse Tweaks API (embedded). Present so Mouse Tweaks can detect
 * custom container screens when the mod is installed; harmless when it is not.
 *
 * <p>For Forge 1.20.1 Mouse Tweaks also honors {@link MouseTweaksDisableWheelTweak}
 * on {@code AbstractContainerScreen} subclasses via its default handler.
 */
public interface IMTModGuiContainer3 {
    boolean MT_isMouseTweaksDisabled();

    boolean MT_isWheelTweakDisabled();

    AbstractContainerMenu MT_getContainer();

    Slot MT_getSlotUnderMouse(double mouseX, double mouseY);

    boolean MT_isCraftingOutput(Slot slot);

    boolean MT_isIgnored(Slot slot);

    boolean MT_disableRMBDraggingFunctionality();
}
