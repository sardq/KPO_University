package com.journal.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By loginInput = By.name("login");
    private By passwordInput = By.name("password");
    private By loginButton = By.xpath("//button[text()='Войти']");
    private By errorToast = By.className("toast-body");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open() {
        driver.get("http://localhost:3000/login");
    }

    public void enterLogin(String login) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(loginInput));
        input.clear();
        input.sendKeys(login);
    }

    public void enterPassword(String password) {
        driver.findElement(passwordInput).sendKeys(password);
    }

    public void clickLoginButton() {
        driver.findElement(loginButton).click();
    }

    public void loginAs(String login, String password) {
        enterLogin(login);
        enterPassword(password);
        clickLoginButton();
        new WebDriverWait(driver, Duration.ofSeconds(10))
    .until(webDriver -> ((JavascriptExecutor) webDriver)
        .executeScript("return localStorage.getItem('token')") != null);
    }
    public void loginAsError(String login, String password) {
        enterLogin(login);
        enterPassword(password);
        clickLoginButton();
    }
    public boolean isErrorToastVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(errorToast)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}