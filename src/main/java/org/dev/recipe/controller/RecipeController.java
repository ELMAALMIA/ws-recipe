package org.dev.recipe.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dev.recipe.dto.request.RecipeRequest;
import org.dev.recipe.dto.response.RecipeResponse;
import org.dev.recipe.services.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping("/generate")
    public ResponseEntity<RecipeResponse> generateRecipe(@Valid @RequestBody RecipeRequest request) {
        return ResponseEntity.ok(recipeService.generateRecipe(request));
    }

    @GetMapping
    public String getRecipeService() {
        return  "Hello World";
    }
}
