package com.personalassistant;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Personal Assistant - консольное Java-приложение.
 * Цель: выполнять команды пользователя и расширяться.
 */
public class PersonalAssistant {

    private static List<String> notes = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Привет! Я Personal Assistant. Введите команду (help для списка):");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            String command = input.toLowerCase();

            if (command.equals("hello")) {
                System.out.println("Привет! Как я могу помочь?");
            } else if (command.equals("help")) {
                showHelp();
            } else if (command.equals("exit")) {
                System.out.println("До свидания!");
                scanner.close();
                return;
            } else if (command.startsWith("note ")) {
                handleNote(input.substring(5).trim());
            } else if (command.startsWith("timer ")) {
                handleTimer(input.substring(6).trim());
            } else {
                System.out.println("Неизвестная команда. Введите 'help' для списка.");
            }
        }
    }

    private static void showHelp() {
        System.out.println("Доступные команды:");
        System.out.println("- hello: приветствие");
        System.out.println("- help: показать эту справку");
        System.out.println("- note add <текст>: добавить заметку");
        System.out.println("- note list: показать все заметки");
        System.out.println("- timer <секунды>: запустить таймер");
        System.out.println("- exit: выйти из программы");
    }

    private static void handleNote(String subCommand) {
        if (subCommand.startsWith("add ")) {
            String text = subCommand.substring(4).trim();
            if (!text.isEmpty()) {
                notes.add(text);
                System.out.println("Заметка добавлена: " + text);
            } else {
                System.out.println("Текст заметки пустой.");
            }
        } else if (subCommand.equals("list")) {
            if (notes.isEmpty()) {
                System.out.println("Заметок нет.");
            } else {
                System.out.println("Ваши заметки:");
                for (int i = 0; i < notes.size(); i++) {
                    System.out.println((i + 1) + ". " + notes.get(i));
                }
            }
        } else {
            System.out.println("Неверная команда заметок. Используйте 'note add <текст>' или 'note list'.");
        }
    }

    private static void handleTimer(String subCommand) {
        try {
            int seconds = Integer.parseInt(subCommand);
            if (seconds > 0) {
                startTimer(seconds);
            } else {
                System.out.println("Время должно быть положительным числом.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат времени. Введите число секунд.");
        }
    }

    private static void startTimer(int seconds) {
        System.out.println("Таймер запущен на " + seconds + " секунд.");
        try {
            Thread.sleep(seconds * 1000L);
            System.out.println("Время вышло!");
        } catch (InterruptedException e) {
            System.out.println("Таймер прерван.");
        }
    }
}