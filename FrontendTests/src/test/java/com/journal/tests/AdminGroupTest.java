package com.journal.tests;

import com.journal.pages.AdminWelcomePage;
import com.journal.pages.GroupPage;
import com.journal.pages.LoginPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class AdminGroupTest extends BaseTest {

    private static final String ADMIN_LOGIN = "s.sergey";
    private static final String ADMIN_PASS = "admin"; 
    
    private static final String STUDENT_IDENTIFIER = "safasf3@ufsfser."; 

    @Test
    public void testGroupLifecycle() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAs(ADMIN_LOGIN, ADMIN_PASS);

        AdminWelcomePage adminPanel = new AdminWelcomePage(driver);
        GroupPage groupPage = adminPanel.clickGroups();
        groupPage.verifyIsOpened();

        String groupName = "Grp " + UUID.randomUUID().toString().substring(0, 5);
        String newGroupName = groupName + " Upd";

        groupPage.createGroup(groupName);
        Assertions.assertTrue(groupPage.getToastMessage().contains("успешно создана"));
        
        groupPage.searchGroup(groupName);
        Assertions.assertTrue(groupPage.isGroupPresent(groupName), "Группа не найдена после создания");

        groupPage.editGroup(groupName, newGroupName);
        
        groupPage.searchGroup(newGroupName);
        Assertions.assertTrue(groupPage.isGroupPresent(newGroupName), "Группа не найдена после переименования");

        groupPage.deleteGroup(newGroupName);
        
        groupPage.searchGroup(newGroupName);
        Assertions.assertFalse(groupPage.isGroupPresent(newGroupName), "Группа не удалилась");
    }

    @Test
    public void testAddStudentToGroup() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAs(ADMIN_LOGIN, ADMIN_PASS);

        AdminWelcomePage adminPanel = new AdminWelcomePage(driver);
        GroupPage groupPage = adminPanel.clickGroups();

        String groupName = "StudentsGrp " + UUID.randomUUID().toString().substring(0, 4);

        groupPage.createGroup(groupName);
        groupPage.searchGroup(groupName);

        groupPage.openAddStudentsModal(groupName);
        groupPage.selectStudentInModal(STUDENT_IDENTIFIER);
        groupPage.saveStudents();

        try { Thread.sleep(2000); } catch (Exception e){}
        
        groupPage.openManageStudentsModal(groupName);
        groupPage.removeStudentInModal(STUDENT_IDENTIFIER);
        
        groupPage.closeManageModal();

        try { Thread.sleep(1000); } catch (Exception e){}
        groupPage.deleteGroup(groupName);
    }
}