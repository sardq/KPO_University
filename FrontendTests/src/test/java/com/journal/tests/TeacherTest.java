package com.journal.tests;

import org.junit.jupiter.api.Test;

import com.journal.pages.JournalPage;
import com.journal.pages.LoginPage;
import com.journal.pages.StatisticPage;
import com.journal.pages.TeacherWelcomePage;

import static org.junit.jupiter.api.Assertions.*;

public class TeacherTest extends BaseTest {

    @Test
    public void testTeacherLoginAndNavigation() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAs("i.ivan", "12345");

        TeacherWelcomePage welcomePage = new TeacherWelcomePage(driver);
        welcomePage.verifyIsOpened();

        JournalPage journalPage = welcomePage.clickJournal();
        journalPage.verifyIsOpened();

        driver.navigate().back();
        welcomePage.verifyIsOpened();

        StatisticPage statsPage = welcomePage.clickStatistics();
        statsPage.verifyIsOpened();
    }

    @Test
    public void testDisciplineStatistics() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAs("i.ivan", "12345");

        TeacherWelcomePage welcomePage = new TeacherWelcomePage(driver);
        StatisticPage statsPage = welcomePage.clickStatistics();

        String disciplineName = "Информационные системы";

        statsPage.selectDisciplineForAvg(disciplineName);
        statsPage.clickGetDisciplineAvg();

        String result = statsPage.getDisciplineAvgResult();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.matches("\\d+\\.\\d{2}"), "Результат должен быть числом с 2 знаками, получили: " + result);
    }

    @Test
    public void testReportGenerationFlow() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAs("i.ivan", "12345");

        TeacherWelcomePage welcomePage = new TeacherWelcomePage(driver);
        StatisticPage statsPage = welcomePage.clickStatistics();

        statsPage.openReportTab();
        statsPage.prepareReport("Математический анализ", "ПМИ-21-1");
        statsPage.clickDownloadReport();

    }
}