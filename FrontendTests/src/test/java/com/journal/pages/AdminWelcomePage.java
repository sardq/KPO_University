package com.journal.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class AdminWelcomePage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//h1[contains(text(), 'Панель администратора')]")
    private WebElement header;

    @FindBy(xpath = "//h4[contains(text(), 'Дисциплины')]/..//button")
    private WebElement disciplinesButton;

    @FindBy(xpath = "//h4[contains(text(), 'Пользователи')]/..//button")
    private WebElement usersButton;

    @FindBy(xpath = "//h4[contains(text(), 'Группы')]/..//button")
    private WebElement groupsButton;

    public AdminWelcomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void verifyIsOpened() {
        wait.until(ExpectedConditions.visibilityOf(header));
    }

    public DisciplinePage clickDisciplines() {
        wait.until(ExpectedConditions.elementToBeClickable(disciplinesButton)).click();
        return new DisciplinePage(driver);
    }
     public GroupPage clickGroups() {
        wait.until(ExpectedConditions.elementToBeClickable(groupsButton)).click();
        return new GroupPage(driver);
    }
    public UserPanelPage clickUsers() {
        wait.until(ExpectedConditions.elementToBeClickable(usersButton)).click();
        return new UserPanelPage(driver);
    }
}