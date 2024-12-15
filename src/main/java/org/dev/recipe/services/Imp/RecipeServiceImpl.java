package org.dev.recipe.services.Imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dev.recipe.dto.request.RecipeRequest;
import org.dev.recipe.dto.response.RecipeResponse;
import org.dev.recipe.services.GeminiAIService;
import org.dev.recipe.services.RecipeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final GeminiAIService geminiAIService;

    @Override
    public RecipeResponse generateRecipe(RecipeRequest request) {
        // Create a more structured prompt for the AI
        String prompt = String.format(
                "You are a professional chef. Please create a detailed and well-structured recipe based on the following ingredients: %s.\n\n" +
                        "### Recipe Requirements:\n" +
                        "1. Provide a **Title** for the recipe.\n" +
                        "2. Specify the **Cooking Time** in minutes or hours.\n" +
                        "3. List the **Ingredients**, with each ingredient on a new line.\n" +
                        "4. Write the **Instructions** step-by-step, numbered sequentially.\n" +
                        "5. Ensure the recipe is easy to follow and suitable for home cooking.\n\n" +
                        "### Formatting Example:\n" +
                        "TITLE: [Recipe Name]\n" +
                        "COOKING TIME: [Time]\n" +
                        "INGREDIENTS:\n" +
                        "- [Ingredient 1]\n" +
                        "- [Ingredient 2]\n" +
                        "- [Ingredient 3]\n" +
                        "INSTRUCTIONS:\n" +
                        "1. [Step 1]\n" +
                        "2. [Step 2]\n" +
                        "3. [Step 3]\n\n" +
                        "Focus on clarity, accuracy, and creativity while generating the recipe.",
                request.getIngredients()
        );


        String aiResponse = geminiAIService.generateContent(prompt);
        log.info("AI Response: {}", aiResponse); // Add logging to see the response

        return parseRecipe(aiResponse, request);
    }

    private RecipeResponse parseRecipe(String content, RecipeRequest request) {
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

            if (trimmedLine.toUpperCase().startsWith("TITLE:")) {
                title = trimmedLine.substring(6).trim();
            }
            else if (trimmedLine.toUpperCase().startsWith("COOKING TIME:")) {
                cookingTime = trimmedLine.substring(12).trim();
            }
            else if (trimmedLine.toUpperCase().startsWith("INGREDIENTS:")) {
                inIngredients = true;
                inInstructions = false;
                continue;
            }
            else if (trimmedLine.toUpperCase().startsWith("INSTRUCTIONS:")) {
                inInstructions = true;
                inIngredients = false;
                continue;
            }

            if (inIngredients && !trimmedLine.toUpperCase().contains("INSTRUCTIONS:")) {
                ingredients.add(trimmedLine);
            }

            if (inInstructions) {
                instructions.add(trimmedLine);
            }
        }

        log.info("Parsed Recipe - Title: {}, Ingredients: {}, Instructions: {}",
                title, ingredients.size(), instructions.size());

        return RecipeResponse.builder()
                .title(title)
                .ingredients(ingredients)
                .instructions(instructions)
                .cookingTime(cookingTime)
                .difficulty(request.getDifficulty())
                .servings(request.getServings())
                .cuisine(request.getCuisine())
                .status("success")
                .build();
    }
}