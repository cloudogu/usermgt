package de.triology.universeadm.configreader;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JsonConfigReader<T> {
    private final String resourceName;
    protected T config;

    public JsonConfigReader(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * @return Instance of the generic type T
     */
    protected T readConfiguration(){
        try {
            InputStream configFileStream = getClass().getClassLoader().getResourceAsStream(this.resourceName);
            return new ObjectMapper().reader(new TypeReference<T>() {}).readValue(configFileStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }


    public T getConfiguration() {
        synchronized(this) {
            if (config == null) {
                config = readConfiguration();
            }
            return config;
        }
    }
}
