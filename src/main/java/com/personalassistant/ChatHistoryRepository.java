package com.personalassistant;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByUserOrderByCreatedAtAsc(User user);
    List<ChatHistory> findByUserAndTypeOrderByCreatedAtAsc(User user, String type);
    void deleteByUserAndType(User user, String type);
    void deleteByUser(User user);
}
