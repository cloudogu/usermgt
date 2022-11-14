package de.triology.universeadm.configuration;

public enum Language {
    de("de"),
    en("en");

    private final String language;

    private static final String defaultLanguage = "EN";

    Language(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        if (this.language == null || language.isEmpty()) {
            return defaultLanguage;
        }
        return language;
    }
}
