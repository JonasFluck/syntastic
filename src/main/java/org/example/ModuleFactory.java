package org.example;

import org.example.concepts.Module;
import org.example.concepts.Parameters;
import org.example.concepts.attributes.Snp;
import org.example.modules.baseModules.ModuleBaseAttributes;
import org.example.modules.extensionModules.Epilepsy.ModuleAttributes;
import org.example.modules.extensionModules.Epilepsy.ModuleDrugsEpilepsy;
import org.example.modules.extensionModules.Epilepsy.ModuleEpilepsy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModuleFactory {
    private final Parameters parameters;
    public ModuleFactory(Parameters parameters) {
        this.parameters = parameters;
    }

    private List<Snp> generateDistinctSnpList(Map<String, Map<String, Snp>> patientData){
        return patientData.values().stream()
                .flatMap(innerMap -> innerMap.values().stream())
                .distinct()
                .toList();
    }

    public List<Module> generateModules(List<Class<? extends Module>> moduleClasses){
        List<Module> modules = new ArrayList<>();
        for(Class<? extends Module> moduleClass : moduleClasses){
            if (moduleClass == ModuleBaseAttributes.class){
                ModuleBaseAttributes.Builder moduleAttributes = new ModuleBaseAttributes.Builder();
                if(parameters.minAge != null) moduleAttributes.setMinAge(parameters.minAge);
                if(parameters.maxAge != null) moduleAttributes.setMaxAge(parameters.maxAge);
                moduleAttributes.setCountries(parameters.countryList);
                moduleAttributes.setGenders(parameters.gender);
                modules.add(moduleAttributes.build());
            }
            if(moduleClass == ModuleEpilepsy.class){
                ModuleEpilepsy.Builder moduleEpilepsyBuilder = new ModuleEpilepsy.Builder();
                moduleEpilepsyBuilder.setEpilepsyTrait("config/traitFiles/Epilepsy.txt");

                ModuleDrugsEpilepsy.Builder moduleDrugsEpilepsyBuilder = new ModuleDrugsEpilepsy.Builder();
                moduleDrugsEpilepsyBuilder.setSnps(generateDistinctSnpList(parameters.patientData));
                if(parameters.maxDrugs != null) moduleDrugsEpilepsyBuilder.setMaxDrugs(parameters.maxDrugs);
                if(parameters.baseDrugEffectiveness != null) moduleDrugsEpilepsyBuilder.setBaseDrugEffectiveness(parameters.baseDrugEffectiveness);
                if(parameters.negativePriorDrugEvent != null) moduleDrugsEpilepsyBuilder.setNegativePriorDrugEvent(parameters.negativePriorDrugEvent);
                if(parameters.positivePriorDrugEvent != null) moduleDrugsEpilepsyBuilder.setPositivePriorDrugEvent(parameters.positivePriorDrugEvent);
                if(parameters.snpsPerDrugType != null) moduleDrugsEpilepsyBuilder.setSnpsPerDrugType(parameters.snpsPerDrugType);
                if(parameters.percentageOfSnpsForDrugPerDrugType != null) moduleDrugsEpilepsyBuilder.setPercentageOfSnpsForDrugPerDrugType(parameters.percentageOfSnpsForDrugPerDrugType);
                if(parameters.positiveResponseAnotherDrug != null) moduleDrugsEpilepsyBuilder.setPositiveResponseAnotherDrug(parameters.positiveResponseAnotherDrug);
                if(parameters.negativeResponseAnotherDrug != null) moduleDrugsEpilepsyBuilder.setNegativeResponseAnotherDrug(parameters.negativeResponseAnotherDrug);

                moduleEpilepsyBuilder.setModuleDrugsEpilepsy(moduleDrugsEpilepsyBuilder.build());
                modules.add(moduleEpilepsyBuilder.build());
            }
            if(moduleClass == ModuleAttributes.class){
                modules.add(new ModuleAttributes.Builder().setOddsRatios(ModuleAttributes.loadAttributes("config/epilepsy_odds_ratios.json")).build());
            }
        }
        return modules;
    }
}
