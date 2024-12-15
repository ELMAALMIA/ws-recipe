package org.dev.recipe.services.Imp;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.dev.recipe.exception.GeminiAIException;
import org.dev.recipe.services.GeminiAIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiAIServiceImpl implements GeminiAIService {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Override
    public String generateContent(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = createRequestBody(prompt);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String url = apiUrl + "?key=" + apiKey;
            JsonNode response = restTemplate.postForObject(url, entity, JsonNode.class);

            return extractRecipeFromResponse(response);
        } catch (Exception e) {
            throw new GeminiAIException("Error calling Gemini AI: " + e.getMessage());
        }
    }

    private Map<String, Object> createRequestBody(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, String> part = new HashMap<>();

        part.put("text", prompt);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        return requestBody;
    }

    private String extractRecipeFromResponse(JsonNode response) {
        if (response != null && response.has("candidates")) {
            return response.get("candidates").get(0)
                    .get("content").get("parts").get(0)
                    .get("text").asText();
        }
        throw new GeminiAIException("Invalid response from Gemini AI");
    }
}
