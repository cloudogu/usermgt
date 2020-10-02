package de.triology.universeadm.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Configuration{

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private final String defaultPath = "/var/lib/usermgt/conf";
    private final String path =  System.getenv("universeadm.home") != null ? System.getenv("universeadm.home") : defaultPath;
    private static Configuration instance;
    private String content;

    private Configuration () {
        this.content = this.readConfigurationFromFile(this.path+"/optional.conf");
    }

    public static Configuration getInstance(){
        if (instance == null){
            instance = new Configuration();
        }
        return instance;
    }

    public String getContent(){
        return this.content;
    }

    private String readConfigurationFromFile(String path){
        String configuration = "";
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            //using string builder because the compiler can't optimize concat in loops
            StringBuilder sb = new StringBuilder();
            while (myReader.hasNextLine()) {
                sb.append(myReader.nextLine());
            }
            configuration = sb.toString();
            myReader.close();
        } catch (FileNotFoundException e) {
            logger.warn("configuration file to setup the password policy not found: No password policy enabled");
            logger.warn(e.getMessage());
        }
        return configuration;
    }


}
