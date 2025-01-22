package org.dev.recipe.services.Imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dev.recipe.dto.request.RecipeRequest;
import org.dev.recipe.dto.response.RecipeResponse;
import org.dev.recipe.services.AIService;
import org.dev.recipe.services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final AIService aiService;

    @Override
    public RecipeResponse generateRecipe(RecipeRequest request) {
        try {
            String prompt = buildPrompt(request);
            String aiResponse = aiService.generateContent(prompt);
            log.info("AI Response received for ingredients: {}", request.getIngredients());

            return parseRecipe(aiResponse, request);
        } catch (Exception e) {
            log.error("Error generating recipe: ", e);
            return RecipeResponse.builder()
                    .status("error")
                    .error("Failed to generate recipe: " + e.getMessage())
                    .build();
        }
    }

    private String buildPrompt(RecipeRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("As a professional chef, create a detailed recipe using these ingredients: ")
                .append(request.getIngredients())
                .append("\n\n");

        // Add cuisine preference if specified
        if (!"any".equalsIgnoreCase(request.getCuisine())) {
            prompt.append("Cuisine style: ").append(request.getCuisine()).append("\n");
        }

        // Add difficulty level context
        prompt.append("Difficulty level: ").append(request.getDifficulty()).append("\n");
        prompt.append("Number of servings: ").append(request.getServings()).append("\n\n");

        // Recipe structure requirements
        prompt.append("Please provide the recipe in this exact format:\n\n")
                .append("TITLE: [Creative and descriptive name for the dish]\n\n")
                .append("COOKING TIME: [Total preparation and cooking time]\n\n")
                .append("INGREDIENTS:\n")
                .append("- [Precise quantity] [Ingredient 1]\n")
                .append("- [Precise quantity] [Ingredient 2]\n")
                .append("(Continue for all ingredients)\n\n")
                .append("INSTRUCTIONS:\n")
                .append("1. [Clear, detailed first step]\n")
                .append("2. [Clear, detailed second step]\n")
                .append("(Continue with all steps)\n\n")
                .append("Ensure all measurements are precise and instructions are clear for home cooking.");

        return prompt.toString();
    }

    private RecipeResponse parseRecipe(String content, RecipeRequest request) {
        try {
            String[] sections = content.split("\n");

            String title = "";
            String cookingTime = "";
            List<String> ingredients = new ArrayList<>();
            List<String> instructions = new ArrayList<>();

            boolean inIngredients = false;
            boolean inInstructions = false;

            for (String line : sections) {
                String trimmedLine = line.trim();

                if (trimmedLine.isEmpty()) continue;

                // Parse title
                if (trimmedLine.toUpperCase().startsWith("TITLE:")) {
                    title = extractContent(trimmedLine, "TITLE:");
                    continue;
                }

                // Parse cooking time
                if (trimmedLine.toUpperCase().startsWith("COOKING TIME:")) {
                    cookingTime = extractContent(trimmedLine, "COOKING TIME:");
                    continue;
                }

                // Handle ingredients section
                if (trimmedLine.toUpperCase().equals("INGREDIENTS:")) {
                    inIngredients = true;
                    inInstructions = false;
                    continue;
                }

                // Handle instructions section
                if (trimmedLine.toUpperCase().equals("INSTRUCTIONS:")) {
                    inInstructions = true;
                    inIngredients = false;
                    continue;
                }

                // Add ingredients
                if (inIngredients && trimmedLine.startsWith("-")) {
                    ingredients.add(trimmedLine.substring(1).trim());
                    continue;
                }

                // Add instructions
                if (inInstructions && Character.isDigit(trimmedLine.charAt(0))) {
                    String instruction = trimmedLine.replaceFirst("^\\d+\\.\\s*", "");
                    instructions.add(instruction);
                }
            }

            log.info("Successfully parsed recipe - Title: {}, Ingredients: {}, Instructions: {}",
                    title, ingredients.size(), instructions.size());

            return RecipeResponse.builder()
                    .title(title)
                    .cookingTime(cookingTime)
                    .ingredients(ingredients)
                    .instructions(instructions)
                    .difficulty(request.getDifficulty())
                    .servings(request.getServings())
                    .cuisine(request.getCuisine())
                    .status("success")
                    .build();

        } catch (Exception e) {
            log.error("Error parsing recipe: ", e);
            return RecipeResponse.builder()
                    .status("error")
                    .error("Failed to parse recipe: " + e.getMessage())
                    .build();
        }
    }

    private String extractContent(String line, String prefix) {
        return line.substring(prefix.length()).trim();
    }
}