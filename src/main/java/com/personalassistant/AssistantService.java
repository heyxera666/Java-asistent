package com.personalassistant;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.net.URI;

/**
 * Сервис F.R.I.D.A.Y. — обработка голосовых и текстовых команд.
 */
@Service
public class AssistantService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private GroqService groqService;

    /**
     * Умная обработка команды — поддержка синонимов и естественной речи.
     */
    public String processCommand(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Босс, я вас слушаю. Скажите команду.";
        }

        String command = input.toLowerCase().trim();

        // --- Приветствия ---
        if (matchesAny(command, "привет", "здравствуй", "хай", "hello", "hi", "добрый день",
                "доброе утро", "добрый вечер", "йоу", "салам", "здорово", "приветик")) {
            return "Здравствуйте, босс! Системы F.R.I.D.A.Y. в полной готовности. Чем могу помочь?";
        }

        // --- Справка ---
        if (matchesAny(command, "help", "помощь", "команды", "что умеешь", "что ты умеешь",
                "что можешь", "помоги", "справка")) {
            return getHelp();
        }

        // --- Время ---
        if (matchesAny(command, "time", "время", "сколько времени", "который час",
                "текущее время", "часы")) {
            return getTime();
        }

        // --- Дата ---
        if (matchesAny(command, "date", "дата", "какая дата", "какой сегодня день",
                "число", "сегодня", "какое число")) {
            return getDate();
        }

        // --- Погода ---
        if (command.startsWith("weather") || command.startsWith("погода")) {
            String city = command.replaceFirst("(weather|погода)\\s*", "").trim();
            return getWeather(city);
        }

        // --- Заметки ---
        if (command.startsWith("note ") || command.startsWith("заметка ") ||
            command.startsWith("запиши") || command.startsWith("запомни")) {
            return handleNoteNatural(input);
        }

        // --- Таймер ---
        if (command.startsWith("timer ") || command.startsWith("таймер ")) {
            String time = command.replaceFirst("(timer|таймер)\\s*", "").trim();
            return handleTimer(time);
        }

        // --- Калькулятор ---
        if (command.startsWith("calc ") || command.startsWith("посчитай ") ||
            command.startsWith("калькулятор ") || command.startsWith("вычисли ")) {
            String expr = command.replaceFirst("(calc|посчитай|калькулятор|вычисли)\\s*", "").trim();
            return calculate(expr);
        }

        // --- Конвертер ---
        if (command.startsWith("convert ") || command.startsWith("конвертируй ") ||
            command.startsWith("переведи единицы ")) {
            String expr = command.replaceFirst("(convert|конвертируй|переведи единицы)\\s*", "").trim();
            return convert(expr);
        }

        // --- Помодоро ---
        if (command.startsWith("pomodoro") || command.startsWith("помодоро") ||
            command.equals("работа") || command.equals("фокус")) {
            String sub = command.replaceFirst("(pomodoro|помодоро)\\s*", "").trim();
            return handlePomodoro(sub);
        }

        // --- IP ---
        if (matchesAny(command, "ip", "мой ip", "айпи", "мой айпи", "покажи ip")) {
            return getIP();
        }

        // --- Шутка ---
        if (matchesAny(command, "joke", "шутка", "анекдот", "рассмеши", "пошути",
                "расскажи шутку", "смешное")) {
            return getJoke();
        }

        // --- Цитата ---
        if (matchesAny(command, "quote", "цитата", "мотивация", "вдохнови",
                "мудрость", "скажи цитату")) {
            return getQuote();
        }

        // --- Перевод ---
        if (command.startsWith("translate ") || command.startsWith("переведи ")) {
            String text = command.replaceFirst("(translate|переведи)\\s*", "").trim();
            return translate(text);
        }

        // --- Статус системы ---
        if (matchesAny(command, "статус", "status", "система", "диагностика",
                "как дела", "всё ок", "системы")) {
            return getSystemStatus();
        }

        // --- Скриншот ---
        if (matchesAny(command, "скриншот", "screenshot", "сделай снимок", "снимок экрана")) {
            return takeScreenshot();
        }

        // --- Гимн ---
        if (matchesAny(command, "гимн", "включи гимн", "казахстан", "гимн казахстана")) {
            return playAnthem();
        }

        // --- Самоуничтожение ---
        if (matchesAny(command, "самоуничтожение", "self-destruct", "протокол 11", "удали себя")) {
            return "[TRIGGER_SELF_DESTRUCT] Босс, протокол самоуничтожения активирован. Было честью работать с вами. Прощайте.";
        }

        // --- Глубокое сканирование ---
        if (matchesAny(command, "сканируй", "сканирование", "анализ проекта", "scan", "deep scan")) {
            return scanProject();
        }

        // --- Геолокация ---
        if (matchesAny(command, "где я", "геолокация", "координаты", "местоположение", "where am i")) {
            return getLocation();
        }

        // --- Статус систем ---
        if (matchesAny(command, "статус систем", "статсус систем", "статус", "статсус", "статистика", "stats", "status")) {
            return getSystemStatus();
        }

        // --- Футбол ---
        if (matchesAny(command, "самый лучший футбольный клуб", "лучший клуб", "кто чемпионы")) {
            return "Ливерпуль, босс! 🔴";
        }

        // --- Кто ты ---
        if (matchesAny(command, "кто ты", "что ты", "как тебя зовут", "who are you",
                "представься", "ты кто")) {
            return "Я — F.R.I.D.A.Y., ваш персональный ИИ-ассистент, босс. " +
                   "Преемница J.A.R.V.I.S. Готова помочь с любыми задачами. " +
                   "Все системы функционируют в штатном режиме.";
        }

        // --- Если не распознано — спросить AI ---
        return groqService.chat(input);
    }

    private boolean matchesAny(String input, String... variants) {
        for (String v : variants) {
            if (input.equals(v) || input.startsWith(v + " ") || input.contains(v)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Справка в стиле F.R.I.D.A.Y.
     */
    private String getHelp() {
        return "Босс, вот мои возможности:\\n\\n" +
                "🎙️ ГОЛОСОВЫЕ КОМАНДЫ:\\n" +
                "• «Привет» — приветствие\\n" +
                "• «Который час» / «Время» — текущее время\\n" +
                "• «Какая дата» / «Число» — текущая дата\\n" +
                "• «Погода Москва» — прогноз погоды\\n" +
                "• «Статус» — диагностика систем\\n" +
                "• «Кто ты» — информация обо мне\\n\\n" +
                "📝 ЗАМЕТКИ:\\n" +
                "• «Запиши купить молоко» — новая заметка\\n" +
                "• «Покажи заметки» — список заметок\\n" +
                "• «Удали заметку 1» — удалить заметку\\n" +
                "• «Найди заметку ...» — поиск\\n\\n" +
                "🔧 ИНСТРУМЕНТЫ:\\n" +
                "• «Посчитай 2+2*3» — калькулятор\\n" +
                "• «Таймер 60» — таймер в секундах\\n" +
                "• «Помодоро» — техника Помодоро\\n" +
                "• «Конвертируй 100 usd to eur» — конвертер\\n" +
                "• «Переведи Hello» — переводчик\\n\\n" +
                "🎭 РАЗВЛЕЧЕНИЯ:\\n" +
                "• «Шутка» — рассказать шутку\\n" +
                "• «Цитата» — мудрая цитата\\n\\n" +
                "💡 Всё, что не распознано — отправляется в AI-модуль для умного ответа.";
    }

    private String getTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return "Босс, сейчас " + now.format(formatter) + ". Все системы синхронизированы.";
    }

    private String getDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String dayOfWeek = getDayOfWeekFull(now);
        return "Сегодня " + dayOfWeek + ", " + now.format(formatter) + ", босс.";
    }

    private String getDayOfWeekFull(LocalDateTime dateTime) {
        String[] days = {"понедельник", "вторник", "среда", "четверг", "пятница", "суббота", "воскресенье"};
        return days[dateTime.getDayOfWeek().getValue() - 1];
    }

    private String getWeather(String location) {
        if (location.isEmpty()) {
            location = "Москва";
        }

        String[] conditions = {"Ясно ☀️", "Облачно ☁️", "Дождь 🌧️", "Снег ❄️"};
        int temp = 15 + (location.length() % 10);
        int conditionIndex = location.length() % conditions.length;
        int humidity = 50 + (location.length() % 30);
        int wind = 5 + (location.length() % 15);
        String city = location.substring(0, 1).toUpperCase() + location.substring(1);

        return String.format("Босс, данные по %s получены:\\n" +
                "• Состояние: %s\\n" +
                "• Температура: %d°C\\n" +
                "• Влажность: %d%%\\n" +
                "• Скорость ветра: %d км/ч\\n" +
                "Рекомендую учесть эти условия при планировании.",
                city, conditions[conditionIndex], temp, humidity, wind);
    }

    /**
     * Обработка заметок с поддержкой естественного языка.
     */
    private String handleNoteNatural(String input) {
        String lower = input.toLowerCase().trim();

        // «запиши ...» или «запомни ...»
        if (lower.startsWith("запиши ") || lower.startsWith("запомни ")) {
            String text = input.substring(input.indexOf(' ') + 1).trim();
            if (!text.isEmpty()) {
                noteRepository.save(new Note(text));
                return "Записала, босс: «" + text + "»";
            }
            return "Босс, не расслышала что записать. Повторите, пожалуйста.";
        }

        // «заметка покажи» / «покажи заметки»
        if (lower.contains("покажи") || lower.contains("список") || lower.contains("list")) {
            List<Note> all = noteRepository.findAll();
            if (all.isEmpty()) {
                return "Босс, у вас нет заметок. Хотите что-то записать?";
            }
            StringBuilder sb = new StringBuilder("Ваши заметки, босс:\\n");
            for (int i = 0; i < all.size(); i++) {
                sb.append((i + 1)).append(". ").append(all.get(i).getText()).append("\\n");
            }
            return sb.toString().trim();
        }

        // «заметка найди ...» / «найди заметку»
        if (lower.contains("найди") || lower.contains("поиск") || lower.contains("search")) {
            String query = lower.replaceAll(".*(найди|поиск|search)\\s*", "").trim();
            if (query.isEmpty()) return "Босс, что ищем?";
            List<Note> found = noteRepository.findByTextContainingIgnoreCase(query);
            if (found.isEmpty()) return "Ничего не найдено по запросу «" + query + "», босс.";
            List<Note> all = noteRepository.findAll();
            StringBuilder sb = new StringBuilder("Найдено, босс:\\n");
            for (Note n : found) {
                sb.append((all.indexOf(n) + 1)).append(". ").append(n.getText()).append("\\n");
            }
            return sb.toString().trim();
        }

        // «заметка удали 1» / «удали заметку 1»
        if (lower.contains("удали") || lower.contains("delete")) {
            String numStr = lower.replaceAll("\\D", "");
            if (numStr.isEmpty()) return "Босс, укажите номер заметки для удаления.";
            try {
                int index = Integer.parseInt(numStr) - 1;
                List<Note> all = noteRepository.findAll();
                if (index >= 0 && index < all.size()) {
                    Note toDelete = all.get(index);
                    noteRepository.delete(toDelete);
                    return "Заметка удалена, босс: «" + toDelete.getText() + "»";
                }
                return "Босс, заметки с таким номером нет.";
            } catch (NumberFormatException e) {
                return "Босс, не могу распознать номер заметки.";
            }
        }

        // Стандартная обработка
        if (lower.startsWith("note ")) {
            return handleNoteClassic(input.substring(5).trim());
        }
        if (lower.startsWith("заметка ")) {
            String sub = input.substring(8).trim();
            if (sub.toLowerCase().startsWith("добавь ") || sub.toLowerCase().startsWith("add ")) {
                String text = sub.substring(sub.indexOf(' ') + 1).trim();
                noteRepository.save(new Note(text));
                return "Записала, босс: «" + text + "»";
            }
            return handleNoteClassic(sub);
        }

        return "Босс, не поняла команду заметки. Скажите «запиши», «покажи заметки» или «удали заметку».";
    }

    private String handleNoteClassic(String subCommand) {
        if (subCommand.startsWith("add ")) {
            String text = subCommand.substring(4).trim();
            if (!text.isEmpty()) {
                noteRepository.save(new Note(text));
                return "Записала, босс: «" + text + "»";
            }
            return "Босс, текст заметки пустой.";
        } else if (subCommand.equals("list")) {
            List<Note> all = noteRepository.findAll();
            if (all.isEmpty()) {
                return "Босс, у вас нет заметок.";
            }
            StringBuilder sb = new StringBuilder("Ваши заметки, босс:\\n");
            for (int i = 0; i < all.size(); i++) {
                sb.append((i + 1)).append(". ").append(all.get(i).getText()).append("\\n");
            }
            return sb.toString().trim();
        } else if (subCommand.startsWith("search ")) {
            String query = subCommand.substring(7).trim();
            if (query.isEmpty()) return "Босс, что ищем?";
            List<Note> found = noteRepository.findByTextContainingIgnoreCase(query);
            if (found.isEmpty()) return "Ничего не найдено, босс.";
            List<Note> all = noteRepository.findAll();
            StringBuilder sb = new StringBuilder("Найдено, босс:\\n");
            for (Note n : found) {
                sb.append((all.indexOf(n) + 1)).append(". ").append(n.getText()).append("\\n");
            }
            return sb.toString().trim();
        } else if (subCommand.startsWith("delete ")) {
            try {
                int index = Integer.parseInt(subCommand.substring(7).trim()) - 1;
                List<Note> all = noteRepository.findAll();
                if (index >= 0 && index < all.size()) {
                    Note toDelete = all.get(index);
                    noteRepository.delete(toDelete);
                    return "Заметка удалена, босс: «" + toDelete.getText() + "»";
                }
                return "Босс, заметки с таким номером нет.";
            } catch (NumberFormatException e) {
                return "Босс, неверный номер.";
            }
        }
        return "Босс, используйте: заметка добавь, покажи заметки, удали заметку.";
    }

    private String calculate(String expr) {
        if (expr.isEmpty()) return "Босс, введите выражение. Например: посчитай 2+2";
        try {
            double result = evalExpr(expr.replaceAll("\\s+", ""));
            String formatted = result == (long) result ? String.valueOf((long) result) : String.valueOf(result);
            return "Результат: " + expr + " = " + formatted + ", босс.";
        } catch (Exception e) {
            return "Босс, не могу вычислить: " + expr;
        }
    }

    private double evalExpr(String expr) {
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
                    pos++;
                    return x;
                }
                int start = pos;
                if (pos < expr.length() && expr.charAt(pos) == '-') pos++;
                while (pos < expr.length() && (Character.isDigit(expr.charAt(pos)) || expr.charAt(pos) == '.')) pos++;
                return Double.parseDouble(expr.substring(start, pos));
            }
        }.parse();
    }

    private String convert(String input) {
        String[] parts = input.toLowerCase().split("\\s+to\\s+");
        if (parts.length != 2) return "Босс, формат: конвертируй 100 usd to eur";
        String[] fromParts = parts[0].trim().split("\\s+");
        if (fromParts.length != 2) return "Босс, формат: конвертируй <число> <единица> to <единица>";
        double value;
        try { value = Double.parseDouble(fromParts[0]); } catch (NumberFormatException e) { return "Босс, не могу распознать число."; }
        String from = fromParts[1];
        String to = parts[1].trim();
        double result = convertValue(value, from, to);
        if (Double.isNaN(result)) return "Босс, не знаю такие единицы: " + from + " → " + to;
        String formatted = result == (long) result ? String.valueOf((long) result) : String.format("%.4f", result);
        return String.format("Конвертация выполнена, босс: %s %s = %s %s", fromParts[0], from.toUpperCase(), formatted, to.toUpperCase());
    }

    private double convertValue(double v, String from, String to) {
        double base = toBase(v, from);
        if (Double.isNaN(base)) return Double.NaN;
        return fromBase(base, from, to);
    }

    private double toBase(double v, String unit) {
        return switch (unit) {
            case "usd" -> v;
            case "eur" -> v / 0.92;
            case "rub" -> v / 90.0;
            case "gbp" -> v / 0.79;
            case "km" -> v;
            case "mi" -> v * 1.60934;
            case "m" -> v / 1000.0;
            case "ft" -> v * 0.0003048;
            case "kg" -> v;
            case "lb" -> v * 0.453592;
            case "g" -> v / 1000.0;
            case "c" -> v;
            case "f" -> (v - 32) * 5.0 / 9.0;
            case "k" -> v - 273.15;
            default -> Double.NaN;
        };
    }

    private double fromBase(double base, String from, String to) {
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

    private String getIP() {
        try {
            RestTemplate rt = new RestTemplate();
            String ip = rt.getForObject("https://api.ipify.org", String.class);
            historyRepository.save(new History("ip", ip));
            return "Босс, ваш внешний IP-адрес: " + ip;
        } catch (Exception e) {
            return "Босс, не удалось получить IP. Проверьте подключение к сети.";
        }
    }

    private String getJoke() {
        try {
            RestTemplate rt = new RestTemplate();
            @SuppressWarnings("unchecked")
            java.util.Map joke = rt.getForObject("https://official-joke-api.appspot.com/random_joke", java.util.Map.class);
            String result = joke.get("setup") + "\n\n" + joke.get("punchline");
            historyRepository.save(new History("joke", result));
            return "Босс, вот вам шутка: " + result;
        } catch (Exception e) {
            String[] jokes = {
                "Босс, знаете почему программисты путают Хэллоуин и Рождество? Потому что Oct 31 = Dec 25!",
                "Босс, это не баг — это недокументированная фича. Так говорил ещё Тони Старк.",
                "Босс, SQL запрос заходит в бар, подходит к двум таблицам и спрашивает: 'Можно присоединиться?'"
            };
            String joke = jokes[(int)(Math.random() * jokes.length)];
            historyRepository.save(new History("joke", joke));
            return joke;
        }
    }

    private String getQuote() {
        String[] quotes = {
            "«Я — Железный Человек.» — Тони Старк",
            "«Иногда нужно бежать, прежде чем научишься ходить.» — Тони Старк",
            "«Герои — не те, у кого есть суперсилы, а те, кто делает правильные вещи.» — Стив Роджерс",
            "«Единственный способ делать хорошую работу — любить то, что делаешь.» — Стив Джобс",
            "«Программирование — это искусство организации сложности.» — Эдсгер Дейкстра",
            "«Любая достаточно продвинутая технология неотличима от магии.» — Артур Кларк"
        };
        String quote = quotes[(int)(Math.random() * quotes.length)];
        historyRepository.save(new History("quote", quote));
        return "Босс, вот мудрость дня: " + quote;
    }

    private String translate(String text) {
        if (text.isEmpty()) return "Босс, что перевести? Пример: переведи Hello world";
        String result = groqService.chat("Переведи на русский язык, ответь только переводом без пояснений: " + text);
        historyRepository.save(new History("translate", text + " → " + result));
        return "Перевод: " + result;
    }

    private String handlePomodoro(String sub) {
        sub = sub.trim().toLowerCase();
        if (sub.equals("start") || sub.isEmpty() || sub.equals("старт") || sub.equals("начни")) {
            return "🍅 POMODORO:25:00";
        } else if (sub.equals("break") || sub.equals("перерыв") || sub.equals("отдых")) {
            return "☕ BREAK:05:00";
        } else if (sub.equals("long break") || sub.equals("длинный перерыв") || sub.equals("большой перерыв")) {
            return "🛋️ LONGBREAK:15:00";
        }
        return "Босс, доступно: помодоро старт, помодоро перерыв, помодоро длинный перерыв";
    }

    private String handleTimer(String subCommand) {
        try {
            int seconds = Integer.parseInt(subCommand.trim());
            if (seconds > 0) {
                return "Таймер установлен на " + seconds + " секунд, босс. Я уведомлю вас по завершении.";
            }
            return "Босс, время должно быть положительным числом.";
        } catch (NumberFormatException e) {
            return "Босс, укажите количество секунд. Например: таймер 60";
        }
    }


    public List<String> getNotes() {
        return noteRepository.findAll().stream()
                .map(Note::getText)
                .collect(Collectors.toList());
    }

    public void clearNotes() {
        noteRepository.deleteAll();
    }

    private String takeScreenshot() {
        try {
            // Принудительно устанавливаем headless в false
            System.setProperty("java.awt.headless", "false");

            File directory = new File("screenshots");
            if (!directory.exists()) directory.mkdirs();

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "screenshot_" + timestamp + ".png";
            File outputFile = new File(directory, fileName);

            // Проверка на наличие графической среды
            if (GraphicsEnvironment.isHeadless()) {
                return "Босс, система работает в безголовом режиме. Скриншот невозможен.";
            }

            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            Robot robot = new Robot();
            BufferedImage capture = robot.createScreenCapture(screenRect);
            
            ImageIO.write(capture, "png", outputFile);

            return "Скриншот успешно сделан, босс! Файл сохранен в папку 'screenshots' как: " + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при создании скриншота: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
        }
    }

    private String playAnthem() {
        String videoId = "Lbdus4ySlG0";
        return "Алға, Қазақстан! Включаю гимн прямо в чате, босс:\\n\\n" +
               "<div class='embedded-video'>" +
               "<iframe width='100%' height='240' src='https://www.youtube.com/embed/" + videoId + "' " +
               "frameborder='0' allow='accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture' allowfullscreen></iframe>" +
               "</div>";
    }

    private String scanProject() {
        try {
            File root = new File("src/main/java");
            final long[] fileCount = {0};
            final long[] lineCount = {0};
            List<String> classNames = new ArrayList<>();

            java.nio.file.Files.walk(root.toPath())
                .filter(java.nio.file.Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(p -> {
                    fileCount[0]++;
                    classNames.add(p.getFileName().toString());
                    try {
                        lineCount[0] += java.nio.file.Files.lines(p).count();
                    } catch (Exception e) {}
                });

            StringBuilder sb = new StringBuilder("[TRIGGER_SCAN] Босс, глубокое сканирование систем завершено:\\n\\n");
            sb.append("📋 ОБЪЕКТ: Проект «Personal Assistant»\\n");
            sb.append("📂 СТРУКТУРА: ").append(fileCount[0]).append(" Java-классов обнаружено\\n");
            sb.append("🔢 ОБЪЕМ: ").append(lineCount[0]).append(" строк чистого кода\\n");
            sb.append("⚡ СТАТУС: Все модули функционируют в штатном режиме.\\n\\n");
            sb.append("Последние просканированные модули: ").append(classNames.stream().limit(3).collect(Collectors.joining(", ")));

            return sb.toString();
        } catch (Exception e) {
            return "Босс, возникла ошибка при доступе к исходному коду: " + e.getMessage();
        }
    }

    private String getLocation() {
        try {
            RestTemplate rt = new RestTemplate();
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> data = rt.getForObject("http://ip-api.com/json/", java.util.Map.class);
            
            if (data != null && "success".equals(data.get("status"))) {
                return String.format("[TRIGGER_GEOLOCATION] Босс, спутниковое наведение завершено:\\n" +
                        "📍 МЕСТО: %s, %s\\n" +
                        "🌐 КООРДИНАТЫ: %s, %s\\n" +
                        "🏢 ПРОВАЙДЕР: %s\\n" +
                        "Ваше текущее местоположение подтверждено. Связь стабильна.",
                        data.get("city"), data.get("country"),
                        data.get("lat"), data.get("lon"),
                        data.get("isp"));
            }
            return "Босс, не удалось точно определить ваши координаты. Проверьте настройки сети.";
        } catch (Exception e) {
            return "Босс, системы геолокации временно недоступны.";
        }
    }

    private String getSystemStatus() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory() / (1024 * 1024);
            long allocatedMemory = runtime.totalMemory() / (1024 * 1024);
            long freeMemory = runtime.freeMemory() / (1024 * 1024);
            long usedMemory = allocatedMemory - freeMemory;
            
            int processors = runtime.availableProcessors();
            String os = System.getProperty("os.name");
            String arch = System.getProperty("os.arch");
            String javaVer = System.getProperty("java.version");

            return String.format("[TRIGGER_SYS_STATS] Босс, текущее состояние ядра:\\n\\n" +
                    "🖥️ ОС: %s (%s)\\n" +
                    "☕ JAVA: v%s\\n" +
                    "🧠 ЦП: %d потоков доступно\\n" +
                    "💾 ОЗУ: %d МБ / %d МБ (Макс: %d МБ)\\n" +
                    "📈 ЗАГРУЗКА: Все системы работают стабильно.",
                    os, arch, javaVer, processors, usedMemory, allocatedMemory, maxMemory);
        } catch (Exception e) {
            return "Босс, не удалось получить данные от системного монитора.";
        }
    }
}
