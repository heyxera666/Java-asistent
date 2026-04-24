package com.personalassistant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Personal Assistant - оконное Java-приложение (Swing).
 * Цель: выполнять команды пользователя в графическом интерфейсе.
 */
public class PersonalAssistant {

    private static List<String> notes = new ArrayList<>();
    private static JTextArea chatArea;
    private static JTextField inputField;

    public static void main(String[] args) {
        // Создаем графический интерфейс в потоке обработки событий (EDT)
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Personal Assistant");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLayout(new BorderLayout());

        // Область для вывода текста (чат)
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        chatArea.setBackground(new Color(30, 30, 30));
        chatArea.setForeground(new Color(220, 220, 220));
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Панель ввода внизу
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        
        inputField = new JTextField();
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        inputField.setBackground(new Color(50, 50, 50));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        
        JButton sendButton = new JButton("Отправить");
        sendButton.setBackground(new Color(70, 130, 180));
        sendButton.setForeground(Color.WHITE);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        frame.add(inputPanel, BorderLayout.SOUTH);

        // Обработка ввода
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText().trim();
                if (!input.isEmpty()) {
                    printMessage("Вы", input);
                    processCommand(input);
                    inputField.setText("");
                }
            }
        };

        inputField.addActionListener(actionListener);
        sendButton.addActionListener(actionListener);

        frame.setLocationRelativeTo(null); // По центру экрана
        frame.setVisible(true);

        printMessage("Ассистент", "Привет! Я Personal Assistant. Введите команду (help для списка).");
    }

    private static void printMessage(String sender, String message) {
        chatArea.append("[" + sender + "]: " + message + "\n\n");
        // Прокрутка вниз
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private static void processCommand(String input) {
        String command = input.toLowerCase();

        if (command.equals("hello")) {
            printMessage("Ассистент", "Привет! Как я могу помочь?");
        } else if (command.equals("help")) {
            showHelp();
        } else if (command.equals("exit")) {
            printMessage("Ассистент", "До свидания!");
            System.exit(0);
        } else if (command.startsWith("note ")) {
            handleNote(input.substring(5).trim());
        } else if (command.startsWith("timer ")) {
            handleTimer(input.substring(6).trim());
        } else {
            printMessage("Ассистент", "Неизвестная команда. Введите 'help' для списка.");
        }
    }

    private static void showHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("Доступные команды:\n");
        sb.append("- hello: приветствие\n");
        sb.append("- help: показать эту справку\n");
        sb.append("- note add <текст>: добавить заметку\n");
        sb.append("- note list: показать все заметки\n");
        sb.append("- timer <секунды>: запустить таймер\n");
        sb.append("- exit: выйти из программы");
        printMessage("Ассистент", sb.toString());
    }

    private static void handleNote(String subCommand) {
        if (subCommand.startsWith("add ")) {
            String text = subCommand.substring(4).trim();
            if (!text.isEmpty()) {
                notes.add(text);
                printMessage("Ассистент", "Заметка добавлена: " + text);
            } else {
                printMessage("Ассистент", "Текст заметки пустой.");
            }
        } else if (subCommand.equals("list")) {
            if (notes.isEmpty()) {
                printMessage("Ассистент", "Заметок нет.");
            } else {
                StringBuilder sb = new StringBuilder("Ваши заметки:\n");
                for (int i = 0; i < notes.size(); i++) {
                    sb.append((i + 1)).append(". ").append(notes.get(i)).append("\n");
                }
                printMessage("Ассистент", sb.toString().trim());
            }
        } else {
            printMessage("Ассистент", "Неверная команда заметок. Используйте 'note add <текст>' или 'note list'.");
        }
    }

    private static void handleTimer(String subCommand) {
        try {
            int seconds = Integer.parseInt(subCommand);
            if (seconds > 0) {
                startTimer(seconds);
            } else {
                printMessage("Ассистент", "Время должно быть положительным числом.");
            }
        } catch (NumberFormatException e) {
            printMessage("Ассистент", "Неверный формат времени. Введите число секунд.");
        }
    }

    private static void startTimer(int seconds) {
        printMessage("Ассистент", "Таймер запущен на " + seconds + " секунд.");
        
        // Запускаем таймер в отдельном потоке, чтобы не зависало окно (Swing EDT)
        new Thread(() -> {
            try {
                Thread.sleep(seconds * 1000L);
                SwingUtilities.invokeLater(() -> {
                    printMessage("Ассистент", "⏰ Время вышло! Прошло " + seconds + " секунд.");
                    Toolkit.getDefaultToolkit().beep(); // Звуковой сигнал
                });
            } catch (InterruptedException e) {
                SwingUtilities.invokeLater(() -> printMessage("Ассистент", "Таймер прерван."));
            }
        }).start();
    }
}