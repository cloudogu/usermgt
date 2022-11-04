package de.triology.universeadm.csvimport;

import com.google.inject.Inject;
import de.triology.universeadm.configuration.ApplicationConfiguration;
import de.triology.universeadm.configuration.LanguageConfiguration;
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
    private final GroupManager groupManager;
    private final UserManager userManager;
    private final ProtocolWriter protocolWriter;
    private final MailSender mailSender;
    private final PasswordGenerator pwdGen;

    private final LanguageConfiguration languageConfiguration;
    private final ApplicationConfiguration applicationConfiguration;

    @Inject
    public CSVImportManager(UserManager userManager, GroupManager groupManager, ProtocolWriter protocolWriter, MailSender mailSender, PasswordGenerator pwdGen, LanguageConfiguration languageConfig, ApplicationConfiguration applicationConfiguration) {
        this.pwdGen = pwdGen;
        this.userManager = userManager;
        this.groupManager = groupManager;
        this.protocolWriter = protocolWriter;
        this.mailSender = mailSender;
        this.languageConfiguration = languageConfig;
        this.applicationConfiguration = applicationConfiguration;
    }

    public void importUsers(InputStream inputStream) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        final List<String> lines = new ArrayList<>();
        String line;
        if (br.readLine().split(SEMICOLON_SPLIT).length != 6) {
            throw new IllegalArgumentException();
        }
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }

        createProtocolEntry(languageConfiguration.getStartingProtocol());
        createProtocolEntry(String.format(languageConfiguration.getCsvWithLinesReadSuccessful(), lines.size()));

        for (int index = 0; index < lines.size(); index++) {
            int currentCSVLineIndex = index + 2;
            final String userDataString = lines.get(index);
            final String[] userValues = userDataString.split(SEMICOLON_SPLIT, -1);
            if (userValues.length != 6 && userValues.length != 5) {
                createProtocolEntry(String.format(languageConfiguration.getIncompleteLine(), currentCSVLineIndex));
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
                String errorMessage = String.format(languageConfiguration.getErrorOnCreatingUser(), currentCSVLineIndex);
                errorMessage += String.join(", ", validationErrors);
                createProtocolEntry(errorMessage);
                continue;
            }

            boolean created = addUser(potentialNewUser);
            if (created) {
                try {
                    mailSender.sendMail(applicationConfiguration.getImportMailSubject(), String.format(applicationConfiguration.getImportMailContent(), potentialNewUser.getUsername(), password), potentialNewUser.getMail());
                } catch (MessagingException e) {
                    createProtocolEntry(potentialNewUser.getUsername(), languageConfiguration.getCouldNotSendMail());
                }
            }

            addGroupsToUser(potentialNewUser, memberOf);
            createProtocolEntry(languageConfiguration.getEndingProtocol());
        }
    }


    private List<String> getUserValidationErrors(final User user) {
        List<String> result = new ArrayList<>();
        if (user.getUsername().equals(EMPTY_STRING)) {
            result.add(languageConfiguration.getEmptyUsername());
        }
        if (user.getDisplayName().equals(EMPTY_STRING)) {
            result.add(languageConfiguration.getEmptyDisplayname());
        }
        if (user.getSurname().equals(EMPTY_STRING)) {
            result.add(languageConfiguration.getEmptySurname());
        }
        if (user.getMail().equals(EMPTY_STRING)) {
            result.add(languageConfiguration.getEmptyMail());
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
        if (userManager.get(username) != null) {
            createProtocolEntry(username, languageConfiguration.getUserAlreadyExists());
            return false;
        }

        try {
            userManager.create(user);
        } catch (Exception e) {
            if (e.getMessage().contains("UNIQUE_EMAIL")) {
                createProtocolEntry(username, languageConfiguration.getUniqueMailError());
            } else {
                createProtocolEntry(username, languageConfiguration.getUnknownError());
            }
        }

        createProtocolEntry(username, String.format(languageConfiguration.getAddedSuccessful(),
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
                createProtocolEntry(username, String.format(languageConfiguration.getGroupDoesNotExist(), group));
                continue;
            }

            if (userManager.get(username).getMemberOf().contains(group)) {
                createProtocolEntry(username, String.format(languageConfiguration.getUserPartOfGroupAlready(), group));
            }

            user.getMemberOf().add(group);
            userManager.modify(user);
            createProtocolEntry(username, String.format(languageConfiguration.getUserAdded(), group));
        }
    }

    private void createProtocolEntry(String username, String content) {
        createProtocolEntry(username + ": " + content);

    }

    private void createProtocolEntry(String content) {
        protocolWriter.writeLine(content);
    }
}
