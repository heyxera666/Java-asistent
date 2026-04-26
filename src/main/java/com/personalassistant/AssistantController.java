package com.personalassistant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assistant")
@CrossOrigin(origins = "*")
public class AssistantController {

    @Autowired
    private AssistantService assistantService;

    @Autowired
    private GroqService groqService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    private User getCurrentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    @PostMapping("/command")
    public ResponseEntity<Map<String, String>> processCommand(
            @RequestBody Map<String, String> request, Authentication auth) {
        String input = request.get("command");
        String response = assistantService.processCommand(input);

        User user = getCurrentUser(auth);
        if (user != null) {
            chatHistoryRepository.save(new ChatHistory(user, "user", "cmd", input));
            chatHistoryRepository.save(new ChatHistory(user, "assistant", "cmd", response));
        }

        Map<String, String> result = new HashMap<>();
        result.put("response", response);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/ai")
    public ResponseEntity<Map<String, String>> askAI(
            @RequestBody Map<String, String> request, Authentication auth) {
        String message = request.get("message");
        String response = groqService.chat(message);

        User user = getCurrentUser(auth);
        if (user != null) {
            chatHistoryRepository.save(new ChatHistory(user, "user", "ai", message));
            chatHistoryRepository.save(new ChatHistory(user, "assistant", "ai", response));
        }

        Map<String, String> result = new HashMap<>();
        result.put("response", response);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Map<String, String>>> getHistory(
            @RequestParam(defaultValue = "cmd") String type, Authentication auth) {
        User user = getCurrentUser(auth);
        if (user == null) return ResponseEntity.ok(List.of());

        List<Map<String, String>> history = chatHistoryRepository
                .findByUserAndTypeOrderByCreatedAtAsc(user, type)
                .stream()
                .map(h -> {
                    Map<String, String> m = new HashMap<>();
                    m.put("role", h.getRole());
                    m.put("message", h.getMessage());
                    return m;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/history")
    public ResponseEntity<Map<String, String>> clearHistory(
            @RequestParam(defaultValue = "cmd") String type, Authentication auth) {
        User user = getCurrentUser(auth);
        if (user != null) chatHistoryRepository.deleteByUserAndType(user, type);
        Map<String, String> result = new HashMap<>();
        result.put("message", "История очищена.");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/notes")
    public ResponseEntity<Map<String, Object>> getNotes() {
        List<String> notes = assistantService.getNotes();
        Map<String, Object> result = new HashMap<>();
        result.put("notes", notes);
        result.put("count", notes.size());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/notes")
    public ResponseEntity<Map<String, String>> clearNotes() {
        assistantService.clearNotes();
        Map<String, String> result = new HashMap<>();
        result.put("message", "Все заметки удалены.");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "OK");
        result.put("message", "Personal Assistant работает!");
        return ResponseEntity.ok(result);
    }
}
