package com.personalassistant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> req) {
        String result = authService.register(
                req.get("username"), req.get("email"), req.get("password"));
        Map<String, String> response = new HashMap<>();
        switch (result) {
            case "EMAIL_EXISTS" -> response.put("error", "Email уже используется");
            case "USERNAME_EXISTS" -> response.put("error", "Имя пользователя занято");
            default -> response.put("message", "Регистрация успешна");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> req) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "ok");
        return ResponseEntity.ok(response);
    }
}
