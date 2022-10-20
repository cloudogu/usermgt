package de.triology.universeadm.user;

import com.google.inject.Inject;
import de.triology.universeadm.group.GroupManager;

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
    public CSVImportManager(UserManager userManager, GroupManager groupManager, ProtocolWriter protocolWriter) {
        this.userManager = userManager;
        this.groupManager = groupManager;
        this.protocolWriter = protocolWriter;
    }

    public void importUsers(InputStream inputStream) throws IOException {

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
        createProtocolEntry("CSV-Datei mit " + lines.size() + " Zeilen erfolgreich eingelesen.");

        //create user for each Line
        for (int index = 0; index < lines.size(); index++) {
            final String userDataString = lines.get(index);
            final String[] userValues = userDataString.split(";", -1);
            if (userValues.length != 6 && userValues.length != 5) {
                createProtocolEntry("Zeile " + (index + 2) + " ist unvollständig");
                continue;
            }

            final List<String> memberOf = new ArrayList<>();
            if (userValues.length == 6) {
                memberOf.addAll(Arrays.asList(userValues[5].split(",")));
            }

            final User potentialNewUser = createUser(userValues);

            final List<String> validationErrors = getUserValidationErrors(potentialNewUser);

            if (validationErrors.size() != 0) {
                String errorMessage = "Fehler in Zeile " + (index + 2) + " Benutzer nicht angelegt. Errors: ";
                for (String error : validationErrors) {
                    errorMessage += error + ", ";
                }
                if (errorMessage.endsWith(", ")) {
                    errorMessage = errorMessage.substring(0, errorMessage.length() - 2);
                }
                createProtocolEntry(errorMessage);
                continue;
            }

            //adding User
            addUser(potentialNewUser);


            //Adding Groups
            addGroupsToUser(potentialNewUser, memberOf);

            //send email with credentials
            //postfix dogu dependency
            //mail in java versenden
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

    private void addUser(User user) {
        String username = user.getUsername();
        if (!(userManager.get(username) == null)) {
            createProtocolEntry(username, "konnte nicht angelegt werden(Nutzer existiert bereits)");
        } else {
            userManager.create(user);
            createProtocolEntry(username, "erfolgreich angelegt(" + user.getUsername() + "," + user.getGivenname() + "," + user.getSurname() + "," + user.getDisplayName() + "," + user.getMail() + ")");
        }
    }

    private void addGroupsToUser(User user, List<String> memberOf) {
        String username = user.getUsername();
        for (String group : memberOf) {
            if (groupManager.get(group) == null) {
                createProtocolEntry(username, group + " existiert nicht");
                continue;
            }
            if (userManager.get(username).getMemberOf().contains(group)) {
                createProtocolEntry(username, "Nutzer ist bereits Teil von " + group);
            }
            user.getMemberOf().add(group);
            userManager.modify(user);
            createProtocolEntry(username, group + " zugeordnet");
        }
    }

    public void createProtocolEntry(String username, String content) {
        try {
            protocolWriter.writeLine(username + ": " + content);
            System.out.println(content);
        } catch (IOException e) {
            System.out.println("Line broken");
        }
    }

    public void createProtocolEntry(String content) {
        try {
            protocolWriter.writeLine(content);
            System.out.println(content);
        } catch (IOException e) {
            System.out.println("Line broken");
        }
    }
}
