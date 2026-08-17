package com.nerdbackpacks.registry;

import com.nerdbackpacks.NerdBackpacks;
import com.nerdbackpacks.content.backpack.BackpackCraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, NerdBackpacks.MOD_ID);

    public static final RegistryObject<RecipeSerializer<BackpackCraftingRecipe>> BACKPACK_CRAFTING =
            SERIALIZERS.register("backpack_crafting", BackpackCraftingRecipe.Serializer::new);

    private ModRecipes() {
    }
}
