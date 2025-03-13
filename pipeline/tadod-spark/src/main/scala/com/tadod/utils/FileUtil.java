package com.tadod.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class FileUtil {
    public static Properties readConfig(String path) {
        Properties prop = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get(path))) {
            prop.load(input);
            return prop;
        } catch (IOException e) {
            System.out.println("Error reading properties file: " + e.getMessage());
        }
        return prop;
    }
}
