package de.triology.universeadm.csvimport;

import com.google.inject.Inject;
import de.triology.universeadm.ConstraintViolationException;
import de.triology.universeadm.configreader.LanguageConfigReader;
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
    private static final String ERROR_INFORMATION_WITH_COMMA = "%s, ";
//    private String STARTING_PROTOCOL;
//    private String ENDING_PROTOCOL;
//    private String MAIL_CONTENT;
//    private String USER_PART_OF_GROUP_ALREADY;
//    private String reader.get("userAdded");
//    private String GROUP_DOESNT_EXIST;
//    private String ADDED_SUCCESSFUL;
//    private String USER_ALREADY_EXISTS;
//    private String EMPTY_USERNAME;
//    private String EMPTY_DISPLAYNAME;
//    private String EMPTY_SURNAME;
//    private String EMPTY_MAIL;
//    private String reader.get("couldNotSendMail");
//    private String SUBJECT;
//    private String reader.get("errorOnCreatingUser");
//    private String CSV_WITH_LINES_READ_SUCCESSFUL;
//    private String INCOMPLETE_LINE;

    private final GroupManager groupManager;
    private final UserManager userManager;
    private final ProtocolWriter protocolWriter;
    private final MailSender mailSender;
    private final PasswordGenerator pwdGen;
    private final LanguageConfigReader reader;

    @Inject
    public CSVImportManager(UserManager userManager, GroupManager groupManager, ProtocolWriter protocolWriter, MailSender mailSender, PasswordGenerator pwdGen, LanguageConfigReader languageConfig) {
        this.pwdGen = pwdGen;
        this.userManager = userManager;
        this.groupManager = groupManager;
        this.protocolWriter = protocolWriter;
        this.mailSender = mailSender;
        this.reader = languageConfig;
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
        createProtocolEntry(reader.get("startingProtocol"));
        createProtocolEntry(String.format(reader.get("csvWithLinesReadSuccessful"), lines.size()));

        for (int index = 0; index < lines.size(); index++) {
            final String userDataString = lines.get(index);
            final String[] userValues = userDataString.split(SEMICOLON_SPLIT, -1);
            if (userValues.length != 6 && userValues.length != 5) {
                createProtocolEntry(String.format(reader.get("incompleteLine"), (index + 2)));
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
                String errorMessage = String.format(reader.get("errorOnCreatingUser"), (index + 2));
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
                    mailSender.sendMail(reader.get("subject"), String.format(reader.get("mailContent"), potentialNewUser.getUsername(), password), potentialNewUser.getMail());
                } catch (MessagingException e) {
                    createProtocolEntry(potentialNewUser.getUsername(), reader.get("couldNotSendMail"));
                }
            }

            addGroupsToUser(potentialNewUser, memberOf);

            createProtocolEntry(reader.get("endingProtocol"));
        }
    }


    private List<String> getUserValidationErrors(final User user) {
        List<String> result = new ArrayList<>();
        if (user.getUsername().equals(EMPTY_STRING)) {
            result.add(reader.get("emptyUsername"));
        }
        if (user.getDisplayName().equals(EMPTY_STRING)) {
            result.add(reader.get("emptyDisplayname"));
        }
        if (user.getSurname().equals(EMPTY_STRING)) {
            result.add(reader.get("emptySurname"));
        }
        if (user.getMail().equals(EMPTY_STRING)) {
            result.add(reader.get("emptyMail"));
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
            createProtocolEntry(username, reader.get("userAlreadyExists"));
            return false;
        }
        try {
            userManager.create(user);
        } catch (Exception e) {
            if (e.getMessage().contains("UNIQUE_EMAIL")) {
                createProtocolEntry(username, reader.get("uniqueMailError"));
            } else {
                createProtocolEntry(username, reader.get("unknownError"));
            }
        }

        createProtocolEntry(username, String.format(reader.get("addedSuccessful"),
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
                createProtocolEntry(username, String.format(reader.get("groupDoesntExist"), group));
                continue;
            }
            if (userManager.get(username).getMemberOf().contains(group)) {
                createProtocolEntry(username, String.format(reader.get("userPartOfGroupAlready"), group));
            }
            user.getMemberOf().add(group);
            userManager.modify(user);
            createProtocolEntry(username, String.format(reader.get("userAdded"), group));
        }
    }

    public void createProtocolEntry(String username, String content) {
        protocolWriter.writeLine(username + ": " + content);

    }

    public void createProtocolEntry(String content) {
        protocolWriter.writeLine(content);
    }
}
