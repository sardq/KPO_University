package com.journal.tests;

import com.journal.pages.AdminWelcomePage;
import com.journal.pages.LoginPage;
import com.journal.pages.UserPanelPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdminUserTest extends BaseTest {

    private static final String ADMIN_LOGIN = "s.sergey";
    private static final String ADMIN_PASS = "admin"; 
    
    private static final String TARGET_LOGIN = "a.anna5";
    private static final String ORIGINAL_FIRST_NAME = "Фамилия"; 
    private static final String EDITED_FIRST_NAME = "Фамилия_Edited";

    @Test
    public void testSearchAndEditUser() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAs(ADMIN_LOGIN, ADMIN_PASS);

        AdminWelcomePage adminPanel = new AdminWelcomePage(driver);
        UserPanelPage userPage = adminPanel.clickUsers();
        userPage.verifyIsOpened();

        userPage.searchUser(TARGET_LOGIN);
        Assertions.assertTrue(userPage.isUserPresent(TARGET_LOGIN), 
                "Пользователь " + TARGET_LOGIN + " не найден через поиск");

        userPage.clickEditUser(TARGET_LOGIN);
        
        userPage.editUserData(EDITED_FIRST_NAME, null);
        
        Assertions.assertTrue(userPage.getToastMessage().contains("Пользователь обновлен"),
                "Нет сообщения об успешном обновлении");

        userPage.searchUser(TARGET_LOGIN); 
        String fullName = userPage.getUserFullName(TARGET_LOGIN);
        Assertions.assertTrue(fullName.contains(EDITED_FIRST_NAME), 
                "Имя в таблице не обновилось. Текущее: " + fullName);

        try { Thread.sleep(2500); } catch (InterruptedException e){}
        
        userPage.clickEditUser(TARGET_LOGIN);
        userPage.editUserData(ORIGINAL_FIRST_NAME, null);
        
    }

    @Test
    public void testRoleFilter() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAs(ADMIN_LOGIN, ADMIN_PASS);
        
        UserPanelPage userPage = new AdminWelcomePage(driver).clickUsers();
        
        userPage.filterByRole("STUDENT");
        
        userPage.searchUser(TARGET_LOGIN);
        Assertions.assertTrue(userPage.isUserPresent(TARGET_LOGIN), 
                "Студент должен отображаться при фильтре STUDENT");
        
        userPage.filterByRole("ADMIN");
        userPage.searchUser(TARGET_LOGIN);
        Assertions.assertFalse(userPage.isUserPresent(TARGET_LOGIN), 
                "Студент НЕ должен отображаться при фильтре ADMIN");
    }
}