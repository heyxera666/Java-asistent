package com.personalassistant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    private final WebClient webClient;

    @Value("${groq.api.key:}")
    private String apiKey;

    @Value("${groq.model}")
    private String model;

    public GroqService(@Value("${groq.api.url}") String apiUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    public String chat(String userMessage) {
        if (!StringUtils.hasText(apiKey)) {
            return "AI чат пока не настроен. Добавьте ключ Groq в переменную окружения GROQ_API_KEY и перезапустите проект.";
        }

        try {
            Map<String, Object> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", "Ты полезный персональный ассистент. Отвечай кратко и по делу.");

            Map<String, Object> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);

            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(systemMsg);
            messages.add(userMsg);

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", messages);
            body.put("max_tokens", 1024);
            body.put("temperature", 0.7);

            Map response = webClient.post()
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || response.get("choices") == null) {
                return "AI вернул пустой ответ. Попробуйте еще раз.";
            }

            List choices = (List) response.get("choices");
            Map choice = (Map) choices.get(0);
            Map message = (Map) choice.get("message");
            return "🤖 " + message.get("content").toString();

        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            if (e.getStatusCode().value() == 401) {
                return "AI не авторизовался в Groq: проверьте GROQ_API_KEY и перезапустите проект.";
            }
            return "Ошибка AI: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        } catch (Exception e) {
            return "Ошибка AI: " + e.getMessage();
        }
    }
}
