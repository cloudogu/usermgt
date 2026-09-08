package de.triology.universeadm.dogu;

import java.util.List;

public class Dogu {
    private final String name;
    private final String displayName;
    private final List<String> tags;
    private final String category;

    public Dogu(String name, String displayName, List<String> tags, String category) {
        this.name = name;
        this.displayName = displayName;
        this.tags = tags;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getTags() {
        return tags;
    }
}
