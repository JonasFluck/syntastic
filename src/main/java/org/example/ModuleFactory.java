package org.example;

import org.example.concepts.Module;
import org.example.concepts.Parameters;
import org.example.concepts.Snp;
import org.example.modules.baseModules.ModuleBaseAttributes;
import org.example.modules.extensionModules.Epilepsy.ModuleAttributes;
import org.example.modules.extensionModules.Epilepsy.ModuleDrugsEpilepsy;
import org.example.modules.extensionModules.Epilepsy.ModuleEpilepsy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModuleFactory {
    private final Parameters parameters;
    public ModuleFactory(Parameters parameters) {
        this.parameters = parameters;
    }

    private List<Snp> generateDistinctSnpList(Map<String, Map<String, Snp>> patientData){
        List<Snp> snps = patientData.values().stream()
                .flatMap(innerMap -> innerMap.values().stream())
                .distinct()
                .toList();
        return snps;
    }

    public List<Module> generateModules(List<Class<? extends Module>> moduleClasses){
        List<Module> modules = new ArrayList<>();
        for(Class<? extends Module> moduleClass : moduleClasses){
            if (moduleClass == ModuleBaseAttributes.class){
                modules.add(new ModuleBaseAttributes.Builder()
                        .setMinAge(parameters.minAge)
                        .setMaxAge(parameters.maxAge)
                        .setGenders(parameters.gender)
                        .setCountries(parameters.countryList)
                        .build());
            }
            if(moduleClass == ModuleEpilepsy.class){
                modules.add(new ModuleEpilepsy.Builder()
                        .setEpilepsyTrait("config/traitFiles/Epilepsy.txt")
                        .setModuleDrugsEpilepsy(
                                new ModuleDrugsEpilepsy.Builder()
                                        .setMaxDrugs(parameters.maxDrugs)
                                        .setSnps(generateDistinctSnpList(parameters.patientData).stream().distinct().toList()) // Convert back to a List
                                        .setBaseDrugEffectiveness(parameters.baseDrugEffectiveness)
                                        .setNegativePriorDrugEvent(parameters.negativePriorDrugEvent)
                                        .setPositivePriorDrugEvent(parameters.positivePriorDrugEvent)
                                        .setSnpsPerDrugType(parameters.snpsPerDrugType)
                                        .setPercentageOfSnpsForDrugPerDrugType(parameters.percentageOfSnpsForDrugPerDrugType)
                                        .build()
                        )
                        .build()
                );
            }
            if(moduleClass == ModuleAttributes.class){
                modules.add(new ModuleAttributes.Builder().setOddsRatios(ModuleAttributes.loadAttributes("src/main/resources/config/epilepsy_odds_ratios.json")).build());
            }
        }
        return modules;
    }
}
