package hantonik.anvilapi.utils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;

public final class AARecipeHelper implements ServerLifecycleEvents.StartDataPackReload, ServerLifecycleEvents.SyncDataPackContents {
    private static RecipeManager MANAGER;

    @Override
    public void startDataPackReload(MinecraftServer server, CloseableResourceManager manager) {
        MANAGER = server.getRecipeManager();
    }

    @Override
    public void onSyncDataPackContents(ServerPlayer player, boolean joined) {
        MANAGER = player.level().getRecipeManager();
    }

    public static RecipeManager getRecipeManager() {
        if (MANAGER.recipes instanceof ImmutableMap) {
            MANAGER.recipes = Maps.newHashMap(MANAGER.recipes);
            MANAGER.recipes.replaceAll((type, recipes) -> Maps.newHashMap(MANAGER.recipes.get(type)));
        }

        return MANAGER;
    }

    public static Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> getRecipes() {
        return getRecipeManager().recipes;
    }

    public static <C extends Container, T extends Recipe<C>> Map<ResourceLocation, T> getRecipes(RecipeType<T> type) {
        return getRecipeManager().byType(type);
    }

    public static void addRecipe(Recipe<?> recipe) {
        getRecipeManager().recipes.computeIfAbsent(recipe.getType(), type -> Maps.newHashMap()).put(recipe.getId(), recipe);
    }

    public static void addRecipe(RecipeType<?> recipeType, Recipe<?> recipe) {
        getRecipeManager().recipes.computeIfAbsent(recipeType, type -> Maps.newHashMap()).put(recipe.getId(), recipe);
    }
}
