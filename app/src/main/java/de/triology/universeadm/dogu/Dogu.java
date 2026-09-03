package de.triology.universeadm.dogu;

import java.util.List;

public class Dogu {
    private final String name;
    private final String displayName;
    private final List<String> tags;

    public Dogu(String name, String displayName, List<String> tags) {
        this.name = name;
        this.displayName = displayName;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getTags() {
        return tags;
    }
}
