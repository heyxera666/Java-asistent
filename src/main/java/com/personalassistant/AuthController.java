package com.personalassistant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> req) {
        String result = authService.register(req.get("username"), req.get("email"), req.get("password"));
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
        String result = authService.resetPassword(req.get("email"), req.get("password"));
        Map<String, String> response = new HashMap<>();
        switch (result) {
            case "OK" -> response.put("message", "Пароль обновлен");
            case "WEAK_PASSWORD" -> response.put("error", "Пароль должен быть не менее 6 символов");
            case "USER_NOT_FOUND" -> response.put("error", "Пользователь с таким email не найден");
            default -> response.put("error", "Введите email и новый пароль");
        }
        return ResponseEntity.ok(response);
    }
}
