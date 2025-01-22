package org.dev.recipe.services.Imp;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dev.recipe.exception.MistralAIException;
import org.dev.recipe.services.AIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Primary
@Profile("mistral")
@RequiredArgsConstructor
public class MistralAIServiceImpl implements AIService {

    private final RestTemplate restTemplate;

    @Value("${mistral.api.key}")
    private String apiKey;

    @Value("${mistral.api.url}")
    private String apiUrl;

    @Override
    public String generateContent(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> requestBody = createRequestBody(prompt);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("Sending request to Mistral AI");
            JsonNode response = restTemplate.postForObject(apiUrl, entity, JsonNode.class);
            log.info("Received response from Mistral AI");

            return extractRecipeFromResponse(response);
        } catch (RestClientException e) {
            log.error("Network or timeout error while calling Mistral AI: {}", e.getMessage());
            throw new MistralAIException("Network error while calling Mistral AI: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while calling Mistral AI: {}", e.getMessage());
            throw new MistralAIException("Error calling Mistral AI: " + e.getMessage());
        }
    }

    private Map<String, Object> createRequestBody(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> message = new HashMap<>();

        message.put("role", "user");
        message.put("content", prompt);

        requestBody.put("model", "mistral-medium");
        requestBody.put("messages", List.of(message));
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2048);

        return requestBody;
    }

    private String extractRecipeFromResponse(JsonNode response) {
        try {
            if (response != null && response.has("choices") && !response.get("choices").isEmpty()) {
                return response.get("choices").get(0)
                        .get("message").get("content").asText();
            }
            log.error("Invalid response structure from Mistral AI: {}", response);
            throw new MistralAIException("Invalid response structure from Mistral AI");
        } catch (NullPointerException e) {
            log.error("Null response or missing fields in Mistral AI response: {}", response);
            throw new MistralAIException("Invalid response format from Mistral AI");
        }
    }
}