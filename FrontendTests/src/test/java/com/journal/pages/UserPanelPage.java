package com.journal.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class UserPanelPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//input[contains(@placeholder, 'Поиск')]")
    private WebElement searchInput;

    @FindBy(xpath = "//input[contains(@placeholder, 'Поиск')]/following-sibling::button[1]")
    private WebElement searchButton;
    
    @FindBy(xpath = "//input[contains(@placeholder, 'Поиск')]/following-sibling::button[2]")
    private WebElement clearSearchButton;

    @FindBy(xpath = "//select[option[@value='STUDENT']]")
    private WebElement roleFilterSelect;
    
    @FindBy(name = "firstName")
    private WebElement firstNameInput;

    @FindBy(name = "lastName")
    private WebElement lastNameInput;

    @FindBy(name = "email")
    private WebElement emailInput;

    @FindBy(name = "role")
    private WebElement roleSelect;

    @FindBy(xpath = "//div[@class='modal-footer']//button[text()='Сохранить']")
    private WebElement modalSaveButton;

    @FindBy(xpath = "//div[contains(@class, 'toast-body')]")
    private WebElement toastMessage;

    public UserPanelPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void verifyIsOpened() {
        wait.until(ExpectedConditions.visibilityOf(searchInput));
    }

    public void searchUser(String text) {
        wait.until(ExpectedConditions.visibilityOf(searchInput)).clear();
        searchInput.sendKeys(text);
        searchButton.click();
        try { Thread.sleep(500); } catch (InterruptedException e) {}
    }
    
    public void filterByRole(String roleValue) {
        wait.until(ExpectedConditions.visibilityOf(roleFilterSelect));
        new Select(roleFilterSelect).selectByValue(roleValue);
        try { Thread.sleep(500); } catch (InterruptedException e) {}
    }

    
    public boolean isUserPresent(String login) {
        return !driver.findElements(By.xpath("//tr/td[2][text()='" + login + "']")).isEmpty();
    }

    public String getUserFullName(String login) {
        String xpath = String.format("//tr[td[2][text()='%s']]/td[1]", login);
        return driver.findElement(By.xpath(xpath)).getText();
    }

    public void clickEditUser(String login) {
        String xpath = String.format("//tr[td[2][text()='%s']]//button[contains(@class, 'btn-outline-warning')]", login);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
    }

    public void clickDeleteUser(String login) {
        String xpath = String.format("//tr[td[2][text()='%s']]//button[contains(@class, 'btn-outline-danger')]", login);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
    }
    public String getToastMessage() {
        wait.until(ExpectedConditions.visibilityOf(toastMessage));
        return toastMessage.getText();
    }
    public void editUserData(String firstName, String lastName) {
    wait.until(ExpectedConditions.visibilityOf(firstNameInput));

    if (firstName != null) {
        firstNameInput.clear();
        firstNameInput.sendKeys(firstName);
    }

    if (lastName != null) {
        lastNameInput.clear();
        lastNameInput.sendKeys(lastName);
    }

    modalSaveButton.click();
}
}