package com.personalassistant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API контроллер для Personal Assistant.
 */
@RestController
@RequestMapping("/api/assistant")
@CrossOrigin(origins = "*")
public class AssistantController {

    @Autowired
    private AssistantService assistantService;

    /**
     * Обработать команду пользователя.
     */
    @PostMapping("/command")
    public ResponseEntity<Map<String, String>> processCommand(@RequestBody Map<String, String> request) {
        String input = request.get("command");
        String response = assistantService.processCommand(input);
        
        Map<String, String> result = new HashMap<>();
        result.put("response", response);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Получить список всех заметок.
     */
    @GetMapping("/notes")
    public ResponseEntity<Map<String, Object>> getNotes() {
        List<String> notes = assistantService.getNotes();
        
        Map<String, Object> result = new HashMap<>();
        result.put("notes", notes);
        result.put("count", notes.size());
        
        return ResponseEntity.ok(result);
    }

    /**
     * Очистить все заметки.
     */
    @DeleteMapping("/notes")
    public ResponseEntity<Map<String, String>> clearNotes() {
        assistantService.clearNotes();
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "Все заметки удалены.");
        
        return ResponseEntity.ok(result);
    }

    /**
     * Проверка здоровья приложения.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "OK");
        result.put("message", "Personal Assistant работает!");
        
        return ResponseEntity.ok(result);
    }
}
