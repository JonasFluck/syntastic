package org.example.concepts.handlers;

import org.example.concepts.Parameters;

public class EpilepsyHandler implements ArgumentHandler {
    @Override
    public boolean handle(String name, String value, Parameters parameters) {
        switch (name) {
            case "maxDrugs":
                parameters.maxDrugs = Integer.parseInt(value);
                return true;
            case "snpsPerDrugType":
                parameters.snpsPerDrugType = Integer.parseInt(value);
                return true;
            case "percentageOfSnpsForDrugPerDrugType":
                parameters.percentageOfSnpsForDrugPerDrugType = Integer.parseInt(value);
                return true;
            case "baseDrugEffectiveness":
                parameters.baseDrugEffectiveness = Double.parseDouble(value);
                return true;
            case "negativePriorDrugEvent":
                parameters.negativePriorDrugEvent = Double.parseDouble(value);
                return true;
            case "positivePriorDrugEvent":
                parameters.positivePriorDrugEvent = Double.parseDouble(value);
                return true;
            default:
                return false;
        }
    }
}
