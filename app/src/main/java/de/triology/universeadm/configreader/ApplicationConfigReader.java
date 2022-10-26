package de.triology.universeadm.configreader;

public class ApplicationConfigReader extends JsonConfigReader {
    public ApplicationConfigReader() {
        super("/resources/config.json");
    }
}