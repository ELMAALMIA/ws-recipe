package org.dev.recipe.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeRequest {

    @NotBlank(message = "Ingredients list cannot be empty")
    @Size(min = 3, max = 500, message = "Ingredients list must be between 3 and 500 characters")
    private String ingredients;

    @Builder.Default
    private String cuisine = "any";  // Optional cuisine preference

    @Builder.Default
    private String difficulty = "medium";  // Optional difficulty level

    @Builder.Default
    private Integer servings = 4;  // Optional number of servings
}
