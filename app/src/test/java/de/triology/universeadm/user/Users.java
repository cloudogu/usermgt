package de.triology.universeadm.user;

import com.google.common.collect.Lists;

/**
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public final class Users {
    private Users() {
    }

    public static User createDent() {
        return new User(
                "dent", "Arthur Dent", "Arthur", "Dent",
                "arthur.dent@hitchhiker24.com", "hitchhiker123", true,
                Lists.newArrayList("Hitchhiker")
        );
    }

    public static User createDent2() {
        return new User(
                "dent2", "Arthur Dent", "Arthur", "Dent",
                "arthur.dent@hitchhiker24.com", "hitchhiker123", false,
                Lists.newArrayList("Hitchhiker")
        );
    }

    public static User createTrillian() {
        return new User(
                "tricia", "Tricia McMillan", "Tricia", "McMillan",
                "tricia.mcmillan-1337@hitchhiker.com", "hitchhiker123", true,
                Lists.newArrayList("Hitchhiker")
        );
    }

    public static User createTrillexterno(){
        return new User(
                "trillexterno", "Triton Trillexterno", "Triton", "Trillexterno",
                "tri.xterno@hitchhiker.com", "hitchhiker123", true,
                Lists.newArrayList("Hitchhiker"), true
        );
    }

    public static User copy(User user) {
        return new User(user.getUsername(), user.getDisplayName(), user.getGivenname(), user.getSurname(),
                user.getMail(), user.getPassword(), user.isPwdReset(), user.getMemberOf(), user.isExternal());
    }
}
