package com.journal.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class GroupPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//button[contains(., 'Создать')]")
    private WebElement createButton;

    @FindBy(xpath = "//input[@placeholder='Поиск по названию группы...']")
    private WebElement searchInput;

    @FindBy(css = ".input-group button svg[data-icon='search']")
    private WebElement searchButtonIcon;

    @FindBy(xpath = "//div[contains(@class, 'toast-body')]")
    private WebElement toastMessage;

    @FindBy(name = "name")
    private WebElement nameInput;

    @FindBy(xpath = "//div[contains(@class, 'modal-footer')]//button[text()='Создать']")
    private WebElement modalCreateButton;

    @FindBy(xpath = "//div[contains(@class, 'modal-footer')]//button[text()='Сохранить']")
    private WebElement modalSaveButton;

    @FindBy(xpath = "//div[contains(@class, 'modal-footer')]//button[text()='Удалить']")
    private WebElement modalDeleteButton;

    @FindBy(xpath = "//input[@placeholder='Поиск студентов...']")
    private WebElement studentSearchInput;

    @FindBy(xpath = "//button[text()='Добавить выбранных']")
    private WebElement addSelectedStudentsButton;

    public GroupPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void verifyIsOpened() {
        wait.until(ExpectedConditions.visibilityOf(createButton));
    }

    public void searchGroup(String name) {
        wait.until(ExpectedConditions.visibilityOf(searchInput)).clear();
        searchInput.sendKeys(name);
        driver.findElement(By.xpath("//input[@placeholder='Поиск по названию группы...']/following-sibling::button[1]")).click();
        try { Thread.sleep(500); } catch (InterruptedException e) {}
    }

    public void createGroup(String name) {
        wait.until(ExpectedConditions.visibilityOf(createButton));
        createButton.click();
        wait.until(ExpectedConditions.visibilityOf(nameInput));
        nameInput.sendKeys(name);
        modalCreateButton.click();
        wait.until(ExpectedConditions.invisibilityOf(nameInput));
    }

    public void editGroup(String oldName, String newName) {
        String xpath = String.format("//tr[td[strong[text()='%s']]]//button[@title='Редактировать']", oldName);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();

        wait.until(ExpectedConditions.visibilityOf(nameInput));
        nameInput.clear();
        nameInput.sendKeys(newName);
        modalSaveButton.click();
        wait.until(ExpectedConditions.invisibilityOf(nameInput));
    }

    public void deleteGroup(String name) {
        String xpath = String.format("//tr[td[strong[text()='%s']]]//button[@title='Удалить']", name);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();

        wait.until(ExpectedConditions.elementToBeClickable(modalDeleteButton)).click();
        wait.until(ExpectedConditions.invisibilityOf(modalDeleteButton));
    }

    public void openAddStudentsModal(String groupName) {

        String xpath = String.format("//tr[td[strong[text()='%s']]]//button[@title='Добавить студентов']", groupName);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
    }

    public void openManageStudentsModal(String groupName) {
        String xpath = String.format("//tr[td[strong[text()='%s']]]//button[@title='Управление студентами']", groupName);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
    }

    public void selectStudentInModal(String studentIdentifier) {
        wait.until(ExpectedConditions.visibilityOf(studentSearchInput));
        studentSearchInput.clear();
        studentSearchInput.sendKeys(studentIdentifier);

        By teacherOption = By.xpath("//button[contains(@class, 'list-group-item') and contains(., '" + studentIdentifier + "')]");
        WebElement option = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(teacherOption));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
    }

    public void saveStudents() {
        wait.until(ExpectedConditions.elementToBeClickable(addSelectedStudentsButton)).click();
        wait.until(ExpectedConditions.invisibilityOf(addSelectedStudentsButton));
    }
    
    public void removeStudentInModal(String studentIdentifier) {
    String xpath = String.format(
        "//div[contains(@class,'d-flex') and .//small[contains(., '%s')]]//button[@title='Отвязать от группы']",
        studentIdentifier
    );

    WebElement removeButton = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));

    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", removeButton);
}

    
    public void closeManageModal() {
        driver.findElement(By.xpath("//button[text()='Закрыть']")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[contains(@class, 'modal-backdrop')]")));
    }

    public boolean isGroupPresent(String name) {
        return !driver.findElements(By.xpath("//tr//strong[text()='" + name + "']")).isEmpty();
    }

    public String getToastMessage() {
        wait.until(ExpectedConditions.visibilityOf(toastMessage));
        return toastMessage.getText();
    }
}