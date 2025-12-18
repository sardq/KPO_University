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

public class DisciplinePage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//button[contains(., 'Создать')]")
    private WebElement createButton;

    @FindBy(xpath = "//input[@placeholder='Поиск по названию дисциплины...']")
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

    @FindBy(xpath = "//input[@placeholder='Поиск преподавателей...']")
    private WebElement teacherSearchInput;

    @FindBy(xpath = "//button[text()='Добавить выбранных']")
    private WebElement addSelectedTeachersButton;

    public DisciplinePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void verifyIsOpened() {
        wait.until(ExpectedConditions.visibilityOf(createButton));
    }

    public void searchDiscipline(String name) {
        wait.until(ExpectedConditions.visibilityOf(searchInput)).clear();
        searchInput.sendKeys(name);
        driver.findElement(By.xpath("//input[@placeholder='Поиск по названию дисциплины...']/following-sibling::button[1]")).click();
        try { Thread.sleep(500); } catch (InterruptedException e) {}
    }

    public void createDiscipline(String name) {
        createButton.click();
        wait.until(ExpectedConditions.visibilityOf(nameInput));
        nameInput.sendKeys(name);
        modalCreateButton.click();
        wait.until(ExpectedConditions.invisibilityOf(nameInput)); 
    }

    public void editDiscipline(String oldName, String newName) {
        String xpath = String.format("//tr[td[strong[text()='%s']]]//button[@title='Редактировать']", oldName);
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
        
        wait.until(ExpectedConditions.visibilityOf(nameInput));
        nameInput.clear();
        nameInput.sendKeys(newName);
        modalSaveButton.click();
        wait.until(ExpectedConditions.invisibilityOf(nameInput));
    }

    public void deleteDiscipline(String name) {
        String xpath = String.format("//tr[td[strong[text()='%s']]]//button[@title='Удалить']", name);

wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
        
        wait.until(ExpectedConditions.elementToBeClickable(modalDeleteButton)).click();
        wait.until(ExpectedConditions.invisibilityOf(modalDeleteButton));
    }

    public void openAddTeachersModal(String disciplineName) {
        String xpath = String.format("//tr[td[strong[text()='%s']]]//td[5]//button[contains(@class, 'btn-outline-primary')]", disciplineName);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
    }

    public void selectTeacherInModal(String teacherEmailOrName) {
    wait.until(ExpectedConditions.visibilityOf(teacherSearchInput));
    teacherSearchInput.clear();
    teacherSearchInput.sendKeys(teacherEmailOrName);

    By teacherOption = By.xpath("//button[contains(@class,'list-group-item') and contains(.,'" + teacherEmailOrName + "')]");
    WebElement option = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.visibilityOfElementLocated(teacherOption));

    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);

    wait.until(ExpectedConditions.elementToBeClickable(addSelectedTeachersButton)).click();
    wait.until(ExpectedConditions.invisibilityOf(addSelectedTeachersButton));
}


    public void saveTeachers() {
        wait.until(ExpectedConditions.elementToBeClickable(addSelectedTeachersButton)).click();
        wait.until(ExpectedConditions.invisibilityOf(addSelectedTeachersButton));
    }

    public boolean isDisciplinePresent(String name) {
        return !driver.findElements(By.xpath("//tr//strong[text()='" + name + "']")).isEmpty();
    }

    public String getToastMessage() {
        wait.until(ExpectedConditions.visibilityOf(toastMessage));
        return toastMessage.getText();
    }
}