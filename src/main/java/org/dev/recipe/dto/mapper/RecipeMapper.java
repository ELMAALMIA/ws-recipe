package org.dev.recipe.dto.mapper;

import org.dev.recipe.dto.response.RecipeResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RecipeMapper {

    public RecipeResponse stringToRecipeResponse(String aiResponse) {
        // This is a simple implementation. In a real app, you'd want to parse the AI response more robustly
        String[] parts = aiResponse.split("\n\n");

        return RecipeResponse.builder()
                .title(extractTitle(parts))
                .ingredients(extractIngredients(parts))
                .instructions(extractInstructions(parts))
                .cookingTime(extractCookingTime(parts))
                .status("success")
                .build();
    }

    private String extractTitle(String[] parts) {
        return parts[0].trim();
    }

    private List<String> extractIngredients(String[] parts) {
        // Find the ingredients section and split into list
        String ingredientsSection = Arrays.stream(parts)
                .filter(p -> p.toLowerCase().contains("ingredients:"))
                .findFirst()
                .orElse("");

        return Arrays.asList(ingredientsSection.split("\n"));
    }

    private List<String> extractInstructions(String[] parts) {
        // Find the instructions section and split into list
        String instructionsSection = Arrays.stream(parts)
                .filter(p -> p.toLowerCase().contains("instructions:"))
                .findFirst()
                .orElse("");

        return Arrays.asList(instructionsSection.split("\n"));
    }

    private String extractCookingTime(String[] parts) {
        return Arrays.stream(parts)
                .filter(p -> p.toLowerCase().contains("cooking time:"))
                .findFirst()
                .orElse("Not specified");
    }
}