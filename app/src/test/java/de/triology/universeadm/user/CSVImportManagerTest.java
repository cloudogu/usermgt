package de.triology.universeadm.user;

import de.triology.universeadm.group.GroupManager;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CSVImportManagerTest {
    final static User user1 = new User(
            "Tester1",
            "Tester1",
            "Tes",
            "Ter",
            "test1@test.com",
            "temp",
            true,
            new ArrayList<String>()
    );

    final static User user2 = new User(
            "Tester2",
            "Tester2",
            "Tes",
            "Ter",
            "test2@test.com",
            "temp",
            true,
            new ArrayList<String>()
    );

    private GroupManager groupManager;
    private UserManager userManager;
    private CSVImportManager csvImportManager;

    @Before
    public void setUp() {
        this.userManager = mockUserManager();
        this.groupManager = mockGroupManager();
        this.csvImportManager = new CSVImportManager(userManager, groupManager);
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
                "Tester2;Tes;Ter;Tester2;test2@test.com"));
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

/*    @Test
    public void missingAnyContent() throws IOException {
        this.csvImportManager.importUsers(this.stringToStream("Username;FirstName;Surname;DisplayName;EMail;Group\n" +
                ";;;;;\n"));

        verify(csvImportManager).createProtocolEntry("Fehler in Zeile 2 Benutzer nicht angelegt. Errors: Nutzername ist leer, DisplayName ist leer, Surname ist leer, Mail ist leer");
    }*/

    @Test
    public void userExistAlready() throws IOException {

    }

    @Test
    public void groupAddedSuccesful() throws IOException {

    }

    @Test
    public void groupDoesNotExist() throws IOException {

    }

    @Test
    public void userAlreadyMemberOfGroup() throws IOException {

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
//        writer.writeToProtocol("test");
//        writer.writeToProtocol("added 1 user");
//        writer.writeToProtocol("added group a to user");
//
//
//        InOrder inOrder = inOrder(writer);
//
//        inOrder.verify(writer).writeToProtocol("test");
//        inOrder.verify(writer).writeToProtocol("added 1 user");
//        inOrder.verify(writer).writeToProtocol("added group a to user");

}
