package de.triology.universeadm.csvimport;

import de.triology.universeadm.BaseDirectory;
import de.triology.universeadm.EntityException;
import de.triology.universeadm.configuration.ApplicationConfiguration;
import de.triology.universeadm.configuration.I18nConfiguration;
import de.triology.universeadm.configuration.Language;
import de.triology.universeadm.group.Group;
import de.triology.universeadm.group.GroupManager;
import de.triology.universeadm.mail.MailSender;
import de.triology.universeadm.user.PasswordGenerator;
import de.triology.universeadm.user.User;
import de.triology.universeadm.user.UserManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class CSVImportManagerTest {
    public static final String SUBJECT = "New Account for CES";
    private static final String EXIST = "exist";
    private static final String MAIL_CONTENT = "Willkommen zum Cloudogu Ecosystem!\n" +
            "Dies ist ihr Benutzeraccount\n" +
            "Benutzername = %s\n" +
            "Passwort = %s\n" +
            "Bei der ersten Anmeldung müssen sie ihr Passwort ändern";
    private static final String NOT_ENOUGH_COLUMNS = "test;test2;test3";
    private static final String TO_MANY_COLUMNS = "test;test2;test3;test4;test5;test6;test7";
    private static final String NO_HEADER = "\n" +
            "Tester1;Tes;Ter;Tester1;test1@test.com;";
    private static final String HEADERS_AND_ONE_USER = "Username;FirstName;Surname;DisplayName;EMail;Group\n" +
            "Tester1;Tes;Ter;Tester1;test1@test.com;exist";
    private static final String HEADERS_AND_TWO_USERS = "Username;FirstName;Surname;DisplayName;EMail;Group\n" +
            "Tester1;Tes;Ter;Tester1;test1@test.com;\n" +
            "Tester2;Tes;Ter;Tester2;test2@test.com;exist";
    private static final String HEADERS_TWO_USERS_AND_EMPTY_LINE_BETWEEN = "Username;FirstName;Surname;DisplayName;EMail;Group\n" +
            "Tester1;Tes;Ter;Tester1;test1@test.com;" +
            "\n" +
            "\nTester2;Tes;Ter;Tester2;test2@test.com;";
    private static final String HEADERS_TWO_USERS_AND_BROKEN_LINE_BETWEEN = "Username;FirstName;Surname;DisplayName;EMail;Group\n" +
            "Tester1;Tes;Ter;Tester1;test1@test.com;\n" +
            "asd;\n" +
            "Tester2;Tes;Ter;Tester2;test2@test.com;";
    private static final String HEADERS_AND_EMPTY_USER = "Username;FirstName;Surname;DisplayName;EMail;Group\n" +
            ";;;;";
    private static final String HEADERS_WITH_ONE_USER_AND_NON_EXISTENT_GROUP = "Username;FirstName;Surname;DisplayName;EMail;Group\n" +
            "Tester1;Tes;Ter;Tester1;test1@test.com;doesnotexist";
    private static final String HEADERS_AND_ONE_USER_WITH_GROUP = "Username;FirstName;Surname;DisplayName;EMail;Group\n" +
            "Tester2;Tes;Ter;Tester2;test2@test.com;exist";
    private static final String PROTOCOL_ENTRY_EMPTY_USER = "Fehler in Zeile 2. Benutzer nicht angelegt. Fehlermeldung: " +
            "Benutzername ist leer, " +
            "DisplayName ist leer, " +
            "Nachname ist leer, " +
            "Mail ist leer";
    private static final String PROTOCOL_ENTRY_ALREADY_EXIST = "Tester1: konnte nicht angelegt werden(Nutzer existiert bereits)";
    private static final String PROTOCOL_ENTRY_GROUP_DOES_NOT_EXIST = "Tester1: doesnotexist existiert nicht";
    private static final String PROTOCOL_ENTRY_USER_IS_MEMBER_OF_GROUP = "Tester2: Nutzer ist bereits Teil von exist";
    private User user1;
    private User user2;
    private GroupManager groupManager;
    private UserManager userManager;
    private ProtocolWriter.ProtocolWriterBuilder protocolWriterBuilder;
    private ProtocolWriter protocolWriter;
    private CSVImportManager csvImportManager;
    private PasswordGenerator pwdGen;
    private I18nConfiguration languageConfigs;
    private ApplicationConfiguration applicationConfig;
    private MailSender mailSender;

    @Before
    public void setUp() {
        userManager = mockUserManager();
        groupManager = mockGroupManager();
        protocolWriter = mockProtocolWriter();
        protocolWriterBuilder = mockProtocolWriterBuilder();
        mailSender = mockMailSender();
        pwdGen = mockPasswordGenerator();
        languageConfigs = mockLanguageConfigs();
        applicationConfig = mockApplicationConfig();

        csvImportManager = new CSVImportManager(userManager, groupManager, protocolWriterBuilder, mailSender, pwdGen, languageConfigs, applicationConfig);

        user1 = new User(
                "Tester1",
                "Tester1",
                "Tes",
                "Ter",
                "test1@test.com",
                "temp",
                true,
                new ArrayList<>());

        user2 = new User(
                "Tester2",
                "Tester2",
                "Tes",
                "Ter",
                "test2@test.com",
                "temp",
                true,
                new ArrayList<>()
        );
    }

    private InputStream stringToStream(final String input) {
        return new ByteArrayInputStream(input.getBytes());
    }

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("universeadm.home", "src/test/resources/");
    }


    @Test(expected = IllegalArgumentException.class)
    public void throwsIllegalArgumentExceptionWhenNotEnoughColumnsInHeadline() throws IOException {
        this.csvImportManager.importUsers(this.stringToStream(NOT_ENOUGH_COLUMNS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsIllegalArgumentExceptionWhenTooManyColumnsInHeadline() throws IOException {
        this.csvImportManager.importUsers(this.stringToStream(TO_MANY_COLUMNS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsIllegalArgumentExceptionOnEmptyFirstLine() throws IOException {
        this.csvImportManager.importUsers(this.stringToStream(NO_HEADER));
    }

    @Test
    public void generatesOneUserSuccessful() throws IOException {
        this.csvImportManager.importUsers(this.stringToStream(HEADERS_AND_ONE_USER));
        verify(userManager).create(user1);
    }

    @Test
    public void generateMultipleUsersSuccessful() throws IOException {
        this.csvImportManager.importUsers(this.stringToStream(HEADERS_AND_TWO_USERS));

        verify(userManager).create(user1);
        verify(userManager).create(user2);
    }

    @Test
    public void generateMultipleUsersWithEmptyLineSuccessful() throws IOException {
        this.csvImportManager.importUsers(this.stringToStream(HEADERS_TWO_USERS_AND_EMPTY_LINE_BETWEEN));
        verify(userManager).create(user1);
        verify(userManager).create(user2);
    }

    @Test
    public void generateMultipleUsersWithWrongLineSuccessful() throws IOException {
        this.csvImportManager.importUsers(this.stringToStream(HEADERS_TWO_USERS_AND_BROKEN_LINE_BETWEEN));
        verify(userManager).create(user1);
        verify(userManager).create(user2);
    }

    @Test
    public void missingAnyContent() throws IOException {
        this.csvImportManager.importUsers(this.stringToStream(HEADERS_AND_EMPTY_USER));
        verify(protocolWriter).writeLine(PROTOCOL_ENTRY_EMPTY_USER);
    }

    @Test
    public void userExistAlreadyAndGroupAddedSuccessful() throws IOException {
        user1.getMemberOf().add(EXIST);
        when(userManager.get(user1.getUsername())).thenReturn(user1);
        when(groupManager.get(EXIST)).thenReturn(new Group(EXIST));
        this.csvImportManager.importUsers(this.stringToStream(HEADERS_AND_ONE_USER));
        verify(protocolWriter).writeLine(PROTOCOL_ENTRY_ALREADY_EXIST);
        verify(userManager).modify(user1);
    }

    @Test
    public void emailExistAlready() throws IOException {
        when(userManager.get(user1.getUsername())).thenReturn(null);
        Mockito.doThrow(new EntityException("UNIQUE_EMAIL")).when(userManager).create(user1);
        this.csvImportManager.importUsers(this.stringToStream(HEADERS_AND_ONE_USER));
        verify(protocolWriter).writeLine("Tester1: Die Mail für diesen Nutzer wird bereits verwendet.");
    }

    @Test
    public void groupDoesNotExist() throws IOException {
        when(userManager.get(user1.getUsername())).thenReturn(user1);
        this.csvImportManager.importUsers(this.stringToStream(HEADERS_WITH_ONE_USER_AND_NON_EXISTENT_GROUP));
        verify(protocolWriter).writeLine(PROTOCOL_ENTRY_GROUP_DOES_NOT_EXIST);
    }

    @Test
    public void userAlreadyMemberOfGroup() throws IOException {
        user2.getMemberOf().add(EXIST);
        when(groupManager.get(EXIST)).thenReturn(new Group(EXIST));
        when(userManager.get(user2.getUsername())).thenReturn(user2);
        this.csvImportManager.importUsers(this.stringToStream(HEADERS_AND_ONE_USER_WITH_GROUP));
        verify(protocolWriter).writeLine(PROTOCOL_ENTRY_USER_IS_MEMBER_OF_GROUP);

    }

    @Test
    public void sendsMailSuccessful() throws IOException, MessagingException {
        when(pwdGen.random(10)).thenReturn("abc123!$?A");
        String content = String.format(MAIL_CONTENT, "Tester1", "abc123!$?A");
        this.csvImportManager.importUsers(this.stringToStream(HEADERS_AND_ONE_USER));
        verify(mailSender).sendMail(eq(SUBJECT), eq(content), eq(user1.getMail()));

    }

    private GroupManager mockGroupManager() {
        return mock(GroupManager.class);
    }

    private UserManager mockUserManager() {
        return mock(UserManager.class);
    }

    private ProtocolWriter mockProtocolWriter() {
        return mock(ProtocolWriter.class);
    }

    private ProtocolWriter.ProtocolWriterBuilder mockProtocolWriterBuilder() {
        ProtocolWriter.ProtocolWriterBuilder pWBuilder = mock(ProtocolWriter.ProtocolWriterBuilder.class);
        when(pWBuilder.build(Matchers.anyString())).thenReturn(protocolWriter);
        return pWBuilder;
    }

    private MailSender mockMailSender() {
        return mock(MailSender.class);
    }

    private PasswordGenerator mockPasswordGenerator() {
        return mock(PasswordGenerator.class);
    }

    private I18nConfiguration mockLanguageConfigs() {
        return new I18nConfiguration(Language.en, Language.de);
    }

    private ApplicationConfiguration mockApplicationConfig() {
        return BaseDirectory.getConfiguration("application-configuration.xml", ApplicationConfiguration.class);

    }
}