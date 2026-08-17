package com.nerdbackpacks;

import com.mojang.logging.LogUtils;
import com.nerdbackpacks.registry.ModBlockEntities;
import com.nerdbackpacks.registry.ModBlocks;
import com.nerdbackpacks.registry.ModCreativeTabs;
import com.nerdbackpacks.registry.ModItems;
import com.nerdbackpacks.registry.ModMenus;
import com.nerdbackpacks.registry.ModRecipes;
import com.nerdbackpacks.network.ModNetwork;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(NerdBackpacks.MOD_ID)
public class NerdBackpacks {
    public static final String MOD_ID = "nerdbackpacks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NerdBackpacks() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modBus);
        ModRecipes.SERIALIZERS.register(modBus);
        ModNetwork.register();

        LOGGER.info("NerdBackpacks initialized");
    }
}
