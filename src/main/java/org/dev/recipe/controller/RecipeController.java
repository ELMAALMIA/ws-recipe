package org.dev.recipe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dev.recipe.dto.request.RecipeRequest;
import org.dev.recipe.dto.response.RecipeResponse;
import org.dev.recipe.exception.MistralAIException;
import org.dev.recipe.services.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateRecipe(@Valid @RequestBody RecipeRequest request) {
        try {
            log.info("Received recipe generation request for ingredients: {}", request.getIngredients());
            RecipeResponse response = recipeService.generateRecipe(request);

            if ("error".equals(response.getStatus())) {
                log.error("Error generating recipe: {}", response.getError());
                return ResponseEntity.badRequest().body(response);
            }

            log.info("Successfully generated recipe: {}", response.getTitle());
            return ResponseEntity.ok(response);
        } catch (MistralAIException e) {
            log.error("Mistral AI service error: ", e);
            return ResponseEntity.internalServerError()
                    .body(RecipeResponse.builder()
                            .status("error")
                            .error("AI Service error: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Unexpected error in recipe generation: ", e);
            return ResponseEntity.internalServerError()
                    .body(RecipeResponse.builder()
                            .status("error")
                            .error("Internal server error")
                            .build());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Recipe Service is running - Powered by Mistral AI");
    }

    @GetMapping("/models")
    public ResponseEntity<String[]> getAvailableModels() {
        return ResponseEntity.ok(new String[]{"mistral-tiny", "mistral-small", "mistral-medium"});
    }
}