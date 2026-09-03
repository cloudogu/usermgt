package de.triology.universeadm.dogu;

public class Dogu {
    private final String name;
    private final String displayName;

    public Dogu(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }
}
