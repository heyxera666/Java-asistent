package com.personalassistant;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        } else if (command.equals("time")) {
            return getTime();
        } else if (command.equals("date")) {
            return getDate();
        } else if (command.startsWith("weather")) {
            return getWeather(input.substring(7).trim());
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
                "• time - показать текущее время\n" +
                "• date - показать дату\n" +
                "• weather - информация о погоде\n" +
                "• note add <текст> - добавить заметку\n" +
                "• note list - показать все заметки\n" +
                "• note delete <номер> - удалить заметку\n" +
                "• timer <секунды> - запустить таймер";
    }

    /**
     * Получить текущее время.
     */
    private String getTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return "⏰ Текущее время: " + now.format(formatter);
    }

    /**
     * Получить текущую дату.
     */
    private String getDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String dayOfWeek = getDayOfWeek(now);
        return "📅 " + now.format(formatter) + " (" + dayOfWeek + ")";
    }

    /**
     * Получить название дня недели на русском.
     */
    private String getDayOfWeek(LocalDateTime dateTime) {
        String[] days = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
        return days[dateTime.getDayOfWeek().getValue() - 1];
    }

    /**
     * Получить информацию о погоде.
     */
    private String getWeather(String location) {
        if (location.isEmpty()) {
            location = "Москва";
        }
        
        // Mock data - в реальном приложении можно подключить API OpenWeatherMap
        String[] conditions = {"Ясно ☀️", "Облачно ☁️", "Дождь 🌧️", "Снег ❄️"};
        
        // Генерируем детерминированный результат на основе названия города
        int temp = 15 + (location.length() % 10);
        int conditionIndex = location.length() % conditions.length;
        int humidity = 50 + (location.length() % 30);
        int wind = 5 + (location.length() % 15);
        
        return String.format("🌡️ Погода в %s:\n" +
                "• Состояние: %s\n" +
                "• Температура: %d°C\n" +
                "• Влажность: %d%%\n" +
                "• Ветер: %d км/ч",
                location.substring(0, 1).toUpperCase() + location.substring(1),
                conditions[conditionIndex],
                temp, humidity, wind);
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
