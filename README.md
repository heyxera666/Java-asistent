# Personal Assistant

Веб-приложение Personal Assistant - ваш персональный помощник в браузере!

## Особенности

✨ **Веб-интерфейс** - красивая современная система с градиентом
💬 **Чат-интерфейс** - общайтесь с ассистентом как в мессенджере
📝 **Управление заметками** - добавляйте, просматривайте и удаляйте заметки
⏱️ **Таймер** - запускайте таймеры и получайте уведомления
🚀 **REST API** - полностью функциональный API для интеграции

## Структура проекта

```
Java-asistent/
├── src/
│   ├── main/
│   │   ├── java/com/personalassistant/
│   │   │   ├── PersonalAssistant.java          # Главное приложение Spring Boot
│   │   │   ├── AssistantService.java           # Бизнес-логика
│   │   │   └── AssistantController.java        # REST API контроллер
│   │   └── resources/
│   │       ├── static/index.html               # Веб-интерфейс
│   │       └── application.properties          # Конфигурация
│   └── test/java/com/personalassistant/
│       └── PersonalAssistantTest.java          # Unit тесты
├── pom.xml                                     # Maven конфигурация
└── README.md
```

## Установка и запуск

### Требования
- JDK 17 или выше
- Maven 3.8.0 или выше

### Запуск локально

1. **Скомпилировать проект:**
   ```bash
   mvn clean compile
   ```

2. **Запустить приложение:**
   ```bash
   mvn spring-boot:run
   ```

3. **Откройте в браузере:**
   ```
   http://localhost:8080
   ```

### Альтернативный запуск

```bash
# Собрать JAR и запустить
mvn clean package
java -jar target/personal-assistant-1.0-SNAPSHOT.jar
```

## Команды

### Основные команды
- **hello** - приветствие от ассистента
- **help** - показать все доступные команды
- **exit** - (в консольной версии) выход

### Команды заметок
- **note add \<текст\>** - добавить новую заметку
- **note list** - показать все заметки
- **note delete \<номер\>** - удалить заметку по номеру

### Команды таймера
- **timer \<секунды\>** - запустить таймер на указанное количество секунд

## API Endpoints

```
POST   /api/assistant/command     - Обработать команду
GET    /api/assistant/notes       - Получить все заметки
DELETE /api/assistant/notes       - Очистить все заметки
GET    /api/assistant/health      - Проверка здоровья приложения
```

### Пример использования API

```bash
# Отправить команду
curl -X POST http://localhost:8080/api/assistant/command \
  -H "Content-Type: application/json" \
  -d '{"command":"note add My task"}'

# Получить заметки
curl http://localhost:8080/api/assistant/notes

# Проверка здоровья
curl http://localhost:8080/api/assistant/health
```

## Технологический стек

- **Backend**: Spring Boot 3.2.0, Java 17
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **Build**: Maven
- **Server**: Embedded Tomcat

## Развитие проекта

Планы на будущее:
- [ ] Добавить рассчитанные напоминания
- [ ] Сохранение заметок в БД
- [ ] Поддержка голосовых команд
- [ ] Темная/светлая тема
- [ ] Синхронизация между устройствами
- [ ] Интеграция с календарем
- [ ] Экспорт заметок

## Лицензия

MIT

