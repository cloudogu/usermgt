package de.triology.universeadm.csvimport;

import com.google.inject.Inject;
import de.triology.universeadm.group.GroupManager;
import de.triology.universeadm.mail.MailSender;
import de.triology.universeadm.user.PasswordGenerator;
import de.triology.universeadm.user.User;
import de.triology.universeadm.user.UserManager;

import javax.mail.MessagingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CSVImportManager {
    private static final String COMMA_SPLIT = ",";
    private static final String SEMICOLON_SPLIT = ";";
    private static final String EMPTY_STRING = "";
    private static final String STARTING_PROTOCOL = "---Starting Protocol---";
    private static final String ENDING_PROTOCOL = "--- Ending Protocol ---";
    private static final String MAIL_CONTENT = "Willkommen zum Cloudogu Ecosystem!\n" +
            "Dies ist ihr Benutzeraccount\n" +
            "Benutzername = %s\n" +
            "Passwort = %s\n" +
            "Bei der ersten Anmeldung müssen sie ihr Passwort ändern";
    private static final String USER_PART_OF_GROUP_ALREADY = "Nutzer ist bereits Teil von %s";
    private static final String USER_ADDED = "%s zugeordnet";
    private static final String GROUP_DOESNT_EXIST = "%s existiert nicht";
    private static final String ADDED_SUCCESSFUL = "erfolgreich angelegt(%s, %s, %s, %s, %s)";
    private static final String USER_ALREADY_EXISTS = "konnte nicht angelegt werden(Nutzer existiert bereits)";
    private static final String EMPTY_USERNAME = "Nutzername ist leer";
    private static final String EMPTY_DISPLAYNAME = "DisplayName ist leer";
    private static final String EMPTY_SURNAME = "Surname ist leer";
    private static final String EMPTY_MAIL = "Mail ist leer";
    private static final String COULD_NOT_SEND_MAIL = "Mail konnte nicht versendet werden";
    private static final String SUBJECT = "New Account for CES";
    private static final String ERROR_ON_CREATING_USER = "Fehler in Zeile %d Benutzer nicht angelegt. Errors: ";
    private static final String ERROR_INFORMATION_WITH_COMMA = "%s, ";
    private static final String CSV_WITH_LINES_READ_SUCCESSFUL = "CSV-Datei mit %d Zeilen erfolgreich eingelesen.";
    private static final String INCOMPLETE_LINE = "Zeile %d ist unvollständig";

    private final GroupManager groupManager;
    private final UserManager userManager;

    private final ProtocolWriter protocolWriter;

    private final MailSender mailSender;

    private final PasswordGenerator pwdGen;

    @Inject
    public CSVImportManager(UserManager userManager, GroupManager groupManager, ProtocolWriter protocolWriter, MailSender mailSender, PasswordGenerator pwdGen) {

        this.pwdGen = pwdGen;
        this.userManager = userManager;
        this.groupManager = groupManager;
        this.protocolWriter = protocolWriter;
        this.mailSender = mailSender;
    }

    public void importUsers(InputStream inputStream) throws IOException {

        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> lines = new ArrayList<>();

        if (br.readLine().split(SEMICOLON_SPLIT).length != 6) {
            throw new IllegalArgumentException();
        }

        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        createProtocolEntry(STARTING_PROTOCOL);
        createProtocolEntry(String.format(CSV_WITH_LINES_READ_SUCCESSFUL, lines.size()));

        for (int index = 0; index < lines.size(); index++) {
            final String userDataString = lines.get(index);
            final String[] userValues = userDataString.split(SEMICOLON_SPLIT, -1);
            if (userValues.length != 6 && userValues.length != 5) {
                createProtocolEntry(String.format(INCOMPLETE_LINE, (index + 2)));
                continue;
            }

            final List<String> memberOf = new ArrayList<>();
            if (userValues.length == 6) {
                memberOf.addAll(Arrays.asList(userValues[5].split(COMMA_SPLIT)));
            }
            final String password = pwdGen.random(10);
            final User potentialNewUser = createUser(userValues, password);

            final List<String> validationErrors = getUserValidationErrors(potentialNewUser);

            if (validationErrors.size() != 0) {
                String errorMessage = String.format(ERROR_ON_CREATING_USER, (index + 2));
                for (int position = 0; position < validationErrors.size(); position++) {
                    if (position + 1 != validationErrors.size()) {
                        errorMessage += String.format(ERROR_INFORMATION_WITH_COMMA, validationErrors.get(position));
                        continue;
                    }
                    errorMessage += validationErrors.get(position);
                }
                createProtocolEntry(errorMessage);
                continue;
            }

            boolean created = addUser(potentialNewUser);
            if (created) {
                try {
                    mailSender.sendMail(SUBJECT, String.format(MAIL_CONTENT, potentialNewUser.getUsername(), password), potentialNewUser.getMail());
                } catch (MessagingException e) {
                    createProtocolEntry(potentialNewUser.getUsername(), COULD_NOT_SEND_MAIL);
                }
            }

            addGroupsToUser(potentialNewUser, memberOf);

            createProtocolEntry(ENDING_PROTOCOL);
        }
    }


    private List<String> getUserValidationErrors(final User user) {
        List<String> result = new ArrayList<>();
        if (user.getUsername().equals(EMPTY_STRING)) {
            result.add(EMPTY_USERNAME);
        }
        if (user.getDisplayName().equals(EMPTY_STRING)) {
            result.add(EMPTY_DISPLAYNAME);
        }
        if (user.getSurname().equals(EMPTY_STRING)) {
            result.add(EMPTY_SURNAME);
        }
        if (user.getMail().equals(EMPTY_STRING)) {
            result.add(EMPTY_MAIL);
        }
        return result;
    }

    private User createUser(final String[] userValues, String password) {
        return new User(
                userValues[0],
                userValues[3],
                userValues[1],
                userValues[2],
                userValues[4],
                password,
                true,
                new ArrayList<String>()
        );
    }

    private boolean addUser(User user) {
        String username = user.getUsername();
        if (!(userManager.get(username) == null)) {
            createProtocolEntry(username, USER_ALREADY_EXISTS);
            return false;
        }
        userManager.create(user);
        createProtocolEntry(username, String.format(ADDED_SUCCESSFUL,
                user.getUsername(),
                user.getGivenname(),
                user.getSurname(),
                user.getDisplayName(),
                user.getMail()));
        return true;
    }

    private void addGroupsToUser(User user, List<String> memberOf) {
        String username = user.getUsername();
        for (String group : memberOf) {
            if (groupManager.get(group) == null) {
                createProtocolEntry(username, String.format(GROUP_DOESNT_EXIST, group));
                continue;
            }
            if (userManager.get(username).getMemberOf().contains(group)) {
                createProtocolEntry(username, String.format(USER_PART_OF_GROUP_ALREADY, group));
            }
            user.getMemberOf().add(group);
            userManager.modify(user);
            createProtocolEntry(username, String.format(USER_ADDED, group));
        }
    }

    public void createProtocolEntry(String username, String content) {
        protocolWriter.writeLine(username + ": " + content);

    }

    public void createProtocolEntry(String content) {
        protocolWriter.writeLine(content);
    }
}
