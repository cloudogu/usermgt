package de.triology.universeadm.user;

import com.google.inject.Inject;
import de.triology.universeadm.group.GroupManager;
import org.joda.time.LocalDateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CSVImportManager {
    //~--- fields ---------------------------------------------------------------

    /**
     * Field description
     */
    private final GroupManager groupManager;

    /**
     * Field description
     */
    private final UserManager userManager;

    /**
     * Field description
     */
    private final ProtocolWriter protocolWriter;

    /**
     * Constructs ...
     *
     * @param userManager
     * @param groupManager
     */
    @Inject
    public CSVImportManager(UserManager userManager, GroupManager groupManager) {
        this.userManager = userManager;
        this.groupManager = groupManager;
        this.protocolWriter = new ProtocolWriter("/var/lib/usermgt/protocol/user-import-protocol");
    }

    public void importUsers(InputStream inputStream) throws IOException {


        //protokoll fehlt, aktuell mit sysout
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> lines = new ArrayList<>();

        //Format Check
        if (br.readLine().split(";").length != 6) {
            throw new IllegalArgumentException();
        }

        //Read all Lines
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        System.out.println(createProtocolEntry("CSV-Datei mit " + lines.size() + " Zeilen erfolgreich eingelesen."));

        //create user for each Line
        for (int index = 0; index < lines.size(); index++) {
            final String userDataString = lines.get(index);
            final String[] userValues = userDataString.split(";");

            if (userValues.length != 6 && userValues.length != 5) {
                System.out.println(createProtocolEntry("Zeile " + index + 1 + " ist unvollständig"));
                continue;
            }

            final List<String> memberOf = new ArrayList<>();
            if (userValues.length == 6) {
                memberOf.addAll(Arrays.asList(userValues[5].split(",")));
            }

            final User potentialNewUser = createUser(userValues);

            final List<String> validationErrors = getUserValidationErrors(potentialNewUser);

            if (validationErrors.size() != 0) {
                String errorMessage = "Fehler in Zeile " + index + " Benutzer nicht angelegt. Errors: ";
                for (String error : validationErrors) {
                    errorMessage += error + ", ";
                }
                if (errorMessage.endsWith(", ")) {
                    errorMessage = errorMessage.substring(0, errorMessage.length() - 2);
                }
                System.out.println(createProtocolEntry(errorMessage));
                continue;
            }

            //adding User
            String userResult = addUser(potentialNewUser);
            System.out.println(userResult);

            //Adding Groups
            String groupsResult = addGroupsToUser(potentialNewUser, memberOf);
            System.out.println(groupsResult);

            //send email with credentials
        }
    }

    private List<String> getUserValidationErrors(final User user) {
        List<String> result = new ArrayList<>();
        if (user.getUsername().equals("")) {
            result.add("Nutzername ist leer");
        }
        if (user.getDisplayName().equals("")) {
            result.add("DisplayName ist leer");
        }
        if (user.getSurname().equals("")) {
            result.add("Surname ist leer");
        }
        if (user.getMail().equals("")) {
            result.add("Mail ist leer");
        }
        return result;
    }

    private User createUser(final String[] userValues) {
        final String password = PasswordGenerator.random(10);
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

    private String addUser(User user) {
        String username = user.getUsername();
        if (!(userManager.get(username) == null)) {
            return (createProtocolEntry(username, "konnte nicht angelegt werden(Nutzer existiert bereits)"));
        } else {
            userManager.create(user);
            return (createProtocolEntry(username, "erfolgreich angelegt(" + user.getUsername() + "," + user.getGivenname() + "," + user.getSurname() + "," + user.getDisplayName() + "," + user.getMail() + ")"));
        }
    }

    private String addGroupsToUser(User user, List<String> memberOf) {
        String result = "";
        String username = user.getUsername();
        for (String group : memberOf) {
            if (groupManager.get(group) == null) {
                result += createProtocolEntry(username, group + " existiert nicht\n");
                continue;
            }
            if (user.getMemberOf().contains(group)) {
                result += createProtocolEntry(username, "Nutzer ist bereits Teil von " + group + "\n");
            }
            user.getMemberOf().add(group);
            userManager.modify(user);
            result += createProtocolEntry(username, group + " zugeordnet\n");
        }
        return result;
    }

    public String createProtocolEntry(String username, String content) {
        return LocalDateTime.now() + ":" + username + ": " + content;
    }

    public String createProtocolEntry(String content) {
        return LocalDateTime.now() + ": " + content;
    }

}
