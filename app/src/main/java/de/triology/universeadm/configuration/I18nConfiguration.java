package de.triology.universeadm.configuration;

import de.triology.universeadm.BaseDirectory;

public class I18nConfiguration {
    private final LanguageConfiguration systemLanguageConfiguration;
    private final LanguageConfiguration displayLanguageConfiguration;

    public I18nConfiguration(Language systemLanguage, Language displayLanguage) {
        this.systemLanguageConfiguration = getConfiguration(systemLanguage);
        this.displayLanguageConfiguration = getConfiguration(displayLanguage);

    }

    private LanguageConfiguration getConfiguration(Language language) {
        return BaseDirectory.getConfiguration("i18n/"+language.toString()+".xml", LanguageConfiguration.class);
    }

    public LanguageConfiguration getSystem(){
        return systemLanguageConfiguration;
    }

    public LanguageConfiguration getDisplay(){
        return displayLanguageConfiguration;
    }
}
