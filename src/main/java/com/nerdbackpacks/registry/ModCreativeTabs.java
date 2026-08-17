package com.nerdbackpacks.registry;

import com.nerdbackpacks.NerdBackpacks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NerdBackpacks.MOD_ID);

    public static final RegistryObject<CreativeModeTab> NERD_BACKPACKS =
            CREATIVE_MODE_TABS.register("nerd_backpacks", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.nerdbackpacks"))
                    .icon(() -> new ItemStack(ModItems.MOCHILA_1.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.SACO.get());
                        output.accept(ModItems.MOCHILA_1.get());
                        output.accept(ModItems.MOCHILA_2.get());
                        output.accept(ModItems.MOCHILA_3.get());
                        output.accept(ModItems.MOCHILA_4.get());
                        output.accept(ModItems.MOCHILA_5.get());
                    })
                    .build());

    private ModCreativeTabs() {
    }
}
