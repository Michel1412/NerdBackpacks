package com.nerdbackpacks.registry.factory;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Fluent helper for registering blocks and matching block items.
 */
public final class BlockFactory {
    private final DeferredRegister<Block> blocks;
    private final DeferredRegister<Item> items;

    private BlockFactory(DeferredRegister<Block> blocks, DeferredRegister<Item> items) {
        this.blocks = blocks;
        this.items = items;
    }

    public static BlockFactory blocks(DeferredRegister<Block> blocks, DeferredRegister<Item> items) {
        return new BlockFactory(blocks, items);
    }

    public RegistryObject<Block> block(
            String name,
            Supplier<BlockBehaviour.Properties> properties,
            BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory
    ) {
        RegistryObject<Block> block = blocks.register(name, () -> new Block(properties.get()));
        items.register(name, () -> blockItemFactory.apply(block.get(), new Item.Properties()));
        return block;
    }
}
