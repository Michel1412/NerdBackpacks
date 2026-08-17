package com.nerdbackpacks.registry;

import com.nerdbackpacks.NerdBackpacks;
import com.nerdbackpacks.content.backpack.BackpackMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, NerdBackpacks.MOD_ID);

    public static final RegistryObject<MenuType<BackpackMenu>> BACKPACK =
            MENUS.register("backpack", () -> IForgeMenuType.create(BackpackMenu::fromNetwork));

    private ModMenus() {
    }
}
