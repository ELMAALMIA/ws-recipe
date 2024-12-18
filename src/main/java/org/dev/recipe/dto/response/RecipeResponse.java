package org.dev.recipe.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeResponse {

    private String title;
    private List<String> ingredients;
    private List<String> instructions;
    private String cookingTime;
    private String difficulty;
    private Integer servings;
    private String cuisine;

    @Builder.Default
    private String status = "success";

    @Builder.Default
    private  String error = "";


    // Optional metadata
    private NutritionInfo nutritionInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutritionInfo {
        private Integer calories;
        private Integer protein;
        private Integer carbs;
        private Integer fat;

    }
}
