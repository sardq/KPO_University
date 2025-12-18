package com.journal.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class ProfilePage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//h3[text()='Профиль']")
    private WebElement header;

    @FindBy(name = "login")
    private WebElement loginInput;

    @FindBy(name = "email")
    private WebElement emailInput;

    @FindBy(name = "firstName")
    private WebElement firstNameInput;

    @FindBy(name = "lastName")
    private WebElement lastNameInput;

    @FindBy(xpath = "//label[text()='Роль']/following-sibling::input")
    private WebElement roleInput;

    @FindBy(xpath = "//button[text()='Сохранить']")
    private WebElement saveButton;

    @FindBy(css = ".alert-info")
    private WebElement groupAlert;

    @FindBy(css = "ul li")
    private List<WebElement> disciplineListItems;

    @FindBy(xpath = "//div[contains(@class, 'toast-body')]")
    private WebElement toastMessage;

    public ProfilePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void open() {
        driver.get("http://localhost:3000/profile"); 
    }

    public void verifyIsOpened() {
        wait.until(ExpectedConditions.visibilityOf(header));
    }

    public void waitForDataToLoad() {
        wait.until(d -> {
            String val = loginInput.getAttribute("value");
            return val != null && !val.isEmpty();
        });
    }

    public String getLogin() { return loginInput.getAttribute("value"); }
    public String getEmail() { return emailInput.getAttribute("value"); }
    public String getFirstName() { return firstNameInput.getAttribute("value"); }
    public String getLastName() { return lastNameInput.getAttribute("value"); }
    public String getRole() { return roleInput.getAttribute("value"); }

    public boolean isLoginEnabled() { return loginInput.isEnabled(); }
    public boolean isFirstNameEnabled() { return firstNameInput.isEnabled(); }

    public void setFirstName(String firstName) {
        wait.until(ExpectedConditions.visibilityOf(firstNameInput));
        firstNameInput.clear();
        firstNameInput.sendKeys(firstName);
    }

    public void setLastName(String lastName) {
        wait.until(ExpectedConditions.visibilityOf(lastNameInput));
        lastNameInput.clear();
        lastNameInput.sendKeys(lastName);
    }

    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();
    }

    public String getGroupName() {
        wait.until(ExpectedConditions.visibilityOf(groupAlert));
        return groupAlert.getText();
    }

    public List<String> getDisciplinesList() {
        return disciplineListItems.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

public String getToastMessage() {
        wait.until(ExpectedConditions.visibilityOf(toastMessage));
        return toastMessage.getText();
    }
}