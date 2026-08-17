package com.nerdbackpacks.registry;

import com.nerdbackpacks.NerdBackpacks;
import com.nerdbackpacks.content.backpack.BackpackBlock;
import com.nerdbackpacks.content.backpack.BackpackTier;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, NerdBackpacks.MOD_ID);

    public static final RegistryObject<Block> MOCHILA_1 =
            BLOCKS.register(BackpackTier.TIER_1.getId(), () -> new BackpackBlock(BackpackTier.TIER_1));
    public static final RegistryObject<Block> MOCHILA_2 =
            BLOCKS.register(BackpackTier.TIER_2.getId(), () -> new BackpackBlock(BackpackTier.TIER_2));
    public static final RegistryObject<Block> MOCHILA_3 =
            BLOCKS.register(BackpackTier.TIER_3.getId(), () -> new BackpackBlock(BackpackTier.TIER_3));
    public static final RegistryObject<Block> MOCHILA_4 =
            BLOCKS.register(BackpackTier.TIER_4.getId(), () -> new BackpackBlock(BackpackTier.TIER_4));
    public static final RegistryObject<Block> MOCHILA_5 =
            BLOCKS.register(BackpackTier.TIER_5.getId(), () -> new BackpackBlock(BackpackTier.TIER_5));

    private ModBlocks() {
    }
}
