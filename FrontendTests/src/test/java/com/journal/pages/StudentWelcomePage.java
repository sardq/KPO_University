package com.journal.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class StudentWelcomePage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//h1[contains(text(), 'Личный кабинет студента')]")
    private WebElement pageHeader;

    @FindBy(xpath = "//h2[contains(@class, 'card-title')]")
    private WebElement welcomeMessage;

    @FindBy(xpath = "//button[contains(., 'Перейти')]")
    private WebElement journalButton;

    public StudentWelcomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public boolean isPageOpened() {
        try {
            wait.until(ExpectedConditions.visibilityOf(pageHeader));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getWelcomeText() {
        wait.until(ExpectedConditions.visibilityOf(welcomeMessage));
        return welcomeMessage.getText();
    }

    public StudentJournalPage clickGoToJournal() {
        wait.until(ExpectedConditions.elementToBeClickable(journalButton)).click();
        return new StudentJournalPage(driver);
    }
}