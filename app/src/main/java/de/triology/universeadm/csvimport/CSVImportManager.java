package de.triology.universeadm.csvimport;

import com.google.inject.Inject;
import de.triology.universeadm.BootstrapContextListener;
import de.triology.universeadm.configuration.ApplicationConfiguration;
import de.triology.universeadm.configuration.I18nConfiguration;
import de.triology.universeadm.group.GroupManager;
import de.triology.universeadm.mail.MailSender;
import de.triology.universeadm.user.PasswordGenerator;
import de.triology.universeadm.user.User;
import de.triology.universeadm.user.UserManager;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final String MASKED = "#";
    private final GroupManager groupManager;
    private final UserManager userManager;
    private final ProtocolWriter.ProtocolWriterBuilder protocolWriterBuilder;
    private final MailSender mailSender;
    private final PasswordGenerator pwdGen;

    private final I18nConfiguration languageConfiguration;
    private final ApplicationConfiguration applicationConfiguration;

    private static final Logger logger = LoggerFactory.getLogger(BootstrapContextListener.class);

    @Inject
    public CSVImportManager(UserManager userManager, GroupManager groupManager, ProtocolWriter.ProtocolWriterBuilder protocolWriter, MailSender mailSender, PasswordGenerator pwdGen, I18nConfiguration languageConfig, ApplicationConfiguration applicationConfiguration) {
        this.pwdGen = pwdGen;
        this.userManager = userManager;
        this.groupManager = groupManager;
        this.protocolWriterBuilder = protocolWriter;
        this.mailSender = mailSender;
        this.languageConfiguration = languageConfig;
        this.applicationConfiguration = applicationConfiguration;
    }

    private static final String USER_IMPORT_FILE_NAME = "/var/lib/usermgt/protocol/user-import-protocol";

    public void importUsers(InputStream inputStream) throws IOException {

        ProtocolWriter writer = protocolWriterBuilder.build(USER_IMPORT_FILE_NAME + LocalDateTime.now());

        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        final List<String> lines = new ArrayList<>();
        String line;
        if (br.readLine().split(SEMICOLON_SPLIT).length != 6) {
            throw new IllegalArgumentException();
        }
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }

        createProtocolEntry(writer, languageConfiguration.getDisplay().getStartingProtocol());
        createProtocolEntry(writer, String.format(languageConfiguration.getDisplay().getCsvWithLinesReadSuccessful(), lines.size()));
        logger.info(String.format(languageConfiguration.getSystem().getCsvWithLinesReadSuccessful(), lines.size()));

        for (int index = 0; index < lines.size(); index++) {
            int currentCSVLineIndex = index + 2;
            final String userDataString = lines.get(index);
            final String[] userValues = userDataString.split(SEMICOLON_SPLIT, -1);
            if (userValues.length != 6 && userValues.length != 5) {
                createProtocolEntry(writer, String.format(languageConfiguration.getDisplay().getIncompleteLine(), currentCSVLineIndex));
                logger.info(String.format(languageConfiguration.getSystem().getIncompleteLine(), currentCSVLineIndex));
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
                String errorMessage = String.format(languageConfiguration.getDisplay().getErrorOnCreatingUser(), currentCSVLineIndex);
                logger.info(String.format(languageConfiguration.getSystem().getErrorOnCreatingUser(), currentCSVLineIndex));
                errorMessage += String.join(", ", validationErrors);
                createProtocolEntry(writer, errorMessage);
                continue;
            }

            boolean created = addUser(writer, potentialNewUser);
            if (created) {
                try {
                    mailSender.sendMail(applicationConfiguration.getImportMailSubject(), String.format(applicationConfiguration.getImportMailContent(), potentialNewUser.getUsername(), password), potentialNewUser.getMail());
                } catch (MessagingException e) {
                    createProtocolEntry(writer, potentialNewUser.getUsername(), languageConfiguration.getDisplay().getCouldNotSendMail());
                    logger.info(languageConfiguration.getSystem().getCouldNotSendMail());
                }

            }
            User user = userManager.get(potentialNewUser.getUsername());
            if (user != null) {
                addGroupsToUser(writer, user, memberOf);
            }
            createProtocolEntry(writer, languageConfiguration.getDisplay().getEndingProtocol());
            writer.close();
        }
    }


    private List<String> getUserValidationErrors(final User user) {
        List<String> result = new ArrayList<>();
        if (user.getUsername().equals(EMPTY_STRING)) {
            result.add(languageConfiguration.getDisplay().getEmptyUsername());
        }
        if (user.getDisplayName().equals(EMPTY_STRING)) {
            result.add(languageConfiguration.getDisplay().getEmptyDisplayname());
        }
        if (user.getSurname().equals(EMPTY_STRING)) {
            result.add(languageConfiguration.getDisplay().getEmptySurname());
        }
        if (user.getMail().equals(EMPTY_STRING)) {
            result.add(languageConfiguration.getDisplay().getEmptyMail());
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
                false,
                new ArrayList<>()

        );
    }

    private boolean addUser(ProtocolWriter writer, User user) {
        String username = user.getUsername();
        if (userManager.get(username) != null) {
            createProtocolEntry(writer, username, languageConfiguration.getDisplay().getUserAlreadyExists());
            logger.info(languageConfiguration.getSystem().getUserAlreadyExists());
            return false;
        }

        try {
            userManager.create(user);
        } catch (Exception e) {
            if (e.getMessage().contains("UNIQUE_EMAIL")) {
                createProtocolEntry(writer, username, languageConfiguration.getDisplay().getUniqueMailError());
                logger.info(languageConfiguration.getSystem().getUniqueMailError());
            } else {
                createProtocolEntry(writer, username, languageConfiguration.getDisplay().getUnknownError());
                logger.info(languageConfiguration.getSystem().getUnknownError());
            }
        }

        createProtocolEntry(writer, username, String.format(languageConfiguration.getDisplay().getAddedSuccessful(),
                user.getUsername(),
                user.getGivenname(),
                user.getSurname(),
                user.getDisplayName(),
                user.getMail()));

        logger.info(String.format(languageConfiguration.getSystem().getAddedSuccessful(),
                user.getUsername(),
                MASKED,
                MASKED,
                MASKED,
                MASKED,
                MASKED));
        return true;
    }

    private void addGroupsToUser(ProtocolWriter writer, User user, List<String> memberOf) {
        String username = user.getUsername();
        for (String group : memberOf) {
            if (groupManager.get(group) == null) {
                createProtocolEntry(writer, username, String.format(languageConfiguration.getDisplay().getGroupDoesNotExist(), group));
                logger.info(String.format(languageConfiguration.getSystem().getGroupDoesNotExist(), group));
                continue;
            }

            if (userManager.get(username).getMemberOf().contains(group)) {
                createProtocolEntry(writer, username, String.format(languageConfiguration.getDisplay().getUserPartOfGroupAlready(), group));
                logger.info(String.format(languageConfiguration.getSystem().getUserPartOfGroupAlready(), group));
                continue;
            }
            user.getMemberOf().add(group);
            createProtocolEntry(writer, username, String.format(languageConfiguration.getDisplay().getUserAdded(), group));
            logger.info(String.format(languageConfiguration.getSystem().getUserAdded(), group));
        }
        userManager.modify(user);
    }

    private void createProtocolEntry(ProtocolWriter writer, String username, String content) {
        createProtocolEntry(writer, username + ": " + content);

    }

    private void createProtocolEntry(ProtocolWriter writer, String content) {
        writer.writeLine(content);
    }

}
