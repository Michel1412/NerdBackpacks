package com.nerdbackpacks.registry.factory;

import com.nerdbackpacks.content.backpack.BackpackItem;
import com.nerdbackpacks.content.backpack.BackpackTier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Fluent helper for registering items on a {@link DeferredRegister}.
 */
public final class ItemFactory {
    private final DeferredRegister<Item> items;

    private ItemFactory(DeferredRegister<Item> items) {
        this.items = items;
    }

    public static ItemFactory items(DeferredRegister<Item> items) {
        return new ItemFactory(items);
    }

    public RegistryObject<Item> backpack(BackpackTier tier, Supplier<Block> block) {
        return items.register(tier.getId(), () ->
                new BackpackItem(block.get(), tier, new Item.Properties().stacksTo(1)));
    }

    public RegistryObject<Item> simple(String name, Function<Item.Properties, Item> factory) {
        return items.register(name, () -> factory.apply(new Item.Properties()));
    }
}
