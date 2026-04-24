package com.personalassistant;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для обработки команд Personal Assistant.
 */
@Service
public class AssistantService {

    private final List<String> notes = new ArrayList<>();

    /**
     * Обработка команды и возврат ответа.
     */
    public String processCommand(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Пожалуйста, введите команду.";
        }

        String command = input.toLowerCase().trim();

        if (command.equals("hello")) {
            return "Привет! Как я могу помочь?";
        } else if (command.equals("help")) {
            return getHelp();
        } else if (command.startsWith("note ")) {
            return handleNote(input.substring(5).trim());
        } else if (command.startsWith("timer ")) {
            return handleTimer(input.substring(6).trim());
        } else {
            return "Неизвестная команда. Введите 'help' для списка доступных команд.";
        }
    }

    /**
     * Получить справку по командам.
     */
    private String getHelp() {
        return "📋 Доступные команды:\n" +
                "• hello - приветствие\n" +
                "• help - показать эту справку\n" +
                "• note add <текст> - добавить заметку\n" +
                "• note list - показать все заметки\n" +
                "• note delete <номер> - удалить заметку\n" +
                "• timer <секунды> - запустить таймер";
    }

    /**
     * Обработка команд заметок.
     */
    private String handleNote(String subCommand) {
        if (subCommand.startsWith("add ")) {
            String text = subCommand.substring(4).trim();
            if (!text.isEmpty()) {
                notes.add(text);
                return "✅ Заметка добавлена: " + text;
            } else {
                return "❌ Текст заметки пустой.";
            }
        } else if (subCommand.equals("list")) {
            if (notes.isEmpty()) {
                return "📝 У вас нет заметок.";
            } else {
                StringBuilder sb = new StringBuilder("📝 Ваши заметки:\n");
                for (int i = 0; i < notes.size(); i++) {
                    sb.append((i + 1)).append(". ").append(notes.get(i)).append("\n");
                }
                return sb.toString().trim();
            }
        } else if (subCommand.startsWith("delete ")) {
            try {
                int index = Integer.parseInt(subCommand.substring(7).trim()) - 1;
                if (index >= 0 && index < notes.size()) {
                    String deleted = notes.remove(index);
                    return "🗑️ Заметка удалена: " + deleted;
                } else {
                    return "❌ Заметка с номером " + (index + 1) + " не найдена.";
                }
            } catch (NumberFormatException e) {
                return "❌ Неверный номер заметки.";
            }
        } else {
            return "❌ Неверная команда. Используйте 'note add <текст>', 'note list' или 'note delete <номер>'.";
        }
    }

    /**
     * Обработка команды таймер.
     */
    private String handleTimer(String subCommand) {
        try {
            int seconds = Integer.parseInt(subCommand.trim());
            if (seconds > 0) {
                return "⏱️ Таймер запущен на " + seconds + " секунд. Вы будете уведомлены когда время истечет.";
            } else {
                return "❌ Время должно быть положительным числом.";
            }
        } catch (NumberFormatException e) {
            return "❌ Неверный формат. Используйте: timer <количество_секунд>";
        }
    }

    /**
     * Получить список всех заметок.
     */
    public List<String> getNotes() {
        return new ArrayList<>(notes);
    }

    /**
     * Очистить все заметки.
     */
    public void clearNotes() {
        notes.clear();
    }
}
