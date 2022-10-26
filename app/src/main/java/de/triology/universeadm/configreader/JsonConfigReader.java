package de.triology.universeadm.configreader;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JsonConfigReader {
    private Map<String, String> values;

    public JsonConfigReader(String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            values = mapper.readValue(Paths.get(filePath).toFile(), Map.class);
        } catch (IOException e) {
            values = new HashMap<>();
            e.printStackTrace();
            System.exit(1);
        }
    }

    public String get(String key) {
        return values.get(key);
    }

}
