package yalter.mousetweaks.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Soft-dependency Mouse Tweaks API annotation. Disables the wheel tweak on this screen
 * so the backpack GUI can use scroll for its own inventory window.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MouseTweaksDisableWheelTweak {
}
