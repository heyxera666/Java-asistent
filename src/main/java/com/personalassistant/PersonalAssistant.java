package com.personalassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Personal Assistant - веб-приложение.
 * Запуск: mvn spring-boot:run
 * Откройте http://localhost:8080
 */
@SpringBootApplication
public class PersonalAssistant {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(PersonalAssistant.class, args);
    }
}