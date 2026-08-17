package com.nerdbackpacks.registry;

import com.nerdbackpacks.NerdBackpacks;
import com.nerdbackpacks.content.backpack.BackpackTier;
import com.nerdbackpacks.registry.factory.ItemFactory;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NerdBackpacks.MOD_ID);

    private static final ItemFactory FACTORY = ItemFactory.items(ITEMS);

    public static final RegistryObject<Item> SACO = FACTORY.simple("saco", Item::new);

    public static final RegistryObject<Item> MOCHILA_1 = FACTORY.backpack(BackpackTier.TIER_1, ModBlocks.MOCHILA_1);
    public static final RegistryObject<Item> MOCHILA_2 = FACTORY.backpack(BackpackTier.TIER_2, ModBlocks.MOCHILA_2);
    public static final RegistryObject<Item> MOCHILA_3 = FACTORY.backpack(BackpackTier.TIER_3, ModBlocks.MOCHILA_3);
    public static final RegistryObject<Item> MOCHILA_4 = FACTORY.backpack(BackpackTier.TIER_4, ModBlocks.MOCHILA_4);
    public static final RegistryObject<Item> MOCHILA_5 = FACTORY.backpack(BackpackTier.TIER_5, ModBlocks.MOCHILA_5);

    private ModItems() {
    }
}
