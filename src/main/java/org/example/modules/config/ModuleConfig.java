package org.example.modules.config;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ModuleConfig {
    private List<String> activeModules;

    public static ModuleConfig loadConfig(String configFilePath) {
        try (Reader reader = new InputStreamReader(ModuleConfig.class.getClassLoader().getResourceAsStream(configFilePath))) {
            if (reader == null) {
                throw new IllegalArgumentException("Config file not found: " + configFilePath);
            }
            return new Gson().fromJson(reader, ModuleConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new ModuleConfig();  // Return an empty config in case of failure
        }
    }

    public List<String> getActiveModules() {
        return activeModules == null ? new ArrayList<>() : activeModules;
    }

    public void setActiveModules(List<String> activeModules) {
        this.activeModules = activeModules;
    }
}
