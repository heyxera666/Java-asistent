# Personal Assistant

Консольное Java-приложение для персонального ассистента.

## Структура проекта

```
Java-asistent/
├── src/
│   ├── main/java/com/personalassistant/
│   │   └── PersonalAssistant.java  # Основной класс
│   └── test/java/com/personalassistant/
│       └── PersonalAssistantTest.java  # Тесты (пока пустой)
├── pom.xml  # Maven конфигурация
└── README.md
```

## Запуск

1. Установи JDK 17+ и Maven.
2. Скомпилируй: `mvn compile`
3. Запусти: `mvn exec:java`

## Команды

- hello: приветствие
- help: справка
- exit: выход

## Развитие

Добавим: заметки, таймер, напоминания.
