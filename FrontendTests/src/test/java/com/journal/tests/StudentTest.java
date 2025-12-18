package com.journal.tests;

import com.journal.pages.LoginPage;
import com.journal.pages.StudentJournalPage;
import com.journal.pages.StudentWelcomePage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StudentTest extends BaseTest {

    @Test
    public void testSuccessfulLoginAndJournalLoad() {
        LoginPage loginPage = new LoginPage(driver);

        String testLogin = "a.anna";
        String testPassword = "12345";

        loginPage.open();
        loginPage.loginAs(testLogin, testPassword);

        StudentWelcomePage welcomePage = new StudentWelcomePage(driver);

        Assertions.assertTrue(welcomePage.isPageOpened(),
                "Не удалось попасть на страницу 'Личный кабинет студента' после логина");

        Assertions.assertTrue(welcomePage.getWelcomeText().contains("safasf@ufsfser.user"),
                "На главной странице не отображается email пользователя. Текст: " + welcomePage.getWelcomeText());

        StudentJournalPage journalPage = welcomePage.clickGoToJournal();

        Assertions.assertTrue(journalPage.isPageOpened(),
                "Страница журнала не открылась после нажатия кнопки 'Перейти'");

        journalPage.selectDisciplineByIndex(2);

        Assertions.assertTrue(journalPage.isTableDisplayed(),
                "Таблица оценок не появилась после выбора дисциплины");

        Assertions.assertTrue(journalPage.getRowCount() > 0,
                "Таблица пуста, хотя должна содержать данные");
    }
}