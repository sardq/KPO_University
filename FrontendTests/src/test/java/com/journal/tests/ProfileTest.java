package com.journal.tests;

import com.journal.pages.LoginPage;
import com.journal.pages.ProfilePage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ProfileTest extends BaseTest {

    private static final String ADMIN_LOGIN = "s.sergey";
    private static final String ADMIN_PASS = "admin";
    
    private static final String STUDENT_LOGIN = "a.anna"; 
    private static final String STUDENT_PASS = "12345";

    @Test
    public void testAdminEditProfile() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAs(ADMIN_LOGIN, ADMIN_PASS);

        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.open();
        profilePage.verifyIsOpened();
        profilePage.waitForDataToLoad();

        Assertions.assertTrue(profilePage.isFirstNameEnabled(), "Поле Имя должно быть активно для Админа");

        String oldFirstName = profilePage.getFirstName();
        String newFirstName = "AdminEdited";

        profilePage.setFirstName(newFirstName);
        profilePage.clickSave();

        Assertions.assertTrue(profilePage.getToastMessage().contains("Профиль обновлен"), 
                "Нет сообщения об успехе");

        driver.navigate().refresh();
        profilePage.waitForDataToLoad();

        Assertions.assertEquals(newFirstName, profilePage.getFirstName(), 
                "Имя не сохранилось после перезагрузки страницы");

        try { Thread.sleep(2000); } catch (Exception e){}
        
        profilePage.setFirstName(oldFirstName);
        profilePage.clickSave();
        Assertions.assertTrue(profilePage.getToastMessage().contains("Профиль обновлен"));
    }

    @Test
    public void testStudentReadOnlyProfile() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAs(STUDENT_LOGIN, STUDENT_PASS);

        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.open();
        profilePage.verifyIsOpened();
        profilePage.waitForDataToLoad();

        Assertions.assertFalse(profilePage.isLoginEnabled(), "Логин должен быть заблокирован для студента");
        Assertions.assertFalse(profilePage.isFirstNameEnabled(), "Имя должно быть заблокировано для студента");
        
        Assertions.assertEquals("STUDENT", profilePage.getRole(), "Роль отображается неверно");

        String groupText = profilePage.getGroupName();
        Assertions.assertTrue(groupText.contains("Группа:"), "Информация о группе не отображается");

        List<String> disciplines = profilePage.getDisciplinesList();
        System.out.println("Дисциплины студента: " + disciplines);
        
        Assertions.assertNotNull(disciplines);
    }
    
    @Test
    public void testValidationAdminProfile() {

        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAs(ADMIN_LOGIN, ADMIN_PASS);
        
        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.open();
        profilePage.waitForDataToLoad();
        
        profilePage.setFirstName(" "); 
        profilePage.clickSave();
        
        String toast = profilePage.getToastMessage();
        Assertions.assertTrue(toast.contains("Имя должно быть минимум 2 символа"), 
                "Валидация имени не сработала. Сообщение: " + toast);
    }
}