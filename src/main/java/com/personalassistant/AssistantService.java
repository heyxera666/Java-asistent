package com.personalassistant;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Сервис для обработки команд Personal Assistant.
 */
@Service
public class AssistantService {

    @Autowired
    private NoteRepository noteRepository;

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
        } else if (command.startsWith("calc ")) {
            return calculate(input.substring(5).trim());
        } else if (command.startsWith("convert ")) {
            return convert(input.substring(8).trim());
        } else if (command.startsWith("pomodoro")) {
            return handlePomodoro(input.substring(8).trim());
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
                "• weather <город> - информация о погоде\n" +
                "• note add <текст> - добавить заметку\n" +
                "• note list - показать все заметки\n" +
                "• note delete <номер> - удалить заметку\n" +
                "• note search <текст> - поиск по заметкам\n" +
                "• timer <секунды> - запустить таймер\n" +
                "• calc <выражение> - калькулятор (например: calc 2+2*3)\n" +
                "• convert <число> <из> to <в> - конвертер (usd/eur/rub/km/mi/kg/lb/c/f)\n" +
                "• pomodoro start - запустить помодоро (25 мин)\n" +
                "• pomodoro break - короткий перерыв (5 мин)";
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
                noteRepository.save(new Note(text));
                return "✅ Заметка добавлена: " + text;
            } else {
                return "❌ Текст заметки пустой.";
            }
        } else if (subCommand.equals("list")) {
            List<Note> all = noteRepository.findAll();
            if (all.isEmpty()) {
                return "📝 У вас нет заметок.";
            } else {
                StringBuilder sb = new StringBuilder("📝 Ваши заметки:\n");
                for (int i = 0; i < all.size(); i++) {
                    sb.append((i + 1)).append(". ").append(all.get(i).getText()).append("\n");
                }
                return sb.toString().trim();
            }
        } else if (subCommand.startsWith("search ")) {
            String query = subCommand.substring(7).trim();
            if (query.isEmpty()) return "❌ Введите текст для поиска.";
            List<Note> found = noteRepository.findByTextContainingIgnoreCase(query);
            if (found.isEmpty()) return "🔍 Заметки не найдены по запросу: " + query;
            List<Note> all = noteRepository.findAll();
            StringBuilder sb = new StringBuilder("🔍 Найдено:\n");
            for (Note n : found) {
                sb.append((all.indexOf(n) + 1)).append(". ").append(n.getText()).append("\n");
            }
            return sb.toString().trim();
        } else if (subCommand.startsWith("delete ")) {
            try {
                int index = Integer.parseInt(subCommand.substring(7).trim()) - 1;
                List<Note> all = noteRepository.findAll();
                if (index >= 0 && index < all.size()) {
                    Note toDelete = all.get(index);
                    noteRepository.delete(toDelete);
                    return "🗑️ Заметка удалена: " + toDelete.getText();
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
     * Калькулятор.
     */
    private String calculate(String expr) {
        if (expr.isEmpty()) return "❌ Введите выражение. Пример: calc 2+2";
        try {
            double result = evalExpr(expr.replaceAll("\\s+", ""));
            String formatted = result == (long) result ? String.valueOf((long) result) : String.valueOf(result);
            return "🧮 " + expr + " = " + formatted;
        } catch (Exception e) {
            return "❌ Ошибка в выражении: " + expr;
        }
    }

    private double evalExpr(String expr) {
        // Поддержка +, -, *, /, скобок
        return new Object() {
            int pos = 0;
            double parse() {
                double x = parseTerm();
                while (pos < expr.length()) {
                    if (expr.charAt(pos) == '+') { pos++; x += parseTerm(); }
                    else if (expr.charAt(pos) == '-') { pos++; x -= parseTerm(); }
                    else break;
                }
                return x;
            }
            double parseTerm() {
                double x = parseFactor();
                while (pos < expr.length()) {
                    if (expr.charAt(pos) == '*') { pos++; x *= parseFactor(); }
                    else if (expr.charAt(pos) == '/') { pos++; x /= parseFactor(); }
                    else break;
                }
                return x;
            }
            double parseFactor() {
                if (pos < expr.length() && expr.charAt(pos) == '(') {
                    pos++;
                    double x = parse();
                    pos++; // ')'
                    return x;
                }
                int start = pos;
                if (pos < expr.length() && expr.charAt(pos) == '-') pos++;
                while (pos < expr.length() && (Character.isDigit(expr.charAt(pos)) || expr.charAt(pos) == '.')) pos++;
                return Double.parseDouble(expr.substring(start, pos));
            }
        }.parse();
    }

    /**
     * Конвертер единиц и валют.
     */
    private String convert(String input) {
        // Формат: <число> <из> to <в>
        String[] parts = input.toLowerCase().split("\\s+to\\s+");
        if (parts.length != 2) return "❌ Формат: convert <число> <единица> to <единица>\nПример: convert 100 usd to eur";
        String[] fromParts = parts[0].trim().split("\\s+");
        if (fromParts.length != 2) return "❌ Формат: convert <число> <единица> to <единица>";
        double value;
        try { value = Double.parseDouble(fromParts[0]); } catch (NumberFormatException e) { return "❌ Неверное число."; }
        String from = fromParts[1];
        String to = parts[1].trim();
        double result = convertValue(value, from, to);
        if (Double.isNaN(result)) return "❌ Неизвестные единицы: " + from + " → " + to;
        String formatted = result == (long) result ? String.valueOf((long) result) : String.format("%.4f", result);
        return String.format("🔄 %s %s = %s %s", fromParts[0], from.toUpperCase(), formatted, to.toUpperCase());
    }

    private double convertValue(double v, String from, String to) {
        // Конвертируем в базовую единицу, затем в целевую
        double base = toBase(v, from);
        if (Double.isNaN(base)) return Double.NaN;
        return fromBase(base, from, to);
    }

    private double toBase(double v, String unit) {
        return switch (unit) {
            // Валюты (в USD)
            case "usd" -> v;
            case "eur" -> v / 0.92;
            case "rub" -> v / 90.0;
            case "gbp" -> v / 0.79;
            // Длина (в км)
            case "km" -> v;
            case "mi" -> v * 1.60934;
            case "m" -> v / 1000.0;
            case "ft" -> v * 0.0003048;
            // Вес (в кг)
            case "kg" -> v;
            case "lb" -> v * 0.453592;
            case "g" -> v / 1000.0;
            // Температура (в Цельсий)
            case "c" -> v;
            case "f" -> (v - 32) * 5.0 / 9.0;
            case "k" -> v - 273.15;
            default -> Double.NaN;
        };
    }

    private double fromBase(double base, String from, String to) {
        // Определяем группу единиц
        String[] currencies = {"usd", "eur", "rub", "gbp"};
        String[] lengths = {"km", "mi", "m", "ft"};
        String[] weights = {"kg", "lb", "g"};
        String[] temps = {"c", "f", "k"};
        if (!sameGroup(from, to, currencies) && !sameGroup(from, to, lengths) &&
            !sameGroup(from, to, weights) && !sameGroup(from, to, temps)) return Double.NaN;
        return switch (to) {
            case "usd" -> base;
            case "eur" -> base * 0.92;
            case "rub" -> base * 90.0;
            case "gbp" -> base * 0.79;
            case "km" -> base;
            case "mi" -> base / 1.60934;
            case "m" -> base * 1000.0;
            case "ft" -> base / 0.0003048;
            case "kg" -> base;
            case "lb" -> base / 0.453592;
            case "g" -> base * 1000.0;
            case "c" -> base;
            case "f" -> base * 9.0 / 5.0 + 32;
            case "k" -> base + 273.15;
            default -> Double.NaN;
        };
    }

    private boolean sameGroup(String a, String b, String[] group) {
        boolean hasA = false, hasB = false;
        for (String s : group) { if (s.equals(a)) hasA = true; if (s.equals(b)) hasB = true; }
        return hasA && hasB;
    }

    /**
     * Помодоро таймер.
     */
    private String handlePomodoro(String sub) {
        sub = sub.trim().toLowerCase();
        if (sub.equals("start") || sub.isEmpty()) {
            return "🍅 POMODORO:25:00";
        } else if (sub.equals("break")) {
            return "☕ BREAK:05:00";
        } else if (sub.equals("long break")) {
            return "🛋️ LONGBREAK:15:00";
        }
        return "❌ Используйте: pomodoro start | pomodoro break | pomodoro long break";
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
        return noteRepository.findAll().stream()
                .map(Note::getText)
                .collect(Collectors.toList());
    }

    public void clearNotes() {
        noteRepository.deleteAll();
    }
}
