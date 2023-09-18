package de.triology.universeadm.user.imports;

import de.triology.universeadm.user.PasswordGenerator;
import de.triology.universeadm.user.User;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collections;
import java.util.Optional;

/**
 * Mapper used decode the DTO for further processing.
 */
public class Mapper {

    private static final PasswordGenerator pwdGen = new PasswordGenerator();
    private static final int PASSWORD_LENGTH = 12;

    /**
     * Decodes the user DTO to a domain user
     * @param userDTOPTriple - is a {@link Pair} containing:
     * <ul>
     *  <li>optional existing user</li>
     *  <li>user dto read from the csv file</li>
     * </ul>
     * @return Triple<Long, Boolean, User>:
     * <ul>
     *   <li>Long - affected line in csv file</li>
     *   <li>Boolean - Flag indication a new user</li>
     *   <li>User - the actual user to be imported</li>
     * </ul>
     */
    public static Triple<Long, Boolean, User> decode(Pair<Optional<User>, CSVUserDTO> userDTOPTriple) {

        CSVUserDTO userDTO = userDTOPTriple.getValue();
        Long linenumber = userDTO.getLineNumber();

        return userDTOPTriple
                .getKey()
                .map(exUser -> modifyUser(exUser, userDTO))
                .map(modifiedUser -> Triple.of(linenumber, false, modifiedUser))
                .orElse(Triple.of(linenumber, true, createNewUser(userDTO)));
    }

    /**
     * Creates a new user. A new password will be generated for this user. Moreover, PasswordReset will be set to false,
     * no matter what option is set in the csv file. The user has no group memberships.
     * At the moment, new users will be treated as external users.
     * @param csvUserDTO - DTO user
     * @return {@link User}
     */
    private static User createNewUser(CSVUserDTO csvUserDTO) {
        return new User(
                csvUserDTO.getUsername(),
                csvUserDTO.getDisplayname(),
                csvUserDTO.getGivenname(),
                csvUserDTO.getSurname(),
                csvUserDTO.getMail(),
                pwdGen.random(PASSWORD_LENGTH),
                false,
                Collections.emptyList(),
                true
        );
    }

    /**
     * Modifies an existing user by overwriting the values from the csv file.
     * @param existingUser - user to be modified
     * @param csvUserDTO - containing new values
     * @return {@link User}
     */
    private static User modifyUser(User existingUser, CSVUserDTO csvUserDTO) {
        existingUser.setDisplayName(csvUserDTO.getDisplayname());
        existingUser.setGivenname(csvUserDTO.getGivenname());
        existingUser.setSurname(csvUserDTO.getSurname());
        existingUser.setMail(csvUserDTO.getMail());
        existingUser.setPwdReset(csvUserDTO.isPwdReset());
        existingUser.setExternal(csvUserDTO.isExternal());

        return existingUser;
    }
}
