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

public class StatisticPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//h3[contains(text(), 'Статистика и отчёты')]")
    private WebElement header;

    // Табы
    @FindBy(xpath = "//a[text()='Статистика']")
    private WebElement statsTab;

    @FindBy(xpath = "//a[text()='Отчёт']")
    private WebElement reportTab;

    public StatisticPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void verifyIsOpened() {
        wait.until(ExpectedConditions.visibilityOf(header));
    }

    public void openReportTab() {
        wait.until(ExpectedConditions.elementToBeClickable(reportTab)).click();
    }

    public void selectDisciplineForAvg(String disciplineName) {
        WebElement card = driver.findElement(By.xpath("//h5[contains(., 'Средний балл по дисциплине')]/.."));

        WebElement selectElement = card.findElement(By.tagName("select"));

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.xpath("//h5[contains(., 'Средний балл по дисциплине')]/..//select/option"), 1));

        Select select = new Select(selectElement);
        select.selectByVisibleText(disciplineName);
    }

    public void clickGetDisciplineAvg() {
        WebElement card = driver.findElement(By.xpath("//h5[contains(., 'Средний балл по дисциплине')]/.."));
        card.findElement(By.xpath(".//button[contains(text(), 'Получить')]")).click();
    }

    public String getDisciplineAvgResult() {
        By resultLocator = By.xpath("//h5[contains(., 'Средний балл по дисциплине')]/../p[contains(@class,'fs-4')]");
        WebElement result = new WebDriverWait(driver, Duration.ofSeconds(10))
        .until(ExpectedConditions.visibilityOfElementLocated(resultLocator));
        return result.getText();
    }

    public void prepareReport(String disciplineName, String groupName) {
        openReportTab();
        WebElement card = driver.findElement(By.xpath("//h5[contains(., 'Сформировать отчет')]/.."));

        WebElement discSelectEl = card.findElements(By.tagName("select")).get(0);
        WebElement groupSelectEl = card.findElements(By.tagName("select")).get(1);

        Select discSelect = new Select(discSelectEl);
        wait.until(d -> discSelect.getOptions().size() > 1);
        discSelect.selectByVisibleText(disciplineName);

        wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOf(groupSelectEl)));
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        Select groupSelect = new Select(groupSelectEl);
        groupSelect.selectByVisibleText(groupName);
    }

    public void clickDownloadReport() {
        WebElement card = driver.findElement(By.xpath("//h5[contains(., 'Сформировать отчет')]/.."));
        card.findElement(By.xpath(".//button[contains(text(), 'Сформировать')]")).click();
    }
}