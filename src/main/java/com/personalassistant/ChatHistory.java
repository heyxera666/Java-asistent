package com.personalassistant;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_history")
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String role; // "user" or "assistant"

    @Column(nullable = false)
    private String type = "cmd"; // "cmd" or "ai"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public ChatHistory() {}
    public ChatHistory(User user, String role, String type, String message) {
        this.user = user;
        this.role = role;
        this.type = type;
        this.message = message;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getRole() { return role; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
