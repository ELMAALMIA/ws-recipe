package org.dev.recipe.services;

import org.dev.recipe.dto.request.RecipeRequest;
import org.dev.recipe.dto.response.RecipeResponse;

public interface RecipeService {
    RecipeResponse generateRecipe(RecipeRequest request);
}