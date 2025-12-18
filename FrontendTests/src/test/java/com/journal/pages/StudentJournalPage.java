package com.journal.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class StudentJournalPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By pageHeader = By.tagName("h1");
    private By disciplineSelect = By.tagName("select");
    private By journalTable = By.tagName("table");
    private By userGreeting = By.xpath("//h4[contains(text(), 'Вы вошли как:')]");

    public StudentJournalPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isPageOpened() {
        try {
            wait.until(ExpectedConditions.urlContains("/studentJournalPanel"));
            return driver.findElement(pageHeader).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserGreetingText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(userGreeting)).getText();
    }

    public void selectDisciplineByIndex(int index) {
        WebElement selectElement = wait.until(ExpectedConditions.elementToBeClickable(disciplineSelect));
        Select dropdown = new Select(selectElement);
        dropdown.selectByIndex(index);
    }

    public void selectDisciplineByText(String text) {
        WebElement selectElement = wait.until(ExpectedConditions.elementToBeClickable(disciplineSelect));
        Select dropdown = new Select(selectElement);
        dropdown.selectByVisibleText(text);
    }

    public boolean isTableDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(journalTable)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public int getRowCount() {
         try {
        Thread.sleep(5000); 
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        return rows.size();
    }
}