package com.lowdragmc.multiblocked.api.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.multiblocked.Multiblocked;
import com.lowdragmc.multiblocked.api.recipe.Recipe;
import com.lowdragmc.multiblocked.api.recipe.RecipeMap;
import net.minecraft.util.JSONUtils;

import java.lang.reflect.Type;

public class RecipeMapTypeAdapter implements JsonSerializer<RecipeMap>,
        JsonDeserializer<RecipeMap> {
    public static final RecipeMapTypeAdapter INSTANCE = new RecipeMapTypeAdapter();

    @Override
    public RecipeMap deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject json = (JsonObject) jsonElement;
        RecipeMap recipeMap = new RecipeMap(json.get("name").getAsString());
        recipeMap.progressTexture = new ResourceTexture(json.get("progressTexture").getAsString());
        recipeMap.fuelTexture = new ResourceTexture(JSONUtils.getAsString(json, "fuelTexture", recipeMap.fuelTexture.imageLocation.toString()));
        for (JsonElement recipe : json.get("recipes").getAsJsonArray()) {
            recipeMap.addRecipe(Multiblocked.GSON.fromJson(recipe, Recipe.class));
        }
        if (json.has("fuelRecipes")) {
            for (JsonElement recipe : json.get("fuelRecipes").getAsJsonArray()) {
                recipeMap.addFuelRecipe(Multiblocked.GSON.fromJson(recipe, Recipe.class));
            }
        }
        return recipeMap;
    }

    @Override
    public JsonElement serialize(RecipeMap recipeMap, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();
        json.addProperty("name", recipeMap.name);
        json.addProperty("progressTexture", recipeMap.progressTexture.imageLocation.toString());
        json.addProperty("fuelTexture", recipeMap.fuelTexture.imageLocation.toString());
        JsonArray recipes = new JsonArray();
        recipeMap.recipes.values().forEach(v -> recipes.add(Multiblocked.GSON.toJsonTree(v)));
        json.add("recipes", recipes);
        if (recipeMap.isFuelRecipeMap()) {
            json.add("fuelRecipes", Multiblocked.GSON.toJsonTree(recipeMap.fuelRecipes));
        }
        return json;
    }
}
