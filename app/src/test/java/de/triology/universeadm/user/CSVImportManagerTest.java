package de.triology.universeadm.user;

import de.triology.universeadm.group.Group;
import de.triology.universeadm.group.GroupManager;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class CSVImportManagerTest {
    static User user1;

    static User user2;

    private GroupManager groupManager;
    private UserManager userManager;

    private ProtocolWriter protocolWriter;
    private CSVImportManager csvImportManager;

    @Before
    public void setUp() {
        this.userManager = mockUserManager();
        this.groupManager = mockGroupManager();
        this.protocolWriter = mockProtocolWriter();
        this.csvImportManager = new CSVImportManager(userManager, groupManager, protocolWriter);
        user1 = new User(
                "Tester1",
                "Tester1",
                "Tes",
                "Ter",
                "test1@test.com",
                "temp",
                true,
                new ArrayList<String>());
        user2 = new User(
                "Tester2",
                "Tester2",
                "Tes",
                "Ter",
                "test2@test.com",
                "temp",
                true,
                new ArrayList<String>()
        );
    }

    private InputStream stringToStream(final String input) {
        return new ByteArrayInputStream(input.getBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsIllegalArgumentExceptionWhenNotEnoughColumnsInHeadline() throws IOException {
        this.csvImportManager.importUsers(this.stringToStream("test;test2;test3"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsIllegalArgumentExceptionWhenTooManyColumnsInHeadline() throws IOException {
        this.csvImportManager.importUsers(this.stringToStream("test;test2;test3;test4;test5;test6;test7"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsIllegalArgumentExceptiononEmptyFirstLine() throws IOException {
        this.csvImportManager.importUsers(this.stringToStream("\nTester1;Tes;Ter;Tester1;test1@test.com;"));
    }

    @Test
    public void generatesOneUserSuccessful() throws IOException {
        this.csvImportManager.importUsers(this.stringToStream("Username;FirstName;Surname;DisplayName;EMail;Group\n" +
                "Tester1;Tes;Ter;Tester1;test1@test.com"));
        verify(userManager).create(user1);
    }

    @Test
    public void generateMultipleUsersSuccessful() throws IOException {
        this.csvImportManager.importUsers(this.stringToStream("Username;FirstName;Surname;DisplayName;EMail;Group\n" +
                "Tester1;Tes;Ter;Tester1;test1@test.com;\n" +
                "Tester2;Tes;Ter;Tester2;test2@test.com;exist"));
        verify(userManager).create(user1);
        verify(userManager).create(user2);
    }

    @Test
    public void generateMultipleUsersWithEmptyLineSuccessful() throws IOException {
        this.csvImportManager.importUsers(this.stringToStream("Username;FirstName;Surname;DisplayName;EMail;Group\n" +
                "Tester1;Tes;Ter;Tester1;test1@test.com;\n" +
                "\n" +
                "Tester2;Tes;Ter;Tester2;test2@test.com;"));
        verify(userManager).create(user1);
        verify(userManager).create(user2);
    }

    @Test
    public void generateMultipleUsersWithWrongLineSuccessful() throws IOException {
        this.csvImportManager.importUsers(this.stringToStream("Username;FirstName;Surname;DisplayName;EMail;Group\n" +
                "Tester1;Tes;Ter;Tester1;test1@test.com;\n" +
                "asd;\n" +
                "Tester2;Tes;Ter;Tester2;test2@test.com;"));

        verify(userManager).create(user1);
        verify(userManager).create(user2);
    }

    @Test
    public void missingAnyContent() throws IOException {
        this.csvImportManager.importUsers(this.stringToStream("Username;FirstName;Surname;DisplayName;EMail;Group\n" +
                ";;;;\n"));

        verify(protocolWriter).writeLine("Fehler in Zeile 2 Benutzer nicht angelegt. Errors: Nutzername ist leer, DisplayName ist leer, Surname ist leer, Mail ist leer");
    }

    @Test
    public void userExistAlreadyAndGroupAddedsuccessful() throws IOException {
        user1.getMemberOf().add("exist");
        when(userManager.get(user1.getUsername())).thenReturn(user1);
        when(groupManager.get("exist")).thenReturn(new Group("exist"));
        this.csvImportManager.importUsers(this.stringToStream("Username;FirstName;Surname;DisplayName;EMail;Group\n" +
                "Tester1;Tes;Ter;Tester1;test1@test.com;exist"));
        verify(protocolWriter).writeLine("Tester1: konnte nicht angelegt werden(Nutzer existiert bereits)");
        verify(userManager).modify(user1);
    }

    @Test
    public void groupDoesNotExist() throws IOException {
        when(userManager.get(user1.getUsername())).thenReturn(user1);
        this.csvImportManager.importUsers(this.stringToStream("Username;FirstName;Surname;DisplayName;EMail;Group\n" +
                "Tester1;Tes;Ter;Tester1;test1@test.com;doesnotexist"));
        verify(protocolWriter).writeLine("Tester1: doesnotexist existiert nicht");

    }

    @Test
    public void userAlreadyMemberOfGroup() throws IOException {
        user2.getMemberOf().add("exist");
        when(groupManager.get("exist")).thenReturn(new Group("exist"));
        when(userManager.get(user2.getUsername())).thenReturn(user2);
        this.csvImportManager.importUsers(this.stringToStream("Username;FirstName;Surname;DisplayName;EMail;Group\n" +
                "Tester2;Tes;Ter;Tester2;test2@test.com;exist"));
        verify(protocolWriter).writeLine("Tester2: Nutzer ist bereits Teil von exist");

    }

    /**
     * Method description
     *
     * @return
     */
    private GroupManager mockGroupManager() {
        return mock(GroupManager.class);
    }

    /**
     * Method description
     *
     * @return
     */
    private UserManager mockUserManager() {
        return mock(UserManager.class);
    }

    private ProtocolWriter mockProtocolWriter() {
        return mock(ProtocolWriter.class);
    }
}
