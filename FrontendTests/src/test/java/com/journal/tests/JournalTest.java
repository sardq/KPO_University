package com.journal.tests;

import org.junit.jupiter.api.Test;

import com.journal.pages.JournalPage;
import com.journal.pages.LoginPage;
import com.journal.pages.TeacherWelcomePage;

import static org.junit.jupiter.api.Assertions.*;

public class JournalTest extends BaseTest {

    private static final String TEACHER_LOGIN = "i.ivan";
    private static final String TEACHER_PASS = "12345";
    private static final String DISCIPLINE_NAME = "Информационные системы";
    private static final String GROUP_NAME = "ФИТ-22-1";

    @Test
    public void testCreateExerciseFlow() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAs(TEACHER_LOGIN, TEACHER_PASS);

        TeacherWelcomePage welcomePage = new TeacherWelcomePage(driver);
        JournalPage journalPage = welcomePage.clickJournal();

        journalPage.selectDiscipline(DISCIPLINE_NAME);
        journalPage.selectGroup(GROUP_NAME);

        journalPage.clickCreateExercise();

        String dateKeys = "10102025T12:00:00";

        journalPage.fillExerciseForm(dateKeys, "Лекция по Selenium");
        journalPage.clickSaveInModal();

        assertTrue(journalPage.isExercisePresentInHeader("12:00"),
                "Созданное занятие должно отображаться в шапке таблицы");
    }

    @Test
    public void testGradingFlow() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAs(TEACHER_LOGIN, TEACHER_PASS);

        TeacherWelcomePage welcomePage = new TeacherWelcomePage(driver);
        JournalPage journalPage = welcomePage.clickJournal();

        journalPage.selectDiscipline(DISCIPLINE_NAME);
        journalPage.selectGroup(GROUP_NAME);

        journalPage.clickCell(0, 0);

        journalPage.setGrade("5");
        journalPage.clickSaveInModal();

        String value = journalPage.getGradeValue(0, 0);
        assertEquals("5", value, "Оценка в ячейке должна быть 5");
    }

    @Test
    public void testCreateExerciseValidation() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAs(TEACHER_LOGIN, TEACHER_PASS);

        JournalPage journalPage = new TeacherWelcomePage(driver).clickJournal();
        journalPage.selectDiscipline(DISCIPLINE_NAME);
        journalPage.selectGroup(GROUP_NAME);

        journalPage.clickCreateExercise();

        journalPage.clickSaveInModalExpectingError();

        String error = journalPage.getErrorMessage();
        assertEquals("Укажите дату и время занятия", error);
    }
}