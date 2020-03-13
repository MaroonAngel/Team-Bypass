package com.github.maroonangel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BypassConfig {

    public List<String> bypassTeams = new ArrayList<String>();
    private static final Logger LOGGER = (Logger) LogManager.getLogger();

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    // Load the config file
    public static BypassConfig loadConfig(File file) {
        BypassConfig config;

        if (file.exists()) {
            try (FileReader fileReader = new FileReader(file)){
                config = gson.fromJson(fileReader, BypassConfig.class);
                LOGGER.info("[TeamBypass] Config file loaded successfully.");
            } catch (IOException e) {
                throw new RuntimeException("[TeamBypass] An error occured loading config:", e);
            }
        }
        else {
            LOGGER.error("[TeamBypass] File does not exist.");
            config = new BypassConfig();
        }
        config.saveConfig(file);
        return config;
    }

    // Save the config file
    public void saveConfig(File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            gson.toJson(this, fileWriter);
            LOGGER.info("[TeamBypass] Config file saved.");
        } catch (IOException e) {
            LOGGER.error("[TeamBypass] An error occured saving config:", e);
        }
    }
}
