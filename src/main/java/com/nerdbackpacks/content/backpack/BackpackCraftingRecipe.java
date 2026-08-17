package com.nerdbackpacks.content.backpack;

import com.google.gson.JsonObject;
import com.nerdbackpacks.registry.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * Shaped crafting that copies inventory NBT from any backpack ingredient into the result
 * (used by tier upgrades so contents are not lost).
 */
public class BackpackCraftingRecipe extends ShapedRecipe {
    public BackpackCraftingRecipe(
            ResourceLocation id,
            String group,
            CraftingBookCategory category,
            int width,
            int height,
            NonNullList<Ingredient> ingredients,
            ItemStack result
    ) {
        super(id, group, category, width, height, ingredients, result);
    }

    private static BackpackCraftingRecipe from(ShapedRecipe shaped) {
        return new BackpackCraftingRecipe(
                shaped.getId(),
                shaped.getGroup(),
                shaped.category(),
                shaped.getWidth(),
                shaped.getHeight(),
                shaped.getIngredients(),
                shaped.getResultItem(RegistryAccess.EMPTY)
        );
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer container, @NotNull RegistryAccess access) {
        ItemStack result = super.assemble(container, access);
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.getItem() instanceof BackpackItem && stack.hasTag()) {
                result.setTag(stack.getTag().copy());
                break;
            }
        }
        return result;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.BACKPACK_CRAFTING.get();
    }

    public static class Serializer implements RecipeSerializer<BackpackCraftingRecipe> {
        @Override
        public @NotNull BackpackCraftingRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            return from(RecipeSerializer.SHAPED_RECIPE.fromJson(id, json));
        }

        @Override
        public @NotNull BackpackCraftingRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
            return from(RecipeSerializer.SHAPED_RECIPE.fromNetwork(id, buffer));
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull BackpackCraftingRecipe recipe) {
            RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
        }
    }
}
