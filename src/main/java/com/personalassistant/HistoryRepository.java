package com.personalassistant;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByTypeOrderByCreatedAtDesc(String type);
}
