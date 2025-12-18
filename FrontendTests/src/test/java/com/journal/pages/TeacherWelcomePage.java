package com.journal.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class TeacherWelcomePage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//h1[contains(text(), 'Главная преподавателя')]")
    private WebElement pageHeader;

    @FindBy(xpath = "//h4[text()='Электронный журнал']/..//button")
    private WebElement journalButton;

    @FindBy(xpath = "//h4[text()='Статистика']/..//button")
    private WebElement statisticsButton;

    public TeacherWelcomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void verifyIsOpened() {
        wait.until(ExpectedConditions.visibilityOf(pageHeader));
    }

    public JournalPage clickJournal() {
        wait.until(ExpectedConditions.elementToBeClickable(journalButton)).click();
        return new JournalPage(driver);
    }

    public StatisticPage clickStatistics() {
        wait.until(ExpectedConditions.elementToBeClickable(statisticsButton)).click();
        return new StatisticPage(driver);
    }
}