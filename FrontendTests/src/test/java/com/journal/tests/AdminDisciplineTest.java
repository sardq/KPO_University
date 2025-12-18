package com.journal.tests;

import com.journal.pages.AdminWelcomePage;
import com.journal.pages.DisciplinePage;
import com.journal.pages.LoginPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class AdminDisciplineTest extends BaseTest {

    private static final String ADMIN_LOGIN = "s.sergey";
    private static final String ADMIN_PASS = "admin"; 

    @Test
    public void testDisciplineLifecycle() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAs(ADMIN_LOGIN, ADMIN_PASS);

        AdminWelcomePage adminPage = new AdminWelcomePage(driver);
        adminPage.verifyIsOpened();
        
        DisciplinePage disciplinePage = adminPage.clickDisciplines();
        disciplinePage.verifyIsOpened();

        String disciplineName = "TestDisc_" + UUID.randomUUID().toString().substring(0, 5);
        String updatedName = disciplineName + "_Upd";

        disciplinePage.createDiscipline(disciplineName);
        
        Assertions.assertTrue(disciplinePage.getToastMessage().contains("успешно создана"), 
                "Нет сообщения об успехе создания");
        
        disciplinePage.searchDiscipline(disciplineName);
        Assertions.assertTrue(disciplinePage.isDisciplinePresent(disciplineName), 
                "Дисциплина не найдена в таблице после создания");

        disciplinePage.editDiscipline(disciplineName, updatedName);
        
        disciplinePage.searchDiscipline(updatedName);
        Assertions.assertTrue(disciplinePage.isDisciplinePresent(updatedName), 
                "Дисциплина не найдена после переименования");

        disciplinePage.deleteDiscipline(updatedName);
        
        disciplinePage.searchDiscipline(updatedName);
        Assertions.assertFalse(disciplinePage.isDisciplinePresent(updatedName), 
                "Дисциплина все еще присутствует в таблице после удаления");
    }

    @Test
    public void testAddTeacherToDiscipline() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAs(ADMIN_LOGIN, ADMIN_PASS);
        
        AdminWelcomePage adminPage = new AdminWelcomePage(driver);
        adminPage.verifyIsOpened();
        
        DisciplinePage disciplinePage = adminPage.clickDisciplines();
        disciplinePage.verifyIsOpened();;

        String disciplineName = "Math_" + UUID.randomUUID().toString().substring(0, 4);
        disciplinePage.createDiscipline(disciplineName);
        disciplinePage.searchDiscipline(disciplineName);

        String teacherIdentifier = "jbsdk@asd"; 
        
        disciplinePage.openAddTeachersModal(disciplineName);
        disciplinePage.selectTeacherInModal(teacherIdentifier);

        
        try { Thread.sleep(2000); } catch (Exception e){}
        disciplinePage.deleteDiscipline(disciplineName);
    }
}