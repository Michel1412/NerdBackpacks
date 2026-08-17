package com.nerdbackpacks.compat.jei;

import com.nerdbackpacks.NerdBackpacks;
import com.nerdbackpacks.content.backpack.BackpackCraftingRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

@JeiPlugin
public class NerdBackpacksJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation(NerdBackpacks.MOD_ID, "jei");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        RecipeManager recipeManager = level.getRecipeManager();
        List<CraftingRecipe> backpackRecipes = recipeManager.getAllRecipesFor(RecipeType.CRAFTING).stream()
                .filter(recipe -> recipe instanceof BackpackCraftingRecipe)
                .map(recipe -> (CraftingRecipe) recipe)
                .toList();

        if (!backpackRecipes.isEmpty()) {
            registration.addRecipes(RecipeTypes.CRAFTING, backpackRecipes);
        }
    }
}
