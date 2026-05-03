package com.personalassistant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit тесты для AssistantService.
 */
@SpringBootTest
public class PersonalAssistantTest {

    @Autowired
    private AssistantService assistantService;

    @BeforeEach
    void setUp() {
        // Очистить заметки перед каждым тестом
        assistantService.clearNotes();
    }

    @Test
    void testHelloCommand() {
        String response = assistantService.processCommand("hello");
        assertTrue(response.contains("Привет"));
    }

    @Test
    void testHelpCommand() {
        String response = assistantService.processCommand("help");
        assertTrue(response.contains("команды"));
        assertTrue(response.contains("note"));
        assertTrue(response.contains("timer"));
    }

    @Test
    void testAddNote() {
        String response = assistantService.processCommand("note add Test note");
        assertTrue(response.contains("добавлена"));
        assertEquals(1, assistantService.getNotes().size());
    }

    @Test
    void testListNotes() {
        assistantService.processCommand("note add First note");
        assistantService.processCommand("note add Second note");
        
        String response = assistantService.processCommand("note list");
        assertTrue(response.contains("First note"));
        assertTrue(response.contains("Second note"));
    }

    @Test
    void testEmptyNotesList() {
        String response = assistantService.processCommand("note list");
        assertTrue(response.contains("нет"));
    }

    @Test
    void testDeleteNote() {
        assistantService.processCommand("note add To delete");
        String response = assistantService.processCommand("note delete 1");
        assertTrue(response.contains("удалена"));
        assertEquals(0, assistantService.getNotes().size());
    }

    @Test
    void testTimerCommand() {
        String response = assistantService.processCommand("timer 10");
        assertTrue(response.contains("Таймер"));
        assertTrue(response.contains("10"));
    }

    @Test
    void testInvalidTimerCommand() {
        String response = assistantService.processCommand("timer abc");
        assertTrue(response.contains("Неверный"));
    }

    @Test
    void testUnknownCommand() {
        String response = assistantService.processCommand("unknown command");
        assertTrue(response.contains("Неизвестная"));
    }

    @Test
    void testEmptyCommand() {
        String response = assistantService.processCommand("");
        assertTrue(response.contains("введите"));
    }

    @Test
    void testCaseSensitivity() {
        String response1 = assistantService.processCommand("HELLO");
        String response2 = assistantService.processCommand("Hello");
        String response3 = assistantService.processCommand("hello");
        
        assertEquals(response1, response2);
        assertEquals(response2, response3);
    }
}
