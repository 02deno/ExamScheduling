package org.example.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigHelper {
    private static Properties properties;
    private static final Logger logger = LogManager.getLogger(ConfigHelper.class);
    private static final String configPath = "src/main/resources/config.properties";

    static {
        try {
            properties = new Properties();
            InputStream input = ConfigHelper.class.getClassLoader().getResourceAsStream("config.properties");
            properties.load(input);
        } catch (Exception e) {
            logger.error("Error loading config.properties: " + e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public static void saveConfig() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(configPath);
            properties.store(fileOutputStream, null);
            fileOutputStream.close();
        } catch (Exception e) {
            logger.error("Error saving to config.properties: " + e);
        }
    }

}
