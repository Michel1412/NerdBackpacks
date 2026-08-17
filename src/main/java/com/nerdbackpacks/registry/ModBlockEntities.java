package com.nerdbackpacks.registry;

import com.nerdbackpacks.NerdBackpacks;
import com.nerdbackpacks.content.backpack.BackpackBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NerdBackpacks.MOD_ID);

    public static final RegistryObject<BlockEntityType<BackpackBlockEntity>> BACKPACK =
            BLOCK_ENTITIES.register("backpack", () ->
                    BlockEntityType.Builder.of(
                            BackpackBlockEntity::new,
                            ModBlocks.MOCHILA_1.get(),
                            ModBlocks.MOCHILA_2.get(),
                            ModBlocks.MOCHILA_3.get(),
                            ModBlocks.MOCHILA_4.get(),
                            ModBlocks.MOCHILA_5.get()
                    ).build(null));

    private ModBlockEntities() {
    }
}
