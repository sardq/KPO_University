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
import java.util.List;

public class JournalPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//select[option[text()='Выберите дисциплину']]")
    private WebElement disciplineSelect;

    @FindBy(xpath = "//select[option[text()='Выберите группу']]")
    private WebElement groupSelect;

    @FindBy(xpath = "//button[contains(text(), 'Создать занятие')]")
    private WebElement createExerciseButton;

    @FindBy(tagName = "table")
    private WebElement gradeTable;

    @FindBy(css = ".modal-content")
    private WebElement modalContent;

    @FindBy(xpath = "//div[@class='modal-footer']//button[contains(text(), 'Сохранить')]")
    private WebElement modalSaveButton;

    @FindBy(css = ".alert-danger")
    private WebElement errorAlert;

    @FindBy(css = "input[type='datetime-local']")
    private WebElement dateInput;

    @FindBy(xpath = "//label[text()='Описание']/following-sibling::textarea")
    private WebElement descriptionInput;

    @FindBy(xpath = "//div[@class='modal-body']//select")
    private WebElement gradeSelect;

    public JournalPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void verifyIsOpened() {
        wait.until(ExpectedConditions.visibilityOf(disciplineSelect));
    }

    public void selectDiscipline(String name) {
        wait.until(ExpectedConditions.visibilityOf(disciplineSelect));
        new Select(disciplineSelect).selectByVisibleText(name);
    }

    public void selectGroup(String name) {
        wait.until(ExpectedConditions.elementToBeClickable(groupSelect));
        wait.until(d -> new Select(groupSelect).getOptions().size() > 1);
        new Select(groupSelect).selectByVisibleText(name);
    }

    public void clickCreateExercise() {
        wait.until(ExpectedConditions.elementToBeClickable(createExerciseButton)).click();
    }

    public void fillExerciseForm(String dateKeys, String description) {
        wait.until(ExpectedConditions.visibilityOf(dateInput));

        dateInput.sendKeys(dateKeys);

        if (description != null) {
            descriptionInput.clear();
            descriptionInput.sendKeys(description);
        }
    }

    public void clickSaveInModal() {
        wait.until(ExpectedConditions.elementToBeClickable(modalSaveButton)).click();
        // Ждем, пока модалка исчезнет
        wait.until(ExpectedConditions.invisibilityOf(modalContent));
    }

    public void clickSaveInModalExpectingError() {
        wait.until(ExpectedConditions.elementToBeClickable(modalSaveButton)).click();
    }

    public String getErrorMessage() {
        wait.until(ExpectedConditions.visibilityOf(errorAlert));
        return errorAlert.getText();
    }

    public void clickCell(int studentIndex, int exerciseIndex) {
        wait.until(ExpectedConditions.visibilityOf(gradeTable));
         this.wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        List<WebElement> rows = gradeTable.findElements(By.cssSelector("tbody tr"));
        WebElement row = rows.get(studentIndex);

        List<WebElement> cells = row.findElements(By.tagName("td"));
        cells.get(exerciseIndex + 1).click();
    }

    public void setGrade(String gradeValue) {
        wait.until(ExpectedConditions.visibilityOf(gradeSelect));
        new Select(gradeSelect).selectByValue(gradeValue);
    }

    public boolean isExercisePresentInHeader(String timeString) {
        wait.until(ExpectedConditions.visibilityOf(gradeTable));
        // Ищем в thead
        List<WebElement> headers = gradeTable.findElements(By.cssSelector("thead th"));
        for (WebElement th : headers) {
            if (th.getText().contains(timeString)) {
                return true;
            }
        }
        return false;
    }

    public String getGradeValue(int studentIndex, int exerciseIndex) {
        wait.until(ExpectedConditions.visibilityOf(gradeTable));
        List<WebElement> rows = gradeTable.findElements(By.cssSelector("tbody tr"));
        WebElement row = rows.get(studentIndex);
        List<WebElement> cells = row.findElements(By.tagName("td"));
        return cells.get(exerciseIndex + 1).getText();
    }
}