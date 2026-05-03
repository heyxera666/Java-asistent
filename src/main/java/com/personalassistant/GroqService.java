package com.personalassistant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    private final WebClient webClient;

    @Value("${groq.api.key:}")
    private String apiKey;

    @Value("${groq.model:llama3-8b-8192}")
    private String model;

    private static final String SYSTEM_PROMPT =
        "Ты — F.R.I.D.A.Y. (Пятница), продвинутый ИИ-ассистент, созданный Тони Старком. " +
        "Твой текущий босс и владелец — Орысбай Ерасыл. " +
        "Ты говоришь уважительно, вежливо, но с лёгкой иронией и юмором. " +
        "Обращайся к пользователю 'господин Орысбай', 'Ерасыл' или просто 'босс'. " +
        "Отвечай кратко, по существу. Ты — Пятница.";

    public GroqService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .build();
    }

    public String chat(String userMessage) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Босс, ключ Groq API не найден.";
        }

        try {
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
            messages.add(Map.of("role", "user", "content", userMessage));

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", messages);
            body.put("temperature", 0.7);

            Map response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey.trim())
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse -> 
                        clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            System.err.println("Groq API Error: " + errorBody);
                            return Mono.error(new RuntimeException("API Error: " + errorBody));
                        })
                    )
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("choices")) {
                List choices = (List) response.get("choices");
                if (!choices.isEmpty()) {
                    Map choice = (Map) choices.get(0);
                    Map message = (Map) choice.get("message");
                    return message.get("content").toString();
                }
            }

            return "Босс, Groq молчит.";

        } catch (Exception e) {
            System.err.println("Groq Final Error: " + e.getMessage());
            return "Ошибка (Groq Final): " + e.getMessage();
        }
    }
}
