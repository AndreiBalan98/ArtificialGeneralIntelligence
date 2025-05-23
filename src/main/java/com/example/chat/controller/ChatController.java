package com.example.chat.controller;

import com.example.chat.dto.ChatRequest;
import com.example.chat.dto.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String userPrompt = request.getMessage();
        String provider = request.getProvider();
        String augmentUrl = "http://localhost:8000/rag";
        String ollamaUrl = "http://localhost:11434/api/generate";
        String microsoftUrl = "https://openrouter.ai/api/v1/chat/completions";
        String openRouterApiKey = "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";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String augmentPayload = String.format("{\"prompt\": \"%s\"}", escapeJson(userPrompt));
            HttpEntity<String> augmentRequest = new HttpEntity<>(augmentPayload, headers);

            ResponseEntity<String> augmentResponse = restTemplate.postForEntity(augmentUrl, augmentRequest, String.class);
            JsonNode augmentJson = mapper.readTree(augmentResponse.getBody());

            if (augmentJson.has("error")) {
                return new ChatResponse("Eroare de la serviciul RAG: " + augmentJson.get("error").asText(), Collections.emptyList(), Collections.emptyList());
            }

            String augmentedPrompt = augmentJson.get("augmented_prompt").asText();
            JsonNode chunksNode = augmentJson.get("chunks");
            JsonNode sourcesNode = augmentJson.get("sources");

            List<String> chunks = chunksNode != null ? mapper.readValue(chunksNode.toString(), List.class) : Collections.emptyList();
            List<String> sources = sourcesNode != null ? mapper.readValue(sourcesNode.toString(), List.class) : Collections.emptyList();

            if (provider.equals("microsoft")) {
                headers.setBearerAuth(openRouterApiKey);

                String openRouterPayload = String.format(
                        "{\"model\": \"microsoft/mai-ds-r1:free\", \"temperature\": 0.7, \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}",
                        escapeJson(augmentedPrompt)
                );
                HttpEntity<String> openRouterRequest = new HttpEntity<>(openRouterPayload, headers);

                ResponseEntity<String> openRouterResponse = restTemplate.postForEntity(microsoftUrl, openRouterRequest, String.class);
                JsonNode openRouterJson = mapper.readTree(openRouterResponse.getBody());

                JsonNode microsoftAnswerNode = openRouterJson.at("/choices/0/message/content");
                String microsoftAnswer = microsoftAnswerNode.isTextual() ? microsoftAnswerNode.asText() : "Răspuns invalid de la OpenRouter.";

                return new ChatResponse(microsoftAnswer, chunks, sources);

            } else if (provider.equals("mistral")) {
                String ollamaPayload = String.format("{\"model\": \"mistral\", \"prompt\": \"%s\", \"stream\": false}", escapeJson(augmentedPrompt));
                HttpEntity<String> ollamaRequest = new HttpEntity<>(ollamaPayload, headers);

                ResponseEntity<String> ollamaResponse = restTemplate.postForEntity(ollamaUrl, ollamaRequest, String.class);
                JsonNode ollamaJson = mapper.readTree(ollamaResponse.getBody());
                String ollamaAnswer = ollamaJson.get("response").asText();

                return new ChatResponse(ollamaAnswer, chunks, sources);
            }
            else {
                return new ChatResponse("Nici un provider selectat", Collections.emptyList(), Collections.emptyList());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ChatResponse("Eroare la procesarea mesajului: " + e.getMessage(), Collections.emptyList(), Collections.emptyList());
        }
    }

    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
