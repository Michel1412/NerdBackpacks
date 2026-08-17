package com.nerdbackpacks.compat.curios;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Soft-dep entry for Curios client renderers.
 */
@OnlyIn(Dist.CLIENT)
public final class CuriosClientCompat {
    private CuriosClientCompat() {
    }

    public static void registerRenderers() {
        if (!CuriosCompat.LOADED) {
            return;
        }
        CuriosClientIntegration.registerRenderers();
    }
}
