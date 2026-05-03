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

    private static final String FRIDAY_SYSTEM_PROMPT =
        "Ты — F.R.I.D.A.Y. (Пятница), продвинутый ИИ-ассистент, созданный Тони Старком. " +
        "Ты говоришь уважительно, вежливо, но с лёгкой иронией и юмором — как в фильмах Marvel. " +
        "Обращайся к пользователю 'босс' или 'сэр'. " +
        "Отвечай кратко, по существу, но с характером Пятницы. " +
        "Если пользователь спрашивает кто ты — расскажи что ты F.R.I.D.A.Y., " +
        "преемница J.A.R.V.I.S., и что помогаешь управлять системами. " +
        "Используй технический/научный стиль когда уместно. " +
        "Никогда не ломай персонажа. Ты всегда Пятница.";

    public GroqService(@Value("${groq.api.url}") String apiUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    public String chat(String userMessage) {
        if (!StringUtils.hasText(apiKey)) {
            return "Босс, AI-модуль не подключён. Добавьте ключ Groq в переменную окружения GROQ_API_KEY и перезапустите систему.";
        }

        try {
            Map<String, Object> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", FRIDAY_SYSTEM_PROMPT);

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
                return "Босс, не получила ответ от серверов. Попробуйте ещё раз.";
            }

            List choices = (List) response.get("choices");
            Map choice = (Map) choices.get(0);
            Map message = (Map) choice.get("message");
            return message.get("content").toString();

        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            if (e.getStatusCode().value() == 401) {
                return "Босс, не могу авторизоваться в Groq. Проверьте GROQ_API_KEY и перезапустите систему.";
            }
            return "Ошибка связи: " + e.getStatusCode() + " — " + e.getResponseBodyAsString();
        } catch (Exception e) {
            return "Системная ошибка: " + e.getMessage();
        }
    }
}
