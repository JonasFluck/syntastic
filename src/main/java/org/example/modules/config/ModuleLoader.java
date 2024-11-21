package org.example.modules.config;

import org.example.concepts.Module;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ModuleLoader {
    public List<Module> loadModules(String configFilePath) {
        // Load the configuration (active/inactive modules)
        ModuleConfig config = ModuleConfig.loadConfig(configFilePath);
        Set<Class<? extends Module>> allModuleClasses = getModuleClasses();

        // Instantiate all modules
        Set<Module> allModules = allModuleClasses.stream()
                .map(this::instantiateModule)
                .collect(Collectors.toSet());

        // Filter based on active modules in config
        return filterActiveModules(allModules, config);
    }

    private Set<Class<? extends Module>> getModuleClasses() {
        // Using Reflections to find all subclasses of Module
        Reflections reflections = new Reflections("org.example.modules"); // base package
        return reflections.getSubTypesOf(Module.class);
    }

    private Module instantiateModule(Class<? extends Module> moduleClass) {
        try {
            return moduleClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Module> filterActiveModules(Set<Module> modules, ModuleConfig config) {
        // Get active module class names from the configuration
        Set<String> activeModuleNames = new HashSet<>(config.getActiveModules());

        // Filter and return only the active modules
        return modules.stream()
                .filter(module -> activeModuleNames.contains(module.getClass().getName()))
                .collect(Collectors.toList());
    }
}
